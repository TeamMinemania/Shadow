package net.shadow.feature;

import net.shadow.feature.base.Module;
import net.shadow.feature.module.*;
import net.shadow.feature.module.chat.Spammer;
import net.shadow.feature.module.combat.SuperKnockback;
import net.shadow.feature.module.combat.TeleportAura;
import net.shadow.feature.module.crash.ArmorStandCrash;
import net.shadow.feature.module.exploit.Fling;
import net.shadow.feature.module.grief.*;
import net.shadow.feature.module.movement.*;
import net.shadow.feature.module.other.FastThrow;
import net.shadow.feature.module.other.Retard;
import net.shadow.feature.module.other.ScreenSaver;
import net.shadow.feature.module.render.*;
import net.shadow.feature.module.world.*;
import net.shadow.feature.module.combat.Velocity;
import net.shadow.feature.module.crash.WindowCrash;
import net.shadow.feature.module.crash.SkriptCrash;
import net.shadow.feature.module.exploit.XCarry;
import net.shadow.feature.module.other.Unload;
import net.shadow.feature.module.world.Timer;

import java.util.*;

public class ModuleRegistry {
    static final List<Module> mods = new ArrayList<>();

    static {
        // mods.add(new AdvancedSpamModule());
        // mods.add(new BetterItemsModule());
        // mods.add(new BlockInteractModule());
        // mods.add(new BranchesModule());
        // mods.add(new BrickThisModule());
        // mods.add(new ColorBooksModule());
        // mods.add(new DebuggerModule());
        // mods.add(new GiveModule());
        // mods.add(new GriefTeamModule());
        // mods.add(new HitboxRemoverModule());
        // mods.add(new IrcModule());
        // mods.add(new NbtBypasserModule());
        // mods.add(new NeuralyzerModule());
        // mods.add(new OverloadModule());
        // mods.add(new ReverseReachModule());
        // mods.add(new TestModule());
        mods.add(new DodgeModule());
        mods.add(new GhostTeleportModule());
        mods.add(new LoginCrash());
        mods.add(new SpeederModule());
        mods.add(new BotMovement());
        mods.add(new AdBlockModule());
        mods.add(new AirJumpModule());
        mods.add(new AirPlaceModule());
        mods.add(new AnimationCrashModule());
        mods.add(new AnnihilatorModule());
        mods.add(new AntiAnvilModule());
        mods.add(new AntiBanModule());
        mods.add(new AntiCrashModule());
        mods.add(new AntiFlyKickModule());
        mods.add(new PacketFlyModule());
        mods.add(new AntiHungerModule());
        mods.add(new AuraModule());
        mods.add(new AutoFireballModule());
        mods.add(new AutoFishModule());
        mods.add(new AutoHitModule());
        mods.add(new AutoHologramModule());
        mods.add(new AutoLavaCast());
        mods.add(new AutoLeaveModule());
        mods.add(new AutoRunModule());
        mods.add(new AutoSprintModule());
        mods.add(new BSBDupe());
        mods.add(new BeaconDelimiterModule());
        mods.add(new BetterChatModule());
        mods.add(new BetterItemInfo());
        mods.add(new BetterSelectorsModule());
        mods.add(new BetterTooltipModule());
        mods.add(new BlinkFlyModule());
        mods.add(new BlinkModule());
        mods.add(new BlipModule());
        mods.add(new BlurModule());
        mods.add(new BlockEspModule());
        mods.add(new BlockZooperModule());
        mods.add(new BoatFlyModule());
        mods.add(new BoatFuckModule());
        mods.add(new BookCrashModule());
        //mods.add(new BookSpoofModule());
        mods.add(new BookWriterModule());
        mods.add(new BowInstaModule());
        mods.add(new BowSpam());
        mods.add(new BungeeCrashModule());
        //mods.add(new BungeeNodeCrash());
        mods.add(new ChatFilterModule());
        mods.add(new ChatTranslatorModule());
        mods.add(new ChestCrashModule());
        mods.add(new ChunksCrashModule());
        mods.add(new ClickGuiModule());
        mods.add(new ClickTPModule());
        mods.add(new ClientCrasherModule());
        mods.add(new ClientSpoofModule());
        mods.add(new CommandsModule());
        mods.add(new ConsoleSpammerModule());
        mods.add(new CoordsPointerModule());
        mods.add(new DeserializerCrash());
        mods.add(new CreativeGUIModule());
        mods.add(new CriticalsModule());
        mods.add(new CrystalAuraModule());
        mods.add(new CustomButtons());
        mods.add(new DecimatorModule());
        mods.add(new DisablerModule());
        mods.add(new DiscordRPCModule());
        mods.add(new DropperModule());
        mods.add(new DupeModule());
        //mods.add(new EgirlESPModule());
        mods.add(new ElytraflyModule());
        mods.add(new LecternCrash());
        mods.add(new EntityStormModule());
        mods.add(new EspModule());
        mods.add(new ExplosionModule());
        mods.add(new FastPlaceModule());
        mods.add(new FlightModule());
        mods.add(new ForceOpModule());
        mods.add(new ForceRequestsModule());
        mods.add(new ForceRideCrashModule());
        mods.add(new FreecamModule());
        mods.add(new FullbrightModule());
        mods.add(new GhostModeModule());
        mods.add(new GravatizerModule());
        mods.add(new HatClickModule());
        mods.add(new HitboxesModule());
        mods.add(new HudModule());
        mods.add(new ImageLoaderModule());
        mods.add(new InfinitePotionsModule());
        mods.add(new InfoDisplayModule());
        mods.add(new InstantCrashModule());
        mods.add(new InstantMineModule());
        mods.add(new InteracterModule());
        mods.add(new InvStealerModule());
        mods.add(new ItemSpoofModule());
        mods.add(new JesusModule());
        mods.add(new LagChestModule());
        mods.add(new LongJumpModule());
        mods.add(new MapFuckerModule());
        mods.add(new MinehutCrashModule());
        mods.add(new MoldoorModule());
        mods.add(new MoonJumpModule());
        mods.add(new MovementModule());
        mods.add(new MultiShotModule());
        mods.add(new MutebypassModule());
        mods.add(new NBTLoggerModule());
        mods.add(new NametagsModule());
        mods.add(new NettyCrashModule());
        mods.add(new NewChunksModule());
        mods.add(new NoClickEventModule());
        mods.add(new NoClipModule());
        mods.add(new NoFallModule());
        mods.add(new NoSlowModule());
        mods.add(new ChestVoiderModule());
        mods.add(new NoSrpModule());
        mods.add(new NoSwingModule());
        mods.add(new NukerModule());
        mods.add(new OnePacketCrashModule());
        mods.add(new OnlineServices());
        mods.add(new OtherScreenModule());
        mods.add(new PacketBlockerModule());
        mods.add(new PacketLimiterBypassModule());
        mods.add(new PacketLoggerModule());
        mods.add(new PacketPlayerCrashModule());
        mods.add(new PhaseModule());
        mods.add(new PlayerFinderModule());
        mods.add(new PlayerPositionModule());
        mods.add(new PluginsModule());
        mods.add(new PopBobCrashMapModule());
        mods.add(new PortalGodmodeModule());
        mods.add(new PortalsModule());
        mods.add(new PotionsModule());
        mods.add(new ProjectilesModule());
        mods.add(new PulseWavemaker());
        mods.add(new PulserModule());
        mods.add(new QuakeSpeed());
        //mods.add(new RCEModule());
        mods.add(new RandomInteractModule());
        mods.add(new ArmorStandCrash());
        mods.add(new ReachModule());
        mods.add(new Retard());
        mods.add(new Fling());
        mods.add(new Scaffold());
        mods.add(new ScreenSaver());
        mods.add(new ServerNuke());
        mods.add(new GriefGUI());
        //mods.add(new SignUpdaterModule());
        mods.add(new SignWriter());
        mods.add(new Spammer());
        mods.add(new Speed());
        mods.add(new Spider());
        mods.add(new SuperCrossbow());
        mods.add(new SuperKnockback());
        mods.add(new TabGui());
        mods.add(new TeleportAura());
        mods.add(new FastThrow());
        mods.add(new Timer());
        mods.add(new Titlescreen());
        mods.add(new Tracers());
        mods.add(new Unload());
        //mods.add(new UnrenderCrashModule());
        mods.add(new ErrorCrash());
        mods.add(new VanillaFly());
        mods.add(new Velocity());
        mods.add(new PersonHider());
        mods.add(new WallPeek());
        mods.add(new ViewChanges());
        //mods.add(new VillagerCrashModule());
        mods.add(new WallBlip());
        mods.add(new Wavedash());
        mods.add(new WindowCrash());
        mods.add(new SkriptCrash());
        mods.add(new ClientEdit());
        mods.add(new WorldPainter());
        mods.add(new XCarry());
        mods.add(new GUICrash());
        mods.sort(Comparator.comparing(Module::getName));
    }

    public static List<Module> getAll() {
        return mods;
    }

    public static Module getGrouping(Class<? extends Module> clazz) {
        for (Module module : mods) {
            if (module.getClass() == clazz)
                return module;
        }
        return null;
    }

    public static <T extends Module> T getByClass(Class<? extends Module> clazz) {
        Optional<Module> m = ModuleRegistry.getAll().stream().filter(module -> module.getClass() == clazz).findFirst();
        if (m.isEmpty())
            throw new RuntimeException("Module" + clazz.getName() + " not registered");
        return (T) m.get();
    }

    public static Module find(String n) {
        for (Module module : mods) {
            if (module.getName().equalsIgnoreCase(n))
                return module;
        }
        return null;
    }
}
