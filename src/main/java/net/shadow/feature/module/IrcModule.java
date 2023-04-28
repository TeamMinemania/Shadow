//package net.shadow.feature.module;
//
//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.JDABuilder;
//import net.dv8tion.jda.api.entities.Message;
//import net.shadow.Shadow;
//import net.shadow.font.FontRenderers;
//import net.shadow.event.events.ChatOutput;
//import net.shadow.feature.base.Module;
//import net.shadow.feature.base.ModuleType;
//import net.shadow.plugin.DiscordRun;
//import net.shadow.plugin.Notification;
//import net.shadow.plugin.NotificationSystem;
//import net.shadow.utils.ChatUtils;
//
//import javax.security.auth.login.LoginException;
//
//public class IrcModule extends Module implements ChatOutput {
//    private static JDA irc = null;
//
//    public IrcModule() {
//        super("IRC", "chat on discor", ModuleType.CHAT);
//    }
//
//    public static void onServer(String contentRaw, Message message) {
//        ChatUtils.irc(contentRaw);
//    }
//
//    @Override
//    public void onEnable() {
//        Shadow.getEventSystem().add(ChatOutput.class, this);
//        new Thread(() -> {
//            try {
//                irc = JDABuilder.createDefault("OTA3OTkzMjUxNjQ5MzE0ODM3.YYvQnA.kt8bdQYWEUZc5yVYXZK7IozLzaY").build();
//                NotificationSystem.notifications.add(new Notification("IRC", "Connected to the irc", 150));
//                irc.addEventListener(new DiscordRun());
//            } catch (LoginException e) {
//                NotificationSystem.notifications.add(new Notification("IRC", "Could not log into the irc, disconnecting", 150));
//                this.setEnabled(false);
//            }
//        }).start();
//    }
//
//    @Override
//    public void onDisable() {
//        Shadow.getEventSystem().remove(ChatOutput.class, this);
//        new Thread(() -> irc.shutdown()).start();
//    }
//
//    @Override
//    public void onUpdate() {
//
//    }
//
//    @Override
//    public void onRender() {
//
//    }
//
//    @Override
//    public void onSentMessage(ChatOutputEvent event) {
//        event.getMessage();
//        if (event.getMessage().startsWith("?")) {
//            event.cancel();
//            irc.getGuildById("785418898853199892").getTextChannelById("907995071977250826").sendMessage("[" + Shadow.c.player.getGameProfile().getName() + "] " + event.getMessage().substring(1)).queue();
//        }
//    }
//}
//