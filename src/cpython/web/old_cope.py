from flask import *
import json
import random


def save():
  with open("/home/moleapi/mysite/static/servers.json", "w") as servers:
      servers.write(json.dumps(buffer))

def load():
  with open("/home/moleapi/mysite/static/servers.json") as servers:
    ebuffer = json.load(servers)
    return ebuffer

def capesave():
    with open("/home/moleapi/mysite/static/capes.json") as capesf:
        capes.write(json.dumps(capesf))

def capeload():
  with open("/home/moleapi/mysite/static/capes.json") as capesf:
    ebuffer = json.load(capesf)
    return ebuffer


app = Flask(__name__)
buffer = load()

capes = capeload()

@app.route('/', methods=["GET"])
def index():
  return "404"


@app.route("/found", methods=["POST"])
def postserver():
  try:
    data = request.get_json()
    motd = data["motd"]
    secret = data["secret"]
    if not secret == "molekey0":
      return jsonify({"status": "INVALID_SECRET"})
    ip = data["ip"]
    version = data["version"]
    players = data["players"]
    print(f"Got new server from {request.remote_addr}\nMOTD: {motd}\nIP: {ip}\nVERSION: {version}\nPlayerCount: {players}")
    for item in buffer["servers"]:
      if item["ip"] == ip:
        item["motd"] = motd
        item["version"] = version
        item["players"] = players
        save()
        return jsonify({"status": "UPDATED"})
    reader = buffer.get("servers")
    reader.append({"ip": ip, "motd": motd, "version": version, "players": players})
    buffer.update({"servers": reader})
    save()
    print(f"saved data to file!")
    return jsonify({"status": "ADDED"})
  except Exception as e:
    print(e)
    return jsonify({"status": "ERROR"})

@app.route("/search", methods=["POST"])
def getsearch():
  #{"item":"", "includes":""}
  try:
    data = request.get_json()
    secret = data["secret"]
    if not secret == "molekey0":
      return jsonify({"status": "INVALID_SECRET"})
    searchterm = data["includes"]
    item = data["item"]
    bufferreturner = {"servers": []}
    static = buffer["servers"]
    for iterator in static:
      if searchterm in iterator[item]:
        reader = bufferreturner.get("servers")
        reader.append(iterator["ip"])
        bufferreturner.update({"servers": reader})
    return jsonify(bufferreturner )
  except Exception as e:
    print(e)
    return jsonify({"status": "ERROR"})


@app.route("/clientSearch", methods=["POST"])
def getsearchclient():
  #{"item":"", "includes":""}
  try:
    data = request.get_json()
    secret = data["secret"]
    if not secret == "molekey0":
      return jsonify({"status": "INVALID_SECRET"})
    searchterm = data["motd"]
    version = data["version"]
    bufferreturner = {"servers": []}
    static = buffer["servers"]
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

@app.route("/refresh", methods=["POST", "GET"])
def refresh():
  global buffer
  buffer = load()
  return jsonify({"status":"DONE"})


@app.route("/attach", methods=["POST"])
def addCape():
    try:
        data = request.get_json()
        username = data["username"]
        cape = data["cape"]
        if cape == "DEFAULT":
            cape = "https://raw.githubusercontent.com/Saturn5Vfive/ShadowClient/main/goldshadowcape.png"
        if cape == "OGDEFAULT":
            cape = "https://raw.githubusercontent.com/Saturn5Vfive/ShadowClient/main/shadowcape.png"
        if cape == "SNOWY":
            cape = "https://raw.githubusercontent.com/Saturn5Vfive/ShadowClient/main/monty4.png"
        if cape == "ARMADA":
            cape = "https://raw.githubusercontent.com/Saturn5Vfive/ShadowClient/main/armadacape.png"
        if cape == "MOLES":
            cape = "https://raw.githubusercontent.com/Saturn5Vfive/ShadowClient/main/molecape.png"
        nonce = data["nonce"]
        if not nonce == "tCmCcuxT8IdQjH4CHMIP1bChonzlDutL0kENcyU7":
            return jsonify({"you-are-tonights-big-looser": 1})
        capes.setdefault(username, cape)
        capesave()
        return jsonify({"you-got-a-cape": 1})
    except:
        return jsonify({"what-the-fuck": 1})

@app.route("/release", methods=["POST"])
def removeCape():
    try:
        data = request.get_json()
        username = data["username"]
        nonce = data["nonce"]
        if not nonce == "tCmCcuxT8IdQjH4CHMIP1bChonzlDutL0kENcyU7":
            return jsonify({"you-are-tonights-big-looser": 1})
        capes.pop(username)
        capesave()
        return jsonify({"you-got-a-cape": 1})
    except:
        return jsonify({"what-the-fuck": 1})

@app.route("/retrieve", methods=["GET"])
def getCapes():
    return jsonify(capes)

if __name__ == "__main__":
  app.run(debug=True)