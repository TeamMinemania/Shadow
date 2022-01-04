package me.x150.sipprivate.feature.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Quaternion;
import org.lwjgl.opengl.GL40C;

public class TestScreen extends Screen {
    public TestScreen() {
        super(Text.of(""));
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            double x = 50;
            double y = 50;
            double w = 30;
            double h = 30;
            matrices.push();
            matrices.translate(x + w / 2, y + h / 2, 0);
            matrices.multiply(new Quaternion(0, 0, (System.currentTimeMillis() % 1000) / 1000f * 360, true));
            Renderer.R2D.renderRoundedQuadInternal(matrices.peek().getPositionMatrix(), 0, 0, 0, 1, -w / 2, -h / 2, w / 2, h / 2, 5, 5);
            matrices.pop();
            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
            RenderSystem.setShaderTexture(0, DefaultSkinHelper.getTexture());
            Screen.drawTexture(matrices, 45, 45, 40, 40, 8, 8, 8, 8, 64, 64);
        });

        super.render(matrices, mouseX, mouseY, delta);
    }
}
