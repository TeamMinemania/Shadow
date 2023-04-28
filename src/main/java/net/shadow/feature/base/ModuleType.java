package net.shadow.feature.base;

import net.shadow.font.Texture;
import net.shadow.plugin.TextureUtils;

public enum ModuleType {
    COMBAT("Combat", TextureUtils.ICONS_COMBAT.getWhere()),
    GRIEF("Grief", TextureUtils.ICONS_GRIEF.getWhere()),
    CHAT("Chat", TextureUtils.ICONS_CHAT.getWhere()),
    ITEMS("Item", TextureUtils.ICONS_ADDON_PROVIDED.getWhere()),
    OTHER("Other", TextureUtils.ICONS_MISC.getWhere()),
    MOVEMENT("Movement", TextureUtils.ICONS_MOVE.getWhere()),
    RENDER("Render", TextureUtils.ICONS_RENDER.getWhere()),
    WORLD("World", TextureUtils.ICONS_WORLD.getWhere()),
    CRASH("Crash", TextureUtils.ICONS_CRASH.getWhere()),
    EXPLOIT("Exploit", TextureUtils.ICONS_EXPLOIT.getWhere());

    final String name;
    final Texture where;

    ModuleType(String n, Texture where) {
        this.name = n;
        this.where = where;
    }

    public Texture getWhere(){
        return where;
    }

    public String getName() {
        return name;
    }
}
