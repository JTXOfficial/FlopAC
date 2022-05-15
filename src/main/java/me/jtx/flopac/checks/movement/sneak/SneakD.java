package me.jtx.flopac.checks.movement.sneak;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInEntityActionPacket;

@CheckInformation(checkName = "Sneak", checkType = "D", description = "Checks if lots of sneak packets are sent at once")
public class SneakD extends Check {

    private double sneakTicks, threshold;

    @Override
    public void onPacket(PacketEvent event) {

        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.POSITION:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LOOK: {

                if (user.shouldCancel()
                        || !user.isChunkLoaded()
                        || user.getTick() < 60) {
                    return;
                }

                if (this.sneakTicks > 1) {
                    if (threshold++ > 2) {
                        this.flag(user, "Large amounts of sneak packets");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.04f);
                }

                this.sneakTicks = 0;
                break;
            }

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket actionPacket =
                        new WrappedInEntityActionPacket(event.getPacket(), user.getPlayer());

                if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING) {
                    this.sneakTicks++;
                } else if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SNEAKING) {
                    this.sneakTicks++;
                }
                break;
            }
        }
    }
}
