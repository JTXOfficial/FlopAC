package me.jtx.flopac.checks.misc.inventory;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInWindowClickPacket;

@CheckInformation(checkName = "Inventory", checkType = "B", lagBack = false, canPunish = false)
public class InventoryB extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.WINDOW_CLICK: {
                User user = event.getUser();

                if (user.shouldCancel() || user.getTick() < 60 || !user.isChunkLoaded()) {
                    return;
                }

                WrappedInWindowClickPacket clickPacket =
                        new WrappedInWindowClickPacket(event.getPacket(), user.getPlayer());

                if (clickPacket.getId() == 0) {
                    if (!user.getMovementProcessor().isInInventory()) {
                        flag(user, "Clicking in inventory while its not open");
                        user.getPlayer().closeInventory();
                        user.getMovementProcessor().setInInventory(false);
                    }
                }
                break;
            }
        }
    }
}
