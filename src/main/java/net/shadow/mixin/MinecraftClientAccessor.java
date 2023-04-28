package net.shadow.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Session;

import java.net.Proxy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

    @Mutable
    @Accessor("session")
    void setSession(Session session);

    @Accessor("renderTickCounter")
    RenderTickCounter getRenderTickCounter();
    @Accessor("networkProxy")
    Proxy getProxy();
}
