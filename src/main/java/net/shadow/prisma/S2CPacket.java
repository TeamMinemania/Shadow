package net.shadow.prisma;

public abstract class S2CPacket {
    private String packetName;
    private boolean moletunnelNeeded;
    public S2CPacket(String packetName) {
        this.packetName = packetName;
        this.moletunnelNeeded = false;
    }
    public S2CPacket(String packetName, boolean moletunnelNeeded) {
        this.packetName = packetName;
        this.moletunnelNeeded = moletunnelNeeded;
    }
    public abstract void on(Object[] args);

    public boolean isMoletunnelOnly() {
        return moletunnelNeeded;
    }

    public String getPacketName() {
        return packetName;
    }
}
