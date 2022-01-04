package me.x150.sipprivate.feature.gui.screen;

import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class AntiAliasedScreen extends Screen {
    int samples;

    public AntiAliasedScreen(int samples) {
        super(Text.of(""));
        this.samples = samples;
    }

    protected abstract void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta);

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (samples != -1) {
            MSAAFramebuffer.use(samples, () -> renderInternal(matrices, mouseX, mouseY, delta));
        } else {
            renderInternal(matrices, mouseX, mouseY, delta);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}
