package net.shadow.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.shadow.Shadow;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class RenderUtils {

    public static int lerp(int o, int i, double p) {
        return (int) Math.floor(i + (o - i) * MathHelper.clamp(p, 0, 1));
    }

    public static double lerp(double i, double o, double p) {
        return (i + (o - i) * MathHelper.clamp(p, 0, 1));
    }

    public static Color lerp(Color a, Color b, double c) {
        return new Color(lerp(a.getRed(), b.getRed(), c), lerp(a.getGreen(), b.getGreen(), c), lerp(a.getBlue(), b.getBlue(), c), lerp(a.getAlpha(), b.getAlpha(), c));
    }


    public static void setupRender() {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
    public static void renderBlock(MatrixStack matrix,BlockPos p, Color c) {

        RenderUtils.renderObject(new Vec3d(p.getX() - 1, p.getY() - 1, p.getZ() - 1), new Vec3d(3, 3, 3), new Color(100, 100, 100, 100), matrix);
    }
    public static void renderLineToCoords(MatrixStack matrix, BlockPos p, Color c) {
        RenderUtils.vector(Utils.RenderCameraStart(), new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5), c, matrix, 2);
    }

    public static void renderRoundedShadowInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples, double wid) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        double[][] map = new double[][] { new double[] { toX1, toY1 }, new double[] { toX1, fromY1 }, new double[] { fromX1, fromY1 }, new double[] { fromX1, toY1 } };
        for (int i = 0; i < map.length; i++) {
            double[] current = map[i];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
                float sin1 = (float) (sin + Math.sin(rad1) * wid);
                float cos1 = (float) (cos + Math.cos(rad1) * wid);
                bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f).next();
            }
        }
    }

    public static void renderRoundedShadow(MatrixStack matrices, Color innerColor, double fromX, double fromY, double toX, double toY, double rad, double samples, double shadowWidth) {
        //            RenderSystem.defaultBlendFunc();

        int color = innerColor.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        renderRoundedShadowInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad, samples, shadowWidth);
        endRender();
    }

    public static void renderRoundedQuadWithShadow(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double rad, double samples) {
        int color = c.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad, samples);

        renderRoundedShadow(matrices, new Color(10, 10, 10, 100), fromX, fromY, toX, toY, rad, samples, 3);
        endRender();
    }

    public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        double[][] map = new double[][] { new double[] { toX1, toY1 }, new double[] { toX1, fromY1 }, new double[] { fromX1, fromY1 }, new double[] { fromX1, toY1 } };
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
            }
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public static void endRender() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void renderEntity(Entity e, Color c, MatrixStack matrix) {
        Box box = e.getBoundingBox();
        Vec3d bang = box.getCenter().subtract(box.getXLength() / 2, box.getYLength() / 2, box.getZLength() / 2);
        renderObject(bang, new Vec3d(box.getXLength(), box.getYLength(), box.getZLength()), c, matrix);
    }

    public static void renderTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        double z = 0;
        renderTexturedQuad(matrices.peek().getPositionMatrix(), x0, x1, y0, y1, z, (u + 0.0F) / (float) textureWidth, (u + (float) regionWidth) / (float) textureWidth, (v + 0.0F) / (float) textureHeight, (v + (float) regionHeight) / (float) textureHeight);
    }

    private static void renderTexturedQuad(Matrix4f matrix, double x0, double x1, double y0, double y1, double z, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }



    public static void renderEntity(Entity e, Vec3d center, Color c, MatrixStack matrix) {
        Box box = e.getBoundingBox();
        renderObject(center.subtract(box.getXLength() / 2, 0, box.getZLength() / 2), new Vec3d(box.getXLength(), box.getYLength(), box.getZLength()), c, matrix);
    }

    public static void renderObject(Vec3d home, Vec3d dimensions, Color color, MatrixStack stack) {
        float red = color.getRed() / 255f;
        float green = color.getGreen() / 255f;
        float blue = color.getBlue() / 255f;
        float alpha = color.getAlpha() / 255f;
        Camera c = Shadow.c.gameRenderer.getCamera();
        Vec3d camPos = c.getPos();
        home = home.subtract(camPos);
        Vec3d end = home.add(dimensions);
        Matrix4f matrix = stack.peek().getPositionMatrix();
        float x1 = (float) home.x;
        float y1 = (float) home.y;
        float z1 = (float) home.z;
        float x2 = (float) end.x;
        float y2 = (float) end.y;
        float z2 = (float) end.z;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.enableBlend();
        buffer.begin(VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION);
        buffer.vertex(matrix, x1, y2, z1).next();
        buffer.vertex(matrix, x1, y2, z2).next();
        buffer.vertex(matrix, x2, y2, z2).next();
        buffer.vertex(matrix, x2, y2, z1).next();

        buffer.vertex(matrix, x1, y1, z2).next();
        buffer.vertex(matrix, x2, y1, z2).next();
        buffer.vertex(matrix, x2, y2, z2).next();
        buffer.vertex(matrix, x1, y2, z2).next();

        buffer.vertex(matrix, x2, y2, z2).next();
        buffer.vertex(matrix, x2, y1, z2).next();
        buffer.vertex(matrix, x2, y1, z1).next();
        buffer.vertex(matrix, x2, y2, z1).next();

        buffer.vertex(matrix, x2, y2, z1).next();
        buffer.vertex(matrix, x2, y1, z1).next();
        buffer.vertex(matrix, x1, y1, z1).next();
        buffer.vertex(matrix, x1, y2, z1).next();

        buffer.vertex(matrix, x1, y2, z1).next();
        buffer.vertex(matrix, x1, y1, z1).next();
        buffer.vertex(matrix, x1, y1, z2).next();
        buffer.vertex(matrix, x1, y2, z2).next();

        buffer.vertex(matrix, x1, y1, z1).next();
        buffer.vertex(matrix, x2, y1, z1).next();
        buffer.vertex(matrix, x2, y1, z2).next();
        buffer.vertex(matrix, x1, y1, z2).next();

        buffer.end();

        BufferRenderer.draw(buffer);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableBlend();
    }


    public static void vector(Vec3d start, Vec3d end, Color color, MatrixStack matrices, int thicc) {
        float red = color.getRed() / 255f;
        float green = color.getGreen() / 255f;
        float blue = color.getBlue() / 255f;
        float alpha = color.getAlpha() / 255f;
        Camera c = Shadow.c.gameRenderer.getCamera();
        Vec3d camPos = c.getPos();
        start = start.subtract(camPos);
        end = end.subtract(camPos);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float x1 = (float) start.x;
        float y1 = (float) start.y;
        float z1 = (float) start.z;
        float x2 = (float) end.x;
        float y2 = (float) end.y;
        float z2 = (float) end.z;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.enableBlend();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES,
                VertexFormats.POSITION);

        buffer.vertex(matrix, x1, y1, z1).next();
        buffer.vertex(matrix, x2, y2, z2).next();

        buffer.end();

        BufferRenderer.draw(buffer);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableBlend();
    }

    public static void lineScreenD(MatrixStack matrices, Color c, double x, double y, double x1, double y1) {
        float g = c.getRed() / 255f;
        float h = c.getGreen() / 255f;
        float k = c.getBlue() / 255f;
        float f = c.getAlpha() / 255f;
        Matrix4f m = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(m, (float) x, (float) y, 0f).color(g, h, k, f).next();
        bufferBuilder.vertex(m, (float) x1, (float) y1, 0f).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void circle2d(double x, double y, double r, Color c, MatrixStack stack, double shitpc) {
        int color = c.getRGB();
        Matrix4f matrix = stack.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.enableCull();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        for (double i = 0; i < 360; i += shitpc) {
            double radians = Math.toRadians(i);
            double sin = Math.sin(radians) * r;
            double cos = Math.cos(radians) * r;
            buffer.vertex(matrix, (float) (x + sin), (float) (y + cos), 0.0F).color(g, h, k, f).next();
        }
        buffer.end();
        BufferRenderer.draw(buffer);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableCull();
    }

    public static void fill(MatrixStack matrices, Color c, double x1, double y1, double x2, double y2) {
        int color = c.getRGB();
        double j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void renderRoundedQuad(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double rad) {
        int color = c.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        int initial = -90;
        double[][] map = new double[][]{
                new double[]{toX1, toY1},
                new double[]{toX1, fromY1},
                new double[]{fromX1, fromY1},
                new double[]{fromX1, toY1}
        };
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            initial += 90;
            for (int r = initial; r < (360 / 4 + initial); r++) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(g, h, k, f).next();
            }
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void beginScissor(MatrixStack stack, double x, double y, double endX, double endY) {
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Vector4f coord = new Vector4f((float) x, (float) y, 0, 1);
        Vector4f end = new Vector4f((float) (endX), (float) (endY), 0, 1);
        coord.transform(matrix);
        end.transform(matrix);
        x = coord.getX();
        y = coord.getY();
        double width = end.getX() - x;
        double height = end.getY() - y;
        width = Math.max(0, width);
        height = Math.max(0, height);
        float d = (float) Shadow.c.getWindow().getScaleFactor();
        int ay = (int) ((Shadow.c.getWindow().getScaledHeight() - (y + height)) * d);
        RenderSystem.enableScissor((int) (x * d), ay, (int) (width * d), (int) (height * d));
    }

    public static void endScissor() {
        RenderSystem.disableScissor();
    }
}