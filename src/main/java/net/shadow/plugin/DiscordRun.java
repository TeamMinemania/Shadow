//package net.shadow.plugin;
//
//
//import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.shadow.feature.module.IrcModule;
//
//public class DiscordRun extends ListenerAdapter {
//    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
//        if (event.getMessage().getGuild().getId().equalsIgnoreCase("785418898853199892") && event.getMessage().getChannel().getId().equalsIgnoreCase("907995071977250826")) {
//            if (!event.getMessage().getAuthor().getId().equalsIgnoreCase("907993251649314837")) {
//                IrcModule.onServer("[DISCORD] [" + event.getMessage().getAuthor().getName() + "] " + event.getMessage().getContentRaw(), event.getMessage());
//            } else {
//                IrcModule.onServer(event.getMessage().getContentRaw(), event.getMessage());
//            }
//        }
//    }
//}