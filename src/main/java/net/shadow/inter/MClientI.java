package net.shadow.inter;

public interface MClientI {

    int getItemUseCooldown();

    void setItemUseCooldown(int itemUseCooldown);

    IClientPlayerEntity getPlayer();

    IClientPlayerInteraction getInteractions();
}