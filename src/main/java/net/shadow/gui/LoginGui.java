package net.shadow.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;

import me.x150.authlib.AccountUtils;
import me.x150.authlib.login.mojang.MinecraftAuthenticator;
import me.x150.authlib.login.mojang.MinecraftToken;
import me.x150.authlib.login.mojang.profile.MinecraftProfile;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.shadow.mixin.MinecraftClientAccessor;
import net.shadow.mixin.SessionAccessor;
import net.shadow.Shadow;
import net.shadow.font.FontRenderers;
import net.shadow.font.Texture;
import net.shadow.font.adapter.FontAdapter;
import net.shadow.gui.etc.RoundTextField;
import net.shadow.utils.ClipUtils;
import net.shadow.utils.MSAAFramebuffer;
import net.shadow.utils.PlayerHeadResolver;
import net.shadow.utils.Rectangle;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.TransitionUtils;
import net.shadow.widgets.RoundButton;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL40C;

import java.awt.Color;
import java.io.File;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class LoginGui extends Screen {
    public static final Map<UUID, Texture> texCache = new HashMap<>();
    static final File ALTS_FILE = new File(Shadow.BASE, "alts.shadow");
    static final String TOP_NOTE = """
            // DO NOT SHARE THIS FILE
            // This file contains sensitive information about your accounts
            // Unless you REALLY KNOW WHAT YOU ARE DOING, DO NOT SEND THIS TO ANYONE
            """;
    static final HttpClient downloader = HttpClient.newHttpClient();
    static final Color bg = new Color(20, 20, 20);
    static final Color pillColor = new Color(40, 40, 40, 100);
    static final Color widgetColor = new Color(40, 40, 40);
    static final Color backgroundOverlay = new Color(0, 0, 0, 130);
    static final Color overlayBackground = new Color(30, 30, 30);
    private static LoginGui instance = null;
    final List<AltContainer> alts = new ArrayList<>();
    final double leftWidth = 200;
    final FontAdapter titleSmall = FontRenderers.getCustomSize(30);
    final FontAdapter title = FontRenderers.getCustomSize(40);
    final AtomicBoolean isLoggingIn = new AtomicBoolean(false);
    final boolean currentAccountTextureLoaded = true;
    AltContainer selectedAlt;
    RoundButton add, exit, remove, tags, login, session, censorMail;
    RoundTextField search;
    boolean censorEmail = true;
    double scroll = 0;
    double scrollSmooth = 0;
    Texture currentAccountTexture = new Texture("dynamic/currentaccount");

    private LoginGui() {
        super(Text.of(""));
        loadAlts();
        updateCurrentAccount();
    }

    public static LoginGui instance() {
        if (instance == null) {
            instance = new LoginGui();
        }
        return instance;
    }

    public List<AltContainer> getAlts() {
        return alts.stream().filter(altContainer -> altContainer.storage.cachedName.toLowerCase().startsWith(search.get().toLowerCase()) || Arrays.stream(altContainer.storage.tags.split(",")).map(String::trim).filter(s -> !s.isEmpty()).anyMatch(s -> s.toLowerCase().startsWith(search.get().toLowerCase()))).collect(Collectors.toList());
    }

    void saveAlts() {
        JsonArray root = new JsonArray();
        for (AltContainer alt1 : alts) {
            AltStorage alt = alt1.storage;
            JsonObject current = new JsonObject();
            current.addProperty("email", alt.email);
            current.addProperty("password", alt.password);
            current.addProperty("type", alt.type.name());
            current.addProperty("cachedUsername", alt.cachedName);
            current.addProperty("cachedUUID", alt.cachedUuid != null ? alt.cachedUuid.toString() : null);
            current.addProperty("valid", alt.valid);
            // remove every tag that is empty or consists of only spaces
            List<String> parsedTags = Arrays.stream(alt.tags.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
            current.addProperty("tags", parsedTags.isEmpty() ? "" : String.join(",", parsedTags));
            root.add(current);
        }
        try {
            FileUtils.write(ALTS_FILE, TOP_NOTE + "\n" + root, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void close() {
        super.close();
        saveAlts();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    void loadAlts() {

        if (!ALTS_FILE.isFile()) {
            ALTS_FILE.delete();
        }
        if (!ALTS_FILE.exists()) {
            return;
        }
        try {
            String contents = FileUtils.readFileToString(ALTS_FILE, StandardCharsets.UTF_8);
            JsonArray ja = JsonParser.parseString(contents).getAsJsonArray();
            for (JsonElement jsonElement : ja) {
                JsonObject jo = jsonElement.getAsJsonObject();
                try {
                    AltStorage container = new AltStorage(jo.get("cachedUsername").getAsString(), jo.get("email").getAsString(), jo.get("password").getAsString(), UUID.fromString(jo.get("cachedUUID").getAsString()), AddScreenOverlay.AccountType.valueOf(jo.get("type").getAsString()), jo.get("tags") == null ? "" : jo.get("tags").getAsString());
                    container.valid = !jo.has("valid") || jo.get("valid").getAsBoolean();
                    AltContainer ac = new AltContainer(0, 0, 0, container);
                    ac.renderY = ac.renderX = -1;
                    alts.add(ac);
                } catch (Exception ignored) {

                }

            }
        } catch (Exception ignored) {
        }
    }

    double getPadding() {
        return 7;
    }

    double getHeaderHeight() {
        return 10 + getPadding() + title.getMarginHeight();
    }

    public void setSelectedAlt(AltContainer selectedAlt) {
        this.selectedAlt = selectedAlt;

    }

    void toggleCensor() {
        censorEmail = !censorEmail;
        censorMail.setText(censorEmail ? "Show email" : "Hide email");
    }

    @Override
    protected void init() {
        search = new RoundTextField(width - 200 - 5 - 100 - 5 - 60 - 5 - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 200D, 20D, "Search", 5D);
        addDrawableChild(search);
        censorMail = new RoundButton(RoundButton.STANDARD, width - 100 - 5 - 60 - 5 - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 100, 20, "Show email", this::toggleCensor);
        add = new RoundButton(RoundButton.STANDARD, width - 60 - 5 - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 60, 20, "Add", () -> client.setScreen(new AddScreenOverlay(this)));
        exit = new RoundButton(RoundButton.STANDARD, width - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 20, 20, "X", this::close);

        double padding = 5;
        double widRHeight = 64 + padding * 2;
        double toX = width - getPadding();
        double fromY = getHeaderHeight();
        double toY = fromY + widRHeight;
        double fromX = width - (leftWidth + getPadding());
        double texDim = widRHeight - padding * 2;
        double buttonWidth = (toX - (fromX + texDim + padding * 2)) / 3d - padding / 4d;
        login = new RoundButton(RoundButton.STANDARD, fromX + texDim + padding * 2, toY - 20 - padding, buttonWidth - padding, 20, "Login", this::login);
        remove = new RoundButton(RoundButton.STANDARD, fromX + texDim + padding * 2 + buttonWidth + padding / 2d, toY - 20 - padding, buttonWidth - padding, 20, "Remove", this::remove);
        tags = new RoundButton(RoundButton.STANDARD, fromX + texDim + padding * 2 + buttonWidth + padding / 2d + buttonWidth + padding / 2d, toY - 20 - padding, buttonWidth - padding, 20, "Tags", this::editTags);

        toY = height - getPadding();
        buttonWidth = toX - fromX - padding * 3 - texDim;
        session = new RoundButton(RoundButton.STANDARD, fromX + texDim + padding * 2, toY - 20 - padding, buttonWidth, 20, "Session", () -> {
            Objects.requireNonNull(client).setScreen(new SessionEditor(this, Shadow.c.getSession())); // this is not a session stealer
        });

        addDrawableChild(censorMail);
        addDrawableChild(add);
        addDrawableChild(exit);
        addDrawableChild(login);
        addDrawableChild(remove);
        addDrawableChild(tags);
        addDrawableChild(session);
    }

    void editTags() {
        client.setScreen(new TagEditor(this));
    }

    void updateCurrentAccount() {
        UUID uid = Shadow.c.getSession().getProfile().getId();

        this.currentAccountTexture = PlayerHeadResolver.resolve(uid);
    }

    void login() {
        if (this.selectedAlt == null) {
            return;
        }
        isLoggingIn.set(true);
        new Thread(() -> {
            this.selectedAlt.login();
            isLoggingIn.set(false);
            // TODO: Actually fix this error instead of janky bypass
            if (!this.selectedAlt.storage.valid && this.selectedAlt.storage.type != AddScreenOverlay.AccountType.ALTENING) {

                return;
            }
            if(this.selectedAlt.storage.type == AddScreenOverlay.AccountType.ALTENING) return;
            AccountUtils.setBaseUrl((YggdrasilMinecraftSessionService) Shadow.c.getSessionService(),"https://sessionserver.mojang.com/session/minecraft/");
            AccountUtils.setJoinUrl((YggdrasilMinecraftSessionService) Shadow.c.getSessionService(),"https://sessionserver.mojang.com/session/minecraft/join");
            AccountUtils.setCheckUrl((YggdrasilMinecraftSessionService) Shadow.c.getSessionService(),"https://sessionserver.mojang.com/session/minecraft/hasJoined");

            Session newSession = new Session(selectedAlt.storage.cachedName, selectedAlt.storage.cachedUuid.toString(), selectedAlt.storage.accessToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
            ((MinecraftClientAccessor) Shadow.c).setSession(newSession);
            updateCurrentAccount();

        }).start();
    }

    void remove() {
        if (this.selectedAlt == null) {
            return;
        }
        alts.remove(this.selectedAlt);
        this.selectedAlt = null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= amount * 10;
        double max = 0;
        for (AltContainer alt : getAlts()) {
            max = Math.max(max, alt.y + alt.getHeight());
        }
        max -= height;
        max += getPadding();
        max = Math.max(0, max);
        scroll = MathHelper.clamp(scroll, 0, max);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            for (AltContainer alt : getAlts()) {
                alt.tickAnim();
            }
            scrollSmooth = TransitionUtils.transition(scrollSmooth, scroll, 7, 0);
            RenderUtils.fill(stack, bg, 0, 0, width, height);
            title.drawString(stack, "Shadow", 10, 10, 0xFFFFFF, false);
            titleSmall.drawString(stack, "Alt manager", 10 + title.getStringWidth("Shadow") + 5, 10 + title.getMarginHeight() - titleSmall.getMarginHeight() - 1, 0xFFFFFF, false);
    
            ClipUtils.globalInstance.addWindow(stack, new Rectangle(getPadding() - 5, getHeaderHeight(), getPadding() + (width - (getPadding() + leftWidth + getPadding() * 2)) + 5, height));
            //RenderUtils.beginScissor(stack, getPadding(), getHeaderHeight(), getPadding() + (width - (getPadding() + leftWidth + getPadding() * 2)), height);
            stack.push();
            stack.translate(0, -scrollSmooth, 0);
            double mys = mouseY + scrollSmooth;
            double x = getPadding();
            double y = getHeaderHeight();
            double wid = width - (getPadding() + leftWidth + getPadding() * 2);
            for (AltContainer alt : getAlts()) {
                alt.x = x;
                alt.y = y;
                alt.width = wid;
                if (alt.renderX == -1) {
                    alt.renderX = -alt.width;
                }
                if (alt.renderY == -1) {
                    alt.renderY = alt.y;
                }
                alt.render(stack, mouseX, mys);
                y += alt.getHeight() + getPadding();
            }
            stack.pop();
            ClipUtils.globalInstance.popWindow();
            //RenderUtils.endScissor();
    
            double padding = 5;
            double widRHeight = 64 + padding * 2;
    
            double fromX = width - (leftWidth + getPadding());
            double toX = width - getPadding();
            double fromY = getHeaderHeight();
            double toY = fromY + widRHeight;
    
            RenderUtils.renderRoundedQuad(stack, pillColor, fromX, fromY, toX, toY, 5);
            boolean vis = selectedAlt != null;
            remove.setVisible(vis);
            login.setVisible(vis);
            tags.setVisible(vis);
            if (vis) {
    
                double texDim = widRHeight - padding * 2;
    
                RenderSystem.enableBlend();
                RenderSystem.colorMask(false, false, false, true);
                RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
                RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderUtils.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), 0, 0, 0, 1, fromX + padding, fromY + padding, fromX + padding + texDim, fromY + padding + texDim, 5, 20);
    
                RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
                RenderSystem.setShaderTexture(0, selectedAlt.tex);
                RenderUtils.renderTexture(stack, fromX + padding, fromY + padding, texDim, texDim, 0, 0, 64, 64, 64, 64);
                RenderSystem.defaultBlendFunc();
    
                String mail;
                if (this.selectedAlt.storage.type != AddScreenOverlay.AccountType.CRACKED || this.selectedAlt.storage.type != AddScreenOverlay.AccountType.ALTENING) {
                    mail = this.selectedAlt.storage.email;
                    String[] mailPart = mail.split("@");
                    String domain = mailPart[mailPart.length - 1];
                    String mailN = String.join("@", Arrays.copyOfRange(mailPart, 0, mailPart.length - 1));
                    if (censorEmail) {
                        mailN = "*".repeat(mailN.length());
                    }
                    mail = mailN + "@" + domain;
                } else {
                    mail = "No email bound";
                }
                AltContainer.PropEntry[] props = new AltContainer.PropEntry[] { new AltContainer.PropEntry(this.selectedAlt.storage.type == AddScreenOverlay.AccountType.CRACKED ? this.selectedAlt.storage.email : this.selectedAlt.storage.cachedName, FontRenderers.getCustomSize(22), this.selectedAlt.storage.valid ? 0xFFFFFF : 0xFF3333), new AltContainer.PropEntry(mail, FontRenderers.getRenderer(), 0xAAAAAA), new AltContainer.PropEntry("Type: " + this.selectedAlt.storage.type.s, FontRenderers.getRenderer(), 0xAAAAAA) };
    
                float propsOffset = (float) (fromY + padding);
                for (AltContainer.PropEntry prop : props) {
                    prop.cfr.drawString(stack, prop.name, (float) (fromX + padding + texDim + padding), propsOffset, prop.color, false);
                    propsOffset += prop.cfr.getMarginHeight();
                }
            }
    
            toY = height - getPadding();
            fromY = toY - widRHeight;
            RenderUtils.renderRoundedQuad(stack, pillColor, fromX, fromY, toX, toY, 5);
            double texDim = widRHeight - padding * 2;
    
            RenderSystem.enableBlend();
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderUtils.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), 0F, 0F, 0F, 1F, fromX + padding, fromY + padding, fromX + padding + texDim, fromY + padding + texDim, 5D, 25D);
    
            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
            RenderSystem.setShaderTexture(0, currentAccountTextureLoaded ? currentAccountTexture : DefaultSkinHelper.getTexture());
            if (currentAccountTextureLoaded) {
                RenderUtils.renderTexture(stack, fromX + padding, fromY + padding, texDim, texDim, 0, 0, 64, 64, 64, 64);
            } else {
                RenderUtils.renderTexture(stack, fromX + padding, fromY + padding, texDim, texDim, 8, 8, 8, 8, 64, 64);
            }
            RenderSystem.defaultBlendFunc();
            String uuid = Shadow.c.getSession().getUuid();
            double uuidWid = FontRenderers.getRenderer().getStringWidth(uuid);
            double maxWid = leftWidth - texDim - padding * 3;
            if (uuidWid > maxWid) {
                double threeDotWidth = FontRenderers.getRenderer().getStringWidth("...");
                uuid = FontRenderers.getRenderer().trimStringToWidth(uuid, maxWid - 1 - threeDotWidth);
                uuid += "...";
            }
            AltContainer.PropEntry[] props = new AltContainer.PropEntry[] { new AltContainer.PropEntry(Shadow.c.getSession().getUsername(), FontRenderers.getCustomSize(22), 0xFFFFFF), new AltContainer.PropEntry(uuid, FontRenderers.getRenderer(), 0xAAAAAA) };
            float propsOffset = (float) (fromY + padding);
            for (AltContainer.PropEntry prop : props) {
                prop.cfr.drawString(stack, prop.name, (float) (fromX + padding + texDim + padding), propsOffset, prop.color, false);
                propsOffset += prop.cfr.getMarginHeight();
            }
    
            super.render(stack, mouseX, mouseY, delta);
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Rectangle rBounds = new Rectangle(getPadding(), getHeaderHeight(), getPadding() + (width - (getPadding() + leftWidth + getPadding() * 2)), height);

        if (isLoggingIn.get()) {
            return false;
        }
        boolean a = super.mouseClicked(mouseX, mouseY, button);
        if (a) return true;
        if (mouseX >= rBounds.getX() && mouseX <= rBounds.getX1() && mouseY >= rBounds.getY() && mouseY <= rBounds.getY1()) {
            for (AltContainer alt : getAlts()) {
                alt.clicked(mouseX, mouseY + scrollSmooth);
            }
        }
        return false;
    }

    static class AltStorage {
        final String email;
        final String password;
        final AddScreenOverlay.AccountType type;
        String tags;
        String cachedName;
        String accessToken;
        UUID cachedUuid;
        boolean valid = true;
        boolean didLogin = false;

        public AltStorage(String n, String e, String p, UUID u, AddScreenOverlay.AccountType type, String tags) {
            this.cachedName = n;
            this.email = e;
            this.password = p;
            this.cachedUuid = u;
            this.type = type;
            this.tags = tags;
        }
    }

    static class SessionEditor extends Screen {
        static final double widgetWid = 300;
        static double widgetHei = 0;
        final Session session;
        final Screen parent;
        final double padding = 5;
        final FontAdapter title = FontRenderers.getCustomSize(40);
        RoundTextField access, name, uuid;
        RoundButton save;

        public SessionEditor(Screen parent, Session s) {
            super(Text.of(""));
            this.session = s;
            this.parent = parent;
        }

        @Override
        protected void init() {
            RoundButton exit = new RoundButton(widgetColor, width - 20 - 5, 5, 20, 20, "X", ()->{
                Shadow.c.setScreen(parent);
            });
            addDrawableChild(exit);
            double y = height / 2d - widgetHei / 2d + padding + title.getMarginHeight() + FontRenderers.getRenderer().getMarginHeight() + padding;
            RoundTextField accessToken = new RoundTextField(width / 2d - (widgetWid - padding * 2) / 2d, y, widgetWid - padding * 2, 20, "Access token");
            accessToken.setText(session.getAccessToken());
            y += accessToken.getHeight() + padding;
            RoundTextField username = new RoundTextField(width / 2d - (widgetWid - padding * 2) / 2d, y, widgetWid - padding * 2, 20, "Username");
            username.setText(session.getUsername());
            y += username.getHeight() + padding;
            RoundTextField uuid = new RoundTextField(width / 2d - (widgetWid - padding * 2) / 2d, y, widgetWid - padding * 2, 20, "UUID");
            uuid.setText(session.getUuid());
            y += uuid.getHeight() + padding;
            RoundButton save = new RoundButton(widgetColor, width / 2d - (widgetWid - padding * 2) / 2d, y, widgetWid - padding * 2, 20, "Save", () -> {
                SessionAccessor sa = (SessionAccessor) session;
                sa.setUsername(username.get());
                sa.setAccessToken(accessToken.get());
                sa.setUuid(uuid.get());
                Objects.requireNonNull(client).setScreen(parent);
            });
            y += 20 + padding;
            this.save = save;
            access = accessToken;
            name = username;
            this.uuid = uuid;
            addDrawableChild(save);
            addDrawableChild(access);
            addDrawableChild(name);
            addDrawableChild(uuid);
            widgetHei = y - (height / 2d - widgetHei / 2d);
            super.init();
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
            if (parent != null) {
                parent.render(stack, mouseX, mouseY, delta);
            }

            double y = height / 2d - widgetHei / 2d + padding + title.getMarginHeight() + FontRenderers.getRenderer().getMarginHeight() + padding;
            access.setY(y);
            y += access.getHeight() + padding;
            name.setY(y);
            y += name.getHeight() + padding;
            uuid.setY(y);
            y += uuid.getHeight() + padding;
            save.setY(y);
            y += 20 + padding;
            widgetHei = y - (height / 2d - widgetHei / 2d);


            save.setEnabled(!name.get().isEmpty() && !uuid.get().isEmpty()); // enable when both name and uuid are set
            RenderUtils.fill(stack, backgroundOverlay, 0, 0, width, height);


            double centerX = width / 2d;
            double centerY = height / 2d;
            RenderUtils.renderRoundedQuad(stack, overlayBackground, centerX - widgetWid / 2d, centerY - widgetHei / 2d, centerX + widgetWid / 2d, centerY + widgetHei / 2d, 5);
            stack.push();

            double originX = width / 2d - widgetWid / 2d;
            double originY = height / 2d - widgetHei / 2d;
            title.drawString(stack, "Edit session", (float) (originX + padding), (float) (originY + padding), 0xFFFFFF, false);
            FontRenderers.getRenderer().drawString(stack, "Edit your user session here", (float) (originX + padding), (float) (originY + padding + title.getMarginHeight()), 0xAAAAAA, false);
            stack.pop();
            super.render(stack, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (Element child : children()) {
                child.mouseClicked(-1, -1, button);
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    class TagEditor extends Screen{
        final List<RoundButton> tags = new ArrayList<>();
        final double widgetWidth = 300;
        final Screen parent;
        RoundTextField tagName;
        RoundButton add;
        double widgetHeight = 0;
        double widgetStartX, widgetStartY;

        public TagEditor(Screen parent) {
            super(Text.of(""));
            this.parent = parent;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (RoundButton tag : new ArrayList<>(tags)) {
                tag.mouseClicked(mouseX, mouseY, button);
            }
            tagName.mouseClicked(mouseX, mouseY, button);
            add.mouseClicked(mouseX, mouseY, button);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            tagName.keyPressed(keyCode, scanCode, modifiers);
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            tagName.charTyped(chr, modifiers);
            return super.charTyped(chr, modifiers);
        }

        @Override
        protected void init() {
            RoundButton exit = new RoundButton(RoundButton.STANDARD, width - 20 - 5, 5, 20, 20, "X", this::close);
            addDrawableChild(exit);
            this.tags.clear();
            String tags = selectedAlt.storage.tags;
            double xOffset = 5;
            double yOffset = 0;
            double widgetsHeight = 20;
            double padding = 5;
            List<String> parsedTags = new ArrayList<>(Arrays.stream(tags.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
            for (String s : parsedTags) {
                if (s.isEmpty()) {
                    continue;
                }
                float width = FontRenderers.getRenderer().getStringWidth(s) + 2 + 4;
                if (xOffset + width > (widgetWidth - 5)) {
                    xOffset = 5;
                    yOffset += FontRenderers.getRenderer().getMarginHeight() + 4 + 2;
                }
                RoundButton inst = new RoundButton(RoundButton.STANDARD, xOffset, yOffset, width, FontRenderers.getRenderer().getMarginHeight() + 4, s, () -> {
                    parsedTags.remove(s);
                    selectedAlt.storage.tags = String.join(",", parsedTags);
                    init();
                });
                this.tags.add(inst);
                xOffset += width + 2;
            }
            double yBase = parsedTags.isEmpty() ? 0 : yOffset + FontRenderers.getRenderer().getMarginHeight() + 4 + padding;
            tagName = new RoundTextField(5, yBase, widgetWidth - 60 - padding * 3, widgetsHeight, "Tag name");
            add = new RoundButton(RoundButton.STANDARD, tagName.getX() + tagName.getWidth() + padding, yBase, 60, widgetsHeight, "Add", () -> {
                if (tagName.get().isEmpty()) {
                    return;
                }
                parsedTags.add(tagName.get());
                tagName.set("");
                selectedAlt.storage.tags = String.join(",", parsedTags);
                init();
            });
            widgetHeight = add.getY() + add.getHeight() + padding * 2;

            widgetStartX = width / 2d - widgetWidth / 2d;
            widgetStartY = height / 2d - widgetHeight / 2d;
            double widgetStartY = this.widgetStartY + padding;

            for (RoundButton tag : this.tags) {
                tag.setX(tag.getX() + widgetStartX);
                tag.setY(tag.getY() + widgetStartY);
            }
            tagName.setX(tagName.getX() + widgetStartX);
            tagName.setY(tagName.getY() + widgetStartY);
            add.setX(add.getX() + widgetStartX);
            add.setY(add.getY() + widgetStartY);
        }

        @Override
        public void close() {
            client.setScreen(parent);
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
            for (RoundButton tag : tags) {
                tag.onFastTick();
            }
            add.onFastTick();
            if (parent != null) {
                parent.render(stack, mouseX, mouseY, delta);
            }
            RenderUtils.fill(stack, backgroundOverlay, 0, 0, width, height);
            RenderUtils.renderRoundedQuad(stack, overlayBackground, widgetStartX, widgetStartY, widgetStartX + widgetWidth, widgetStartY + widgetHeight, 5);
            for (RoundButton tag : tags) {
                tag.render(stack, mouseX, mouseY, delta);
            }
            tagName.render(stack, mouseX, mouseY, delta);
            add.render(stack, mouseX, mouseY, delta);
            super.render(stack, mouseX, mouseY, delta);
        }
    }

    class AddScreenOverlay extends Screen {
        static final double widgetWid = 200;
        static int accountTypeI = 0;
        static double widgetHei = 0;
        final List<RoundButton> buttons = new ArrayList<>();
        final Screen parent;
        final double padding = 5;
        final FontAdapter title = FontRenderers.getCustomSize(40);
        RoundTextField email;
        RoundTextField passwd;
        RoundButton type;
        RoundButton add;

        public AddScreenOverlay(Screen parent) {
            super(Text.of(""));
            this.parent = parent;
        }

        @Override
        protected void init() {
            RoundButton exit = new RoundButton(RoundButton.STANDARD, width - 20 - 5, 5, 20, 20, "X", () -> Objects.requireNonNull(client).setScreen(parent));
            buttons.add(exit);
            email = new RoundTextField(width / 2d - (widgetWid - padding * 2) / 2d, height / 2d - widgetHei / 2d + padding, widgetWid - padding * 2, 20, "E-Mail or username or Token", 5D);
            passwd = new RoundTextField(width / 2d - (widgetWid - padding * 2) / 2d, height / 2d - widgetHei / 2d + padding * 2 + 20, widgetWid - padding * 2, 20, "Password", 5D);
            type = new RoundButton(RoundButton.STANDARD, 0, 0, widgetWid / 2d - padding * 1.5, 20,  "Type: " + AccountType.values()[accountTypeI].s, this::cycle);
            add = new RoundButton(RoundButton.STANDARD, 0, 0, widgetWid / 2d - padding * 1.5, 20, "Add", this::add);
        }

        void add() {
            AltStorage as = new AltStorage("Unknown", email.getText(), passwd.getText(), UUID.randomUUID(), AccountType.values()[accountTypeI], "");
            AltContainer ac = new AltContainer(-1, -1, 0, as);
            ac.renderX = -1;
            ac.renderY = -1;
            alts.add(ac);
            Objects.requireNonNull(client).setScreen(parent);
        }

        boolean isAddApplicable() {
            if (AccountType.values()[accountTypeI] == AccountType.CRACKED ||AccountType.values()[accountTypeI] == AccountType.ALTENING  && !email.getText().isEmpty()) {
                return true;
            } else {
                return !email.getText().isEmpty() && !passwd.getText().isEmpty();
            }
        }

        void cycle() {
            accountTypeI++;
            if (accountTypeI >= AccountType.values().length) {
                accountTypeI = 0;
            }
            type.setText("Type: " + AccountType.values()[accountTypeI].s);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (RoundButton themedButton : buttons) {
                themedButton.mouseClicked(mouseX, mouseY, button);
            }
            email.mouseClicked(mouseX, mouseY, button);
            passwd.mouseClicked(mouseX, mouseY, button);
            type.mouseClicked(mouseX, mouseY, button);
            add.mouseClicked(mouseX, mouseY, button);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
            for (RoundButton button : buttons) {
                button.onFastTick();
            }
            type.onFastTick();
            add.onFastTick();
            if (parent != null) {
                parent.render(stack, mouseX, mouseY, delta);
            }
            RenderUtils.fill(stack, backgroundOverlay, 0, 0, width, height);

            for (RoundButton button : buttons) {
                button.render(stack, mouseX, mouseY, delta);
            }
            double centerX = width / 2d;
            double centerY = height / 2d;
            RenderUtils.renderRoundedQuad(stack, overlayBackground, centerX - widgetWid / 2d, centerY - widgetHei / 2d, centerX + widgetWid / 2d, centerY + widgetHei / 2d, 5);
            stack.push();

            double originX = width / 2d - widgetWid / 2d;
            double originY = height / 2d - widgetHei / 2d;
            title.drawString(stack, "Add account", (float) (originX + padding), (float) (originY + padding), 0xFFFFFF, false);
            FontRenderers.getRenderer().drawString(stack, "Add another account here", (float) (originX + padding), (float) (originY + padding + title.getMarginHeight()), 0xAAAAAA, false);
            email.setX(originX + padding);
            email.setY(originY + padding + title.getMarginHeight() + FontRenderers.getRenderer().getMarginHeight() + padding);
            email.setWidth(widgetWid - padding * 2);
            email.render(stack, mouseX, mouseY, 0);
            passwd.setX(originX + padding);
            passwd.setY(originY + padding + title.getMarginHeight() + FontRenderers.getRenderer().getMarginHeight() + padding + email.getHeight() + padding);
            passwd.setWidth(widgetWid - padding * 2);
            passwd.render(stack, mouseX, mouseY, 0);
            type.setX(originX + padding);
            type.setY(originY + padding + title.getMarginHeight() + FontRenderers.getRenderer().getMarginHeight() + padding + email.getHeight() + padding + passwd.getHeight() + padding);
            type.render(stack, mouseX, mouseY, delta);
            add.setX(originX + padding + type.getWidth() + padding);
            add.setY(originY + padding + title.getMarginHeight() + FontRenderers.getRenderer().getMarginHeight() + padding + email.getHeight() + padding + passwd.getHeight() + padding);
            add.setEnabled(isAddApplicable());
            add.render(stack, mouseX, mouseY, delta);
            widgetHei = padding + title.getMarginHeight() + FontRenderers.getRenderer().getMarginHeight() + padding + email.getHeight() + padding + passwd.getHeight() + padding + type.getHeight() + padding;
            stack.pop();
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            email.charTyped(chr, modifiers);
            passwd.charTyped(chr, modifiers);
            return super.charTyped(chr, modifiers);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            email.keyPressed(keyCode, scanCode, modifiers);
            passwd.keyPressed(keyCode, scanCode, modifiers);
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        enum AccountType {
            MOJANG("Mojang"), MICROSOFT("Microsoft"), CRACKED("Cracked"), ALTENING("The Altening");

            final String s;

            AccountType(String s) {
                this.s = s;
            }
        }
    }

    public class AltContainer {
        final AltStorage storage;
        Texture tex;
        boolean texLoaded = false;
        float animProgress = 0;
        boolean isHovered = false;
        double x, y, width, renderX, renderY;


        public AltContainer(double x, double y, double width, AltStorage inner) {
            this.storage = inner;
            this.tex = new Texture(DefaultSkinHelper.getTexture(inner.cachedUuid));
            this.x = x;
            this.y = y;
            this.width = width;
            UUID uuid = inner.cachedUuid;
            if (texCache.containsKey(uuid)) {
                this.tex = texCache.get(uuid);
            } else {
                downloadTexture();
            }
        }

        void downloadTexture() {
            if(this.storage.cachedUuid == null) return;
            this.tex = PlayerHeadResolver.resolve(this.storage.cachedUuid);
        }

        public double getHeight() {
            return 60d;
        }

        public void login() {
            if (storage.didLogin) {
                return;
            }
            storage.didLogin = true;
            try {
                MinecraftAuthenticator auth = new MinecraftAuthenticator();
                MinecraftToken token = switch (storage.type) {
                    case MOJANG -> auth.login(storage.email, storage.password);
                    case MICROSOFT -> auth.loginWithMicrosoft(storage.email, storage.password);
                    case CRACKED -> null;
                    case ALTENING -> auth.loginWithAltening(storage.email);
                };
                if (token == null && storage.password.equals("")) {
                    storage.valid = true;
                    storage.cachedUuid = UUID.randomUUID();
                    storage.cachedName = storage.email;
                    storage.accessToken = "shadow";
                    return;
                }
                if (token == null) {
                    throw new NullPointerException();
                }
                storage.accessToken = token.getAccessToken();
                MinecraftProfile profile = null;
                storage.cachedName = profile.getUsername();
                storage.cachedUuid = profile.getUuid();

                downloadTexture();
                storage.valid = true;
            } catch (Exception ignored) {
                storage.valid = false;
            }
        }

        public void tickAnim() {
            double d = 0.04;
            if (!isHovered) {
                d *= -1;
            }
            animProgress += d;
            animProgress = MathHelper.clamp(animProgress, 0, 1);
            if (renderX != -1) {
                renderX = TransitionUtils.transition(renderX, x, 7, 0.0001);
            }
            if (renderY != -1) {
                renderY = TransitionUtils.transition(renderY, y, 7, 0.0001);
            }
        }

        boolean inBounds(double cx, double cy) {
            return cx >= renderX && cx < renderX + width && cy >= renderY && cy < renderY + getHeight();
        }

        double easeInOutQuint(double x) {
            return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
        }

        public void render(MatrixStack stack, double mx, double my) {
            isHovered = inBounds(mx, my);
            stack.push();
            double originX = -width / 2d;
            double originY = -getHeight() / 2d;
            stack.translate(renderX + width / 2d, renderY + getHeight() / 2d, 0);
            float animProgress = (float) easeInOutQuint(this.animProgress);
            stack.scale(MathHelper.lerp(animProgress, 1f, 0.99f), MathHelper.lerp(animProgress, 1f, 0.99f), 1f);
            RenderUtils.renderRoundedQuadWithShadow(stack, pillColor, originX, originY, originX + width, originY + getHeight(), 5, 20);
            double padding = 5;
            double texWidth = getHeight() - padding * 2;
            double texHeight = getHeight() - padding * 2;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderUtils.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), 0, 0, 0, 1, originX + padding, originY + padding, originX + padding + texWidth, originY + padding + texHeight, 5, 20);

            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
            RenderSystem.setShaderTexture(0, tex);
            RenderUtils.renderTexture(stack, originX + padding, originY + padding, texWidth, texHeight, 0, 0, 64, 64, 64, 64);
            String mail;
            if (this.storage.type != AddScreenOverlay.AccountType.CRACKED) {
                mail = this.storage.email;
                String[] mailPart = mail.split("@");
                String domain = mailPart[mailPart.length - 1];
                String mailN = String.join("@", Arrays.copyOfRange(mailPart, 0, mailPart.length - 1));
                if (censorEmail) {
                    mailN = "*".repeat(mailN.length());
                }
                mail = mailN + "@" + domain;
            } else {
                mail = "No email bound";
            }
            PropEntry[] props = new PropEntry[] { new PropEntry(this.storage.type == AddScreenOverlay.AccountType.CRACKED ? this.storage.email : this.storage.cachedName, FontRenderers.getCustomSize(22), storage.valid ? 0xFFFFFF : 0xFF3333), new PropEntry("Email: " + mail, FontRenderers.getRenderer(), 0xAAAAAA)/*, new PropEntry("Type: " + this.storage.type.s, FontRenderers.getRenderer(), 0xAAAAAA)*/ };
            float propsOffset = (float) (getHeight() - (texHeight)) / 2f;
            for (PropEntry prop : props) {
                prop.cfr.drawString(stack, prop.name, (float) (originX + padding + texWidth + padding), (float) (originY + propsOffset), prop.color, false);
                propsOffset += prop.cfr.getFontHeight(prop.name);
            }
            if (isLoggingIn.get() && selectedAlt == this) {
                double fromTop = getHeight() / 2d;
            }
            double xOff = 0;
            for (String s : (storage.tags.isEmpty() ? "No tags" : storage.tags).split(",")) {
                String v = s.trim();
                if (v.isEmpty()) {
                    continue;
                }
                float w = FontRenderers.getRenderer().getStringWidth(v);
                float h = FontRenderers.getRenderer().getMarginHeight();
                float pad = 2;
                w += pad * 2;
                RenderUtils.renderRoundedQuad(stack, RoundButton.STANDARD, originX + padding + texWidth + padding + xOff, originY + getHeight() - h - pad * 2 - padding, originX + padding + texWidth + padding + xOff + w, originY + getHeight() - padding, 5);
                FontRenderers.getRenderer().drawString(stack, v, originX + padding + texWidth + padding + xOff + pad, originY + getHeight() - pad - FontRenderers.getRenderer().getMarginHeight() - padding, 0xFFFFFF);
                xOff += w + 2;
            }

            stack.pop();
        }

        public void clicked(double mx, double my) {
            if (inBounds(mx, my)) {
                setSelectedAlt(this);
            }
        }

        public record PropEntry(String name, FontAdapter cfr, int color) {

        }
    }
}
