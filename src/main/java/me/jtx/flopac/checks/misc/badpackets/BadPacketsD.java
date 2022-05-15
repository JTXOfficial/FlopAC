package me.jtx.flopac.checks.misc.badpackets;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;

@CheckInformation(checkName = "BadPackets", checkType = "D", lagBack = false, canPunish = false)
public class BadPacketsD extends Check {

    private int lastSlot;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Client.HELD_ITEM_SLOT: {
                User user = event.getUser();

                if (user.shouldCancel() || user.getLastTeleportTimer().hasNotPassed(20)) {
                    threshold = 0;
                    return;
                }

                WrappedInHeldItemSlotPacket heldItemSlot = new WrappedInHeldItemSlotPacket(event.getPacket(), user.getPlayer());

                int slot = heldItemSlot.getSlot();

                if (slot == lastSlot) {
                    if (threshold++ > 1) {
                        flag(user, "Invalid slot packet");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);
                }

                lastSlot = slot;
                break;
            }
        }
    }
}