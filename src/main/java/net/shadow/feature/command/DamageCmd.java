package net.shadow.feature.command;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.Shadow;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import net.shadow.utils.ChatUtils;

public class DamageCmd extends Command {
    public DamageCmd() {
        super("damage", "damage yourself");
    }

    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return List.of(new String[]{"5"});
        }
        return List.of(new String[0]);
    }

    @Override
    public void call(String[] args) {
        if (args.length != 1) {
            ChatUtils.message("Please use the format >damage <amount>");
            return;
        }

        if (Shadow.c.player.getAbilities().creativeMode) {
            ChatUtils.message("you cannot damage in creative mode");
            return;
        }

        int amount = parseAmount(args[0]);
        if (amount == 0) return;
        applyDamage(amount);
        NotificationSystem.notifications.add(new Notification("Damage", "Applied Damage", 150));
    }

    private int parseAmount(String dmgString) {
        try {
            return Integer.parseInt(dmgString);
        } catch (Exception e) {
            {
                ChatUtils.message("Not a number!");
                return 0;
            }
        }

    }

    private void applyDamage(int amount) {
        Vec3d pos = Shadow.c.player.getPos();

        for (int i = 0; i < 80; i++) {
            sendPosition(pos.x, pos.y + amount + 2.1, pos.z, false);
            sendPosition(pos.x, pos.y + 0.05, pos.z, false);
        }

        sendPosition(pos.x, pos.y, pos.z, true);
    }

    private void sendPosition(double x, double y, double z, boolean onGround) {
        Shadow.c.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
    }
}
