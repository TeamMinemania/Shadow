from flask import *
import json
import random

#this controls if all clients launch or not, ONLY turn this off under extreme emergency
ENABLE_CLIENT = True
ACCOUNTS_FILE = "/home/shadows/mysite/static/clients.json"
SERVER_FILE = "/home/shadows/mysite/static/servers.json"
ITEMS_DIR = "/home/shadows/mysite/static/items"


clients = {}

#clients = {"v5ugh2v3jb5mn325":{"mcname":"ssqd","username":"saturn5Vfive", "cape":"https://my.tech/cape.png"}}

app = Flask(__name__)

def save():
    with open(ACCOUNTS_FILE, "w+") as handle:
        handle.write(json.dumps(clients))

def load():
    global clients
    with open(ACCOUNTS_FILE, "r") as handle:
        clients = json.loads(handle.read())

load()

@app.route('/')
def startup_lock():
    if ENABLE_CLIENT:
        return 'TRUE'
    else:
        return 'FALSE'


@app.route('/startup', methods=['POST'])
def start_client():
    data = request.get_json()
    cuid = data['client']
    mcusername = data['username']
    if not cuid in clients.keys():
        clients.setdefault(cuid, {"mcname":mcusername, "username":"DefaultUser" + str(random.randint(0, 9000)), "cape":"https://cdn.discordapp.com/attachments/1002147514281107456/1003087797281902673/unknown.png"})
        resp = make_response("CLIENT_NEEDS_USERNAME", 200)
        resp.mimetype = 'text/plain'
        save()
        return resp
    if cuid in clients.keys():
        clients[cuid]['mcname'] = mcusername
        resp = make_response("CLIENT_OK", 200)
        resp.mimetype = 'text/plain'
        save()
        return resp

@app.route("/username", methods=['POST'])
def edit_username():
    data = request.get_json()
    cuid = data['client']
    name = data['username']
    clients[cuid]['username'] = name
    resp = make_response("CLIENT_OK", 200)
    resp.mimetype = 'text/plain'
    save()
    return resp

@app.route("/username/get", methods=['GET'])
def get_username():
    hwid = request.args.get("hwid")
    name = clients[hwid]['username']
    resp = make_response(name, 200)
    resp.mimetype = 'text/plain'
    return resp

@app.route("/capes/edit", methods=['POST'])
def edit_cape():
    data = request.get_json()
    cuid = data['client']
    cape = data['cape']
    clients[cuid]['cape'] = cape
    resp = make_response("CLIENT_OK", 200)
    resp.mimetype = 'text/plain'
    save()
    return resp

@app.route('/capes/list', methods=['GET'])
def get_capes():
    arr = {}
    for user in clients.values():
        arr.setdefault(user['mcname'], user['cape'])
    save()
    return jsonify(arr)

#START SERVER SCANNER

servers = {"servers":[]}

def save_servers():
  with open(SERVER_FILE, "w") as serversf:
      serversf.write(json.dumps(servers))

def load_servers():
  global servers
  with open(SERVER_FILE) as servers:
    ebuffer = json.load(servers)
    servers = ebuffer

load_servers()

@app.route('/servers/search', methods=['POST'])
def search_servers():
#{"item":"", "includes":""}
    try:
      data = request.get_json()
      secret = data["secret"]
      if not secret == "molekey0":
        return jsonify({"status": "INVALID_SECRET"})
      searchterm = data["motd"]
      version = data["version"]
      bufferreturner = {"servers": []}
      static = servers["servers"]
      for iterator in static:
        if searchterm in iterator["motd"] and version in iterator["version"]:
          if data["players"] == "needs":
            if iterator["players"] < 1:
              continue
          reader = bufferreturner.get("servers")
          reader.append(iterator["ip"])
          bufferreturner.update({"servers": reader})
      reader = bufferreturner.get("servers")
      random.shuffle(reader)
      bufferreturner.update({"servers": reader})
      return jsonify(bufferreturner)
    except Exception as e:
      print(e)
      return jsonify({"status": "ERROR"})

s_counter = 0

@app.route('/servers/metrics')
def server_metrics():
    return jsonify({"amount":len(servers['servers'])})

@app.route('/servers/add', methods=['POST'])
def add_server():
    global s_counter
    try:
        data = request.get_json()
        motd = data["motd"]
        secret = data["secret"]
        if not secret == "molekey0":
          return jsonify({"status": "INVALID_SECRET"})
        ip = data["ip"]
        version = data["version"]
        players = data["players"]
        print(f"Got a new server entry: {request.remote_addr}\nMOTD: {motd}\nIP: {ip}\nVERSION: {version}\nPlayerCount: {players}")
        for item in servers["servers"]:
          if item["ip"] == ip:
            item["motd"] = motd
            item["version"] = version
            item["players"] = players
            s_counter += 1
            return jsonify({"status": "UPDATED"})
        reader = servers.get("servers")
        reader.append({"ip": ip, "motd": motd, "version": version, "players": players})
        servers.update({"servers": reader})
        s_counter += 1
        if s_counter % 100 == 0:
            save_servers()
            print(f"saved data to file!")
        return jsonify({"status": "ADDED"})
    except Exception as e:
        print(e)
        return jsonify({"status": "ERROR"})


#Items api begin
#items = {"items":[{"name":"minecraft:acacia_boat", "count":1, "nbt":"CCBBAKJTBATKTMANNNNTNTNTNTNN"},{"name":"minecraft:acacia_boat", "count":1, "nbt":"CCBBAKJTBATKTMANNNNTNTNTNTNN"}]}

#item = {"name":"minecraft:acacia_boat", "count":1, "nbt":"CCBBAKJTBATKTMANNNNTNTNTNTNN"}

@app.route("/items/grief/add", methods=['POST'])
def grief_items_add():
    data = request.get_json()
    items = None
    with open(ITEMS_DIR +"/grief.json") as handle:
        items = json.load(handle)
        items['items'].append(data)
    with open(ITEMS_DIR + "/grief.json", "w") as handle:
        handle.write(json.dumps(items))
    resp = make_response("ITEM_ADD", 200)
    resp.mimetype = 'text/plain'
    return resp

@app.route("/items/grief/list", methods=['GET'])
def grief_items():
    with open(ITEMS_DIR + "/grief.json") as handle:
        resolve = json.load(handle)
        return jsonify(resolve)

@app.route("/items/exploits/add", methods=['POST'])
def exploits_items_add():
    data = request.get_json()
    items = None
    with open(ITEMS_DIR +"/exploits.json") as handle: 
        items = json.load(handle)
        items['items'].append(data)
    with open(ITEMS_DIR + "/exploits.json", "w") as handle:
        handle.write(json.dumps(items))
    resp = make_response("ITEM_ADD", 200)
    resp.mimetype = 'text/plain'
    return resp

@app.route("/items/exploits/list", methods=['GET'])
def exploits_items():
    with open(ITEMS_DIR + "/exploits.json") as handle:
        resolve = json.load(handle)
        return jsonify(resolve)