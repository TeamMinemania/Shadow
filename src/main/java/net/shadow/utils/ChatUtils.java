package net.shadow.utils;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shadow.Shadow;
//import net.shadow.gui.ShadowScreenIMGUI;
import net.shadow.gui.ConsoleScreen;
import net.shadow.gui.ConsoleScreen.LogEntry;


public class ChatUtils {
    static long lastbreakerpop = 0;

    public static void message(String msg) {
        if(Shadow.c.currentScreen instanceof ConsoleScreen){
            for(String msg2 : msg.split("\n")){
                ConsoleScreen.instance().addLog(new LogEntry(msg2, ConsoleScreen.BACKGROUND));
            }
        }
        else {
            assert Shadow.c.player != null;
            Shadow.c.player.sendMessage(Text.of(Formatting.GRAY + "[" + Formatting.DARK_GRAY + "Shadow" + Formatting.GRAY + "] " + Formatting.RESET + msg), false);
        }
    }

    public static void hud(String msg) {
        assert Shadow.c.player != null;
        Shadow.c.player.sendMessage(Text.of(Formatting.GRAY + "[" + Formatting.DARK_GRAY + "Shadow" + Formatting.GRAY + "] " + Formatting.RESET + msg), true);
    }

    public static void breakerPop(String alert) {
        Shadow.c.player.sendMessage(Text.of(Formatting.GRAY + "[" + Formatting.DARK_GRAY + "Shadow" + Formatting.GRAY + "] " + Formatting.GRAY + "[" + Formatting.DARK_GRAY + "Anticrash" + Formatting.GRAY + "] " + Formatting.RESET + alert), false);
    }

    public static void irc(String text) {
        Shadow.c.player.sendMessage(Text.of(Formatting.GRAY + "[" + Formatting.DARK_GRAY + "Shadow" + Formatting.GRAY + "] " + Formatting.GRAY + "[" + Formatting.DARK_GRAY + "IRC" + Formatting.GRAY + "] " + Formatting.RESET + text), false);
    }
}