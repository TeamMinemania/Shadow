package net.shadow.plugin;

import java.util.ArrayList;
import java.util.List;

public class FriendSystem {
    public static final List<String> friendsystem = new ArrayList<>();

    public static String getFriendCFormat() {
        StringBuilder list = new StringBuilder();
        for (String friend : friendsystem) {
            list.append("name=!").append(friend).append(",");
        }
        list = new StringBuilder(list.substring(0, list.length() - 1));
        return list.toString();
    }

    public static boolean isFriend(String name) {
        for (String s : friendsystem) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getFriends() {
        String[] friends = new String[friendsystem.size()];
        for (int i = 0; i < friendsystem.size(); i++) {
            friends[i] = friendsystem.get(i);
        }
        return friends;
    }
}
