package net.shadow.plugin.shader;

public class ShaderSystem {
    public static final Shader BLUR = Shader.create("blur", managedShaderEffect -> managedShaderEffect.setUniformValue("radius", 5f));
}
