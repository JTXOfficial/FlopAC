package me.jtx.flopac.tinyprotocol.packet.in;

import lombok.Getter;
import me.jtx.flopac.tinyprotocol.api.NMSObject;
import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;

@Getter
public class WrappedInArmAnimationPacket extends NMSObject {
    private static final String packet = Client.ARM_ANIMATION;

    public WrappedInArmAnimationPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
    }
}
