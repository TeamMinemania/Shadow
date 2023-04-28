//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.utils.wjson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public record WsonArray(JsonArray json) {
    public WsonArray(JsonArray json) {
        this.json = Objects.requireNonNull(json);
    }

    public ArrayList<String> getAllStrings() {
        return StreamSupport.stream(json.spliterator(), false)
                .filter(JsonUtils::isString).map(JsonElement::getAsString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<WsonObject> getAllObjects() {
        return StreamSupport.stream(json.spliterator(), false)
                .filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
                .map(WsonObject::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public JsonArray toJsonArray() {
        return json;
    }
}
