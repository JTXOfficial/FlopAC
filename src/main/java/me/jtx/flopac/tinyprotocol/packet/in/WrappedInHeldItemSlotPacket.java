package me.jtx.flopac.tinyprotocol.packet.in;

import lombok.Getter;
import me.jtx.flopac.tinyprotocol.api.NMSObject;
import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import me.jtx.flopac.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedInHeldItemSlotPacket extends NMSObject {
    private static final String packet = Client.HELD_ITEM;

    // Fields
    private static final FieldAccessor<Integer> fieldHeldSlot = fetchField(packet, int.class, 0);

    // Decoded data
    private int slot;


    public WrappedInHeldItemSlotPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        slot = fetch(fieldHeldSlot);
    }
}
