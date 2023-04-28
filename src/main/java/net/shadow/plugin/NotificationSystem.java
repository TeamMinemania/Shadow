package net.shadow.plugin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.Shadow;
import net.shadow.font.FontRenderers;
import net.shadow.font.adapter.FontAdapter;
import net.shadow.utils.RenderUtils;
import net.shadow.utils.TransitionUtils;

public class NotificationSystem {
    public static final List<Notification> notifications = new ArrayList<>();


    public static void post(String title, String desc){
        NotificationSystem.notifications.add(new Notification(title, desc, 150));
    }

    public static void render() {
        for (Notification notification : new ArrayList<>(notifications)) {
            notification.renderPosX = TransitionUtils.transition(notification.renderPosX, notification.posX, 10);
            notification.renderPosY = TransitionUtils.transition(notification.renderPosY, notification.posY, 10);
            notification.animationProgress = TransitionUtils.transition(notification.animationProgress, notification.animationGoal, 20, 0.0001);
        }

        double padding = 10;
        MatrixStack ms = new MatrixStack();
        double yOffset = 0;
        double bottomRightStartX = Shadow.c.getWindow().getScaledWidth() - padding;
        double bottomRightStartY = Shadow.c.getWindow().getScaledHeight() - padding;
        FontAdapter fontRenderer = FontRenderers.getRenderer();
        double texPadding = 4;
        double iconDimensions = 24;
        double minWidth = 100;

        long c = System.currentTimeMillis();
        notifications.removeIf(notification -> notification.creationDate + notification.duration < c && TransitionUtils.easeOutExpo(notification.animationProgress) == 0);
        for (Notification notification : new ArrayList<>(notifications)) {
            boolean notificationExpired = notification.creationDate + notification.duration < c;
            notification.animationGoal = notificationExpired ? 0 : 1;
            double contentHeight = 0;
            double contentWidth = 0;
            List<String> content = new ArrayList<>();
            boolean hasTitle = notification.title != null && !notification.title.isEmpty();
            if (hasTitle) {
                contentHeight += fontRenderer.getFontHeight();
                contentWidth = fontRenderer.getStringWidth(notification.title);
                content.add(notification.title);
            }

            contentHeight += fontRenderer.getFontHeight();
            content.add(notification.contents);


            double notificationHeight = Math.max(iconDimensions, contentHeight) + texPadding * 2d; // always have padding at the outside no matter what
            double notificationWidth = texPadding + iconDimensions + texPadding + Math.max(minWidth, contentWidth) + texPadding; // take padding for the icon into account as well
            double notificationX = notification.posX = bottomRightStartX - notificationWidth;
            double notificationY = bottomRightStartY - notificationHeight - yOffset;
            double interpolatedAnimProgress = TransitionUtils.easeOutExpo(notification.animationProgress);
            RenderUtils.renderRoundedQuad(ms, new Color(20, 20, 20, (int) Math.min(255, 255 * interpolatedAnimProgress)), notificationX, notificationY, notificationX + notificationWidth, notificationY + notificationHeight, 5);
            RenderSystem.setShaderTexture(0, TextureUtils.NOTIF_SUCCESS.getWhere());
            Color notifTheme = new Color(58, 223, 118);
            RenderUtils.setupRender();
            RenderSystem.setShaderColor(notifTheme.getRed() / 255f, notifTheme.getGreen() / 255f, notifTheme.getBlue() / 255f, (float) interpolatedAnimProgress);
            RenderUtils.renderTexture(ms, notificationX + texPadding, notificationY + notificationHeight / 2d - iconDimensions / 2d, iconDimensions, iconDimensions, 0, 0, iconDimensions, iconDimensions, iconDimensions, iconDimensions);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderUtils.endRender();

            double contentHeightAbsolved = 0;
            for (String s : content) {
                fontRenderer.drawString(ms, s, (float) (notificationX + texPadding + iconDimensions + texPadding), (float) (notificationY + notificationHeight / 2d - contentHeight / 2d + contentHeightAbsolved), 1f, 1f, 1f, (float) interpolatedAnimProgress);
                contentHeightAbsolved += fontRenderer.getFontHeight();
            }


            yOffset += (notificationHeight + 5) * interpolatedAnimProgress;
        }
    }
}
