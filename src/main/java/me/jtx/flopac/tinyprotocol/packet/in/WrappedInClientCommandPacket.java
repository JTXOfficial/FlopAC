package me.jtx.flopac.tinyprotocol.packet.in;

import lombok.Getter;
import me.jtx.flopac.tinyprotocol.api.NMSObject;
import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import me.jtx.flopac.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedInClientCommandPacket extends NMSObject {
    private static final String packet = Client.CLIENT_COMMAND;

    // Fields
    private static final FieldAccessor<Enum> fieldCommand = fetchField(packet, Enum.class, 0);

    // Decoded data
    EnumClientCommand command;

    public WrappedInClientCommandPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        command = EnumClientCommand.values()[fetch(fieldCommand).ordinal()];
    }

    public enum EnumClientCommand {
        PERFORM_RESPAWN,
        REQUEST_STATS,
        OPEN_INVENTORY_ACHIEVEMENT;

        EnumClientCommand() {
        }
    }
}
