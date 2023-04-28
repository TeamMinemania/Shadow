package net.shadow.feature.module.grief;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.BooleanValue;

public class PersonHider extends Module {
    private static boolean enabel = false;
    private static boolean onlm = false;
    final BooleanValue onlyme = this.config.create("OnlyYou", false);

    public PersonHider() {
        super("PersonHider", "hides everyone", ModuleType.GRIEF);
    }

    public static String protect(String string) {
        if (!enabel || Shadow.c.player == null)
            return string;

        String me = Shadow.c.getSession().getUsername();
        if (string.contains(me))
            return string.replace(me, "User\u00a7r");

        if (onlm) return string;

        int i = 0;
        for (PlayerListEntry info : Shadow.c.player.networkHandler.getPlayerList()) {
            i++;
            String name =
                    info.getProfile().getName().replaceAll("\u00a7(?:\\w|\\d)", "");

            if (string.contains(name))
                return string.replace(name, "\u00a7oPlayer" + i + "\u00a7r");
        }

        for (AbstractClientPlayerEntity player : Shadow.c.world.getPlayers()) {
            i++;
            String name = player.getName().getString();

            if (string.contains(name))
                return string.replace(name, "\u00a7oPlayer" + i + "\u00a7r");
        }

        return string;
    }

    @Override
    public void onEnable() {
        enabel = true;
    }

    @Override
    public void onDisable() {
        enabel = false;
    }

    @Override
    public void onUpdate() {
        onlm = onlyme.getThis();
    }

    @Override
    public void onRender() {

    }
}
