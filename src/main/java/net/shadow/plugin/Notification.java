package net.shadow.plugin;

import com.mojang.authlib.Environment;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.Shadow;
import net.shadow.clickgui.ClickGUI;
import net.shadow.font.FontRenderers;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.TransitionUtils;

public class Notification {
    public final String title;
    public final long creationDate;
    public String contents;
    public long duration;
    public double posX;
    public double posY;
    public double renderPosX = 0;
    public double renderPosY = 0;
    public double animationProgress = 0;
    public double animationGoal = 0;
    public boolean shouldDoAnimation = false;


    public Notification(String title, String description, int frames) {
        this.duration = frames * 15;
        this.creationDate = System.currentTimeMillis();
        this.contents = description;
        this.title = title;
    }
}
