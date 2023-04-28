package net.shadow.feature;

import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.feature.command.*;

import java.util.ArrayList;

public class CommandRegistry {
    private static final List<Command> reg = new ArrayList<>();

    public static void init() {
        reg.add(new gwatCmd());
        reg.add(new GlueAuraCmd());
        reg.add(new LwatCmd());
        reg.add(new AddItemCmd());
        reg.add(new ApvelCmd());
        reg.add(new AsConsoleCmd());
        reg.add(new AuthorCmd());
        reg.add(new BackdoorCmd());
        reg.add(new BackdoorConsolePrefixCmd());
        reg.add(new BackdoorPrefixCmd());
        reg.add(new BanCmd());
        reg.add(new BaseCmd());
        reg.add(new BossbarCmd());
        reg.add(new BringCmd());
        reg.add(new BypassShulkerCmd());
        reg.add(new CPermCmd());
        reg.add(new CheckCmdCmd());
        reg.add(new ClearItemsCmd());
        reg.add(new ClientFloodCmd());
        reg.add(new ClipCmd());
        reg.add(new CrashCmd());
        reg.add(new DamageCmd());
        reg.add(new DropCmd());
        reg.add(new EnchantCmd());
        reg.add(new EquipCmd());
        reg.add(new EvclipCmd());
        reg.add(new FindCmd());
        reg.add(new FireballCmd());
        reg.add(new FlingCmd());
        reg.add(new FloodLPCmd());
        reg.add(new ForceOPCmd());
        reg.add(new ForceTeleportCmd());
        reg.add(new FreezeCmd());
        reg.add(new FriendsCmd());
        reg.add(new GamemodeCmd());
        reg.add(new GetCmd());
        reg.add(new GetIpCmd());
        reg.add(new GhostItemCmd());
        reg.add(new GiveCmd());
        reg.add(new HClipCmd());
        reg.add(new HelpCmd());
        reg.add(new HoldCmd());
        reg.add(new HoloCmd());
        reg.add(new ImageCmd());
        reg.add(new InjectCmd());
        reg.add(new ItemCmd());
        reg.add(new ItemDataCmd());
        reg.add(new KickAllCmd());
        reg.add(new KickCmd());
        reg.add(new KickselfCmd());
        reg.add(new KillCmd());
        reg.add(new LagCmd());
        reg.add(new LinkPlayerCmd());
        reg.add(new LockdownCmd());
        reg.add(new MoletunnelCmd());
        reg.add(new MoveCmd());
        reg.add(new OpenCmd());
        reg.add(new PeekCmd());
        reg.add(new PoofCmd());
        reg.add(new PopCmd());
        reg.add(new RHoloCmd());
        reg.add(new RenameCmd());
        reg.add(new RepairCmd());
        reg.add(new SCrashCmd());
        reg.add(new SayCmd());
        reg.add(new ServerCmd());
        reg.add(new ShulkerCmd());
        reg.add(new SocketKickCmd());
        reg.add(new StopCmd());
        reg.add(new ToggleCmd());
        reg.add(new UnstuckCmd());
        reg.add(new VClipCmd());
        reg.add(new ViewNbtCmd());
        reg.add(new VoidCmd());
        reg.add(new StuckShulkerCmd());
        reg.add(new BoatKillCmd());
    }

    public static List<Command> getList() {
        return reg;
    }

    public static Command find(String name) {
        for (Command c : reg) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
}
