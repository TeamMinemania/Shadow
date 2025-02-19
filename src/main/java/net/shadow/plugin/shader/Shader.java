package net.shadow.plugin.shader;

import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class Shader {
    final ManagedShaderEffect effect;

    private Shader(Identifier ident, Consumer<ManagedShaderEffect> init) {
        this.effect = ShaderEffectManager.getInstance().manage(ident, init);
    }

    public static Shader create(String progName, Consumer<ManagedShaderEffect> callback) {
        return new Shader(new Identifier("shadow", String.format("shaders/post/%s.json", progName)), callback);
    }

    public ManagedShaderEffect getEffect() {
        return effect;
    }

    public void render(float delta) {
        effect.render(delta);
    }
}
