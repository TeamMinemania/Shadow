package net.shadow.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.Text;
import net.shadow.feature.ModuleRegistry;
import net.shadow.utils.SignUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public class SignMixin extends Screen {

    private static String variable;
    @Shadow
    @Final
    private String[] text;

    protected SignMixin(Text title) {
        super(title);
    }

    @Inject(at = {@At("HEAD")}, method = {"init()V"})
    private void onInit(CallbackInfo ci) {
        if (ModuleRegistry.find("SignWriter").isEnabled()) {
            String special = ModuleRegistry.find("SignWriter").getSpecial();
            if (special.contains(":")) {
                try {
                    String[] the = ModuleRegistry.find("SignWriter").getSpecial().split(":");
                    text[0] = the[0];
                    text[1] = the[1];
                    text[2] = the[2];
                    text[3] = the[3];
                    finishEditing();
                } catch (Exception e) {
                    finishEditing();
                }
            }
            if (special.equalsIgnoreCase("overload")) {
                text[0] = SignUtils.getText();
                text[1] = "";
                text[2] = "";
                text[3] = "";
                finishEditing();
            }
            if (special.equalsIgnoreCase("crash")) {
                text[0] = SignUtils.getABanText();
                text[1] = SignUtils.getABanText();
                text[2] = SignUtils.getABanText();
                text[3] = SignUtils.getABanText();
                finishEditing();
            }
            if (special.equalsIgnoreCase("render")) {
                text[0] = "{}".repeat(50);
                text[1] = "{}".repeat(50);
                text[2] = "{}".repeat(50);
                text[3] = "{}".repeat(50);
                finishEditing();
            }
            if (special.equalsIgnoreCase("dupe")) {
                for (int i = 0; i < 4; i++) {
                    text[i] = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".repeat(10);
                }
                finishEditing();
            }

        }
    }

    @Shadow
    private void finishEditing() {

    }
}
