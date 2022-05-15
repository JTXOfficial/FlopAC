/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package me.jtx.flopac.tinyprotocol.packet.in;

import lombok.Getter;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import me.jtx.flopac.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedInVelocityPacket extends Packet {
    private static final String packet = Client.FLYING;

    // Fields
    private static final FieldAccessor<Integer> fieldX = fetchField(packet, int.class, 0);
    private static final FieldAccessor<Integer> fieldY = fetchField(packet, int.class, 1);
    private static final FieldAccessor<Integer> fieldZ = fetchField(packet, int.class, 2);

    // Decoded data
    private double x, y, z;

    public WrappedInVelocityPacket(Object packet) {
        super(packet);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        x = fieldX.get(getPacket());
        y = fieldY.get(getPacket());
        z = fieldZ.get(getPacket());
    }
}
