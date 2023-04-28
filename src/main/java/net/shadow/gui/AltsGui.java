package net.shadow.gui;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.Shadow;
import net.shadow.font.FontRenderers;
import net.shadow.utils.CreativeUtils;
import net.shadow.utils.RenderUtils;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;
import java.util.Optional;
import me.x150.authlib.AccountUtils;
import me.x150.authlib.login.mojang.MinecraftAuthenticator;
import me.x150.authlib.login.mojang.MinecraftToken;
import me.x150.authlib.login.mojang.profile.MinecraftProfile;
import java.util.UUID;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.math.MathHelper;
import net.shadow.mixin.MinecraftClientAccessor;
import net.shadow.mixin.SessionAccessor;
import net.shadow.font.adapter.FontAdapter;
import net.shadow.gui.etc.RoundTextField;
import net.shadow.utils.ClipUtils;
import net.shadow.utils.MSAAFramebuffer;
import net.shadow.utils.PlayerHeadResolver;
import net.shadow.utils.Rectangle;
import net.shadow.utils.TransitionUtils;
import net.shadow.widgets.RoundButton;


public class AltsGui extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    TextFieldWidget username;
    TextFieldWidget password;

    protected AltsGui() {
        super(Text.of("sus"));
    }

    public void auth(LoginType ltype) {
        MinecraftAuthenticator auth = new MinecraftAuthenticator();
        MinecraftToken token = switch (ltype) {
            case MOJANG -> auth.login(username.getText(), password.getText());
            case MICROSOFT -> auth.loginWithMicrosoft(username.getText(), password.getText());
            case CRACKED -> new MinecraftToken(null, username.getText(), UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));
            case ALTENING -> auth.loginWithAltening(username.getText());
            case TOKEN -> new MinecraftToken(password.getText(), username.getText(), UUID.fromString(CreativeUtils.getUUID2(username.getText())));
        };
        if(token == null){
            username.setText("Email or Password invalid");
            password.setText("");
            return;
        }
        if(ltype.equals(LoginType.ALTENING)) return;
        AccountUtils.setBaseUrl((YggdrasilMinecraftSessionService) Shadow.c.getSessionService(),"https://sessionserver.mojang.com/session/minecraft/");
        AccountUtils.setJoinUrl((YggdrasilMinecraftSessionService) Shadow.c.getSessionService(),"https://sessionserver.mojang.com/session/minecraft/join");
        AccountUtils.setCheckUrl((YggdrasilMinecraftSessionService) Shadow.c.getSessionService(),"https://sessionserver.mojang.com/session/minecraft/hasJoined");

        Session newSession = new Session(token.getUsername(), token.getUuid().toString(), token.getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
        ((MinecraftClientAccessor) Shadow.c).setSession(newSession);
        Shadow.c.setScreen(new TitleScreen());
    }

    @Override
    protected void init() {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        username = new TextFieldWidget(MC.textRenderer, ww - 150, hh - 100, 300, 20, Text.of("username"));
        username.setMaxLength(65535);
        password = new TextFieldWidget(MC.textRenderer, ww - 150, hh - 75, 300, 20, Text.of("password"));
        password.setMaxLength(65535);

        ButtonWidget login = new ButtonWidget(ww - 150, hh - 50, 300, 20, Text.of("Mojang"), button -> {
            auth(LoginType.MOJANG);
        });

        ButtonWidget tokenlogin = new ButtonWidget(ww - 150, hh - 25, 300, 20, Text.of("Token"), button -> {
            auth(LoginType.TOKEN);
        });

        ButtonWidget crackedlogin = new ButtonWidget(ww - 150, hh, 300, 20, Text.of("Cracked"), button -> {
            auth(LoginType.CRACKED);
        });

        ButtonWidget microsoftlogin = new ButtonWidget(ww - 150, hh + 25, 300, 20, Text.of("Microsoft"), button -> {
            auth(LoginType.MICROSOFT);
        });

        ButtonWidget alteninglogin = new ButtonWidget(ww - 150, hh + 50, 300, 20, Text.of("Altening"), button -> {
            auth(LoginType.ALTENING);
        });


        this.addDrawableChild(login);
        this.addDrawableChild(tokenlogin);
        this.addDrawableChild(crackedlogin);
        this.addDrawableChild(alteninglogin);
        this.addDrawableChild(microsoftlogin);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(55, 55, 55, 255).getRGB());
        RenderUtils.renderRoundedQuad(matrices, new Color(25, 25, 25, 255), ww - 225, hh - 125, ww + 225, hh + 100, 10);
        username.render(matrices, mouseX, mouseY, delta);
        password.render(matrices, mouseX, mouseY, delta);
        if(username.getText().length() == 0){
            FontRenderers.getRenderer().drawString(matrices, "Username", ww - 145, hh - 95, 16777215);
        }
        if(password.getText().length() == 0){
            FontRenderers.getRenderer().drawString(matrices, "Password", ww - 145, hh - 70, 16777215);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        username.charTyped(chr, keyCode);
        password.charTyped(chr, keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        username.keyReleased(keyCode, scanCode, modifiers);
        password.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        username.keyPressed(keyCode, scanCode, modifiers);
        password.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        username.mouseClicked(mouseX, mouseY, button);
        password.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }


    enum LoginType {
        MOJANG, 
        MICROSOFT,
        ALTENING,
        CRACKED,
        TOKEN
    }

}