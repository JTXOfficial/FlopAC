package me.jtx.flopac.checks.misc.scaffold;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Scaffold", checkType = "G", lagBack = false, description = "Inventory Blocking", punishmentVL = 3)
public class ScaffoldG extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.BLOCK_PLACE: {

                if (user.shouldCancel() || user.getTick() < 60) {
                    return;
                }

                if (user.getMovementProcessor().isInInventory()) {
                    devFlag(user, "Blocking/Placing blocks while in inventory");
                }
                break;
            }
        }
    }
}