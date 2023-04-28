package me.x150.authlib.login.altening;

import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import me.x150.authlib.AccountUtils;
import me.x150.authlib.login.mojang.MinecraftToken;
import net.minecraft.client.util.Session;
import net.shadow.Shadow;
import net.shadow.mixin.MinecraftClientAccessor;

import java.util.Optional;

public class AlteningAuth {
    private static final String AUTH = "http://authserver.thealtening.com";
    private static final String ACCOUNT = "https://api.mojang.com";
    private static final String SESSION = "http://sessionserver.thealtening.com";
    private static final String SERVICES = "https://api.minecraftservices.com";
    String username;
    String uuid;
    String token;
    private YggdrasilUserAuthentication getAuth() {
        YggdrasilUserAuthentication yggdrasilUserAuthentication = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(((MinecraftClientAccessor) Shadow.c).getProxy(), "", Environment.create((String)"http://authserver.thealtening.com", (String)"https://api.mojang.com", (String)"http://sessionserver.thealtening.com", (String)"https://api.minecraftservices.com", (String)"The Altening")).createUserAuthentication(Agent.MINECRAFT);
        yggdrasilUserAuthentication.setUsername(token);
        yggdrasilUserAuthentication.setPassword("(REAL)");
        return yggdrasilUserAuthentication;
    }
    public AlteningAuth(String token) {
        this.token = token;
    }
    public boolean fetchInfo() {
        YggdrasilUserAuthentication yggdrasilUserAuthentication = this.getAuth();
        try {
            yggdrasilUserAuthentication.logIn();
            this.username = yggdrasilUserAuthentication.getSelectedProfile().getName();
            this.uuid = yggdrasilUserAuthentication.getSelectedProfile().getId().toString();
            return true;
        }
        catch (AuthenticationException authenticationException) {
            return false;
        }
    }
    public MinecraftToken login() {
        fetchInfo();
        YggdrasilMinecraftSessionService yggdrasilMinecraftSessionService = (YggdrasilMinecraftSessionService) Shadow.c.getSessionService();
        AccountUtils.setBaseUrl(yggdrasilMinecraftSessionService, SESSION + "/session/minecraft/");
        AccountUtils.setJoinUrl(yggdrasilMinecraftSessionService, SESSION + "/session/minecraft/join");
        AccountUtils.setCheckUrl(yggdrasilMinecraftSessionService, SESSION + "/session/minecraft/hasJoined");
        YggdrasilUserAuthentication yggdrasilUserAuthentication = this.getAuth();
        try {
            yggdrasilUserAuthentication.logIn();
            ((MinecraftClientAccessor) Shadow.c).setSession(new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), Optional.empty(), Optional.empty(), Session.AccountType.MOJANG));
            this.username = yggdrasilUserAuthentication.getSelectedProfile().getName();
            return new MinecraftToken(yggdrasilUserAuthentication.getAuthenticatedToken(),username,yggdrasilUserAuthentication.getSelectedProfile().getId());
        }
        catch (AuthenticationException authenticationException) {
            return null;
        }
    }
}
