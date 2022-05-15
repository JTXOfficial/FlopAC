package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "I", lagBack = false, description = "Inventory Attack/Interact", punishmentVL = 3)
public class KillauraI extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.USE_ENTITY: {

                WrappedInUseEntityPacket useEntityPacket =
                        new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                switch (useEntityPacket.getAction()) {
                    case ATTACK:
                    case INTERACT:
                    case INTERACT_AT: {
                        if (user.getMovementProcessor().isInInventory()) {
                            flag(user, "Attacking / Interacting while in inventory");
                        }
                        break;
                    }
                }

                break;
            }
        }
    }
}