package net.shadow.feature.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.utils.ChatUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GetUuidCmd extends Command {
    public GetUuidCmd() {
        super("getuuid", "get a players uuid");
    }

    @Override
    public void call(String[] args) {
        if (args.length < 1) {
            ChatUtils.message("Please use the format >GetUuid <player>");
            return;
        }
        try {
            String useruuid = getUUID(args[0]);
            ChatUtils.message("UUID of Player: " + args[0] + " copied to clipboard");
            Shadow.c.keyboard.setClipboard(useruuid);
        } catch (Exception e) {
            ChatUtils.message("Player Does not exist");
        }
    }

    private String getUUID(String username) throws IOException {
        URL profileURL =
                URI.create("https://api.mojang.com/users/profiles/minecraft/")
                        .resolve(URLEncoder.encode(username, StandardCharsets.UTF_8)).toURL();

        try (InputStream profileInputStream = profileURL.openStream()) {
            JsonObject profileJson = new Gson().fromJson(
                    IOUtils.toString(profileInputStream, StandardCharsets.UTF_8),
                    JsonObject.class);

            return profileJson.get("id").getAsString();
        }
    }
}
