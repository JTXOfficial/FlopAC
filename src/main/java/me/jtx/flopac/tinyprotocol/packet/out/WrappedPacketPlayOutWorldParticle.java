package me.jtx.flopac.tinyprotocol.packet.out;

import lombok.Getter;
import me.jtx.flopac.tinyprotocol.api.NMSObject;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import me.jtx.flopac.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.jtx.flopac.tinyprotocol.api.packets.reflections.types.WrappedMethod;
import me.jtx.flopac.tinyprotocol.packet.types.WrappedEnumParticle;

@Getter
public class WrappedPacketPlayOutWorldParticle extends NMSObject {

    private WrappedEnumParticle type;
    private static WrappedClass craftParticle, particle;
    private static WrappedMethod toNMS;
    private static String packetPlayOutWorldParticle = Packet.Server.WORLD_PARTICLE;
    private boolean j;
    private float x;
    private float y;
    private float z;
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float speed;
    private int amount;
    private int[] data;

    public WrappedPacketPlayOutWorldParticle(WrappedEnumParticle type, boolean var2, float x, float y, float z, float xOffset, float yOffset, float zOffset, float speed, int amount, int... data) {
        this.type = type;
        this.j = var2;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.speed = speed;
        this.amount = amount;
        this.data = data;

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            setPacket(packetPlayOutWorldParticle, type.getName().toLowerCase(), x, y, z, xOffset, yOffset, zOffset, speed, amount);
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            setPacket(packetPlayOutWorldParticle, type.toNMS(), var2, x, y, z, xOffset, yOffset, zOffset, speed, amount, data);
        } else {
            setPacket(packetPlayOutWorldParticle, x, y, z, xOffset, yOffset, zOffset, speed, amount,
                    var2, type.toNMS());
        }
    }

    @Override
    public void updateObject() {

    }
}