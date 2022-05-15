package me.jtx.flopac.checks.movement.wtap;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInEntityActionPacket;

@CheckInformation(checkName = "WTap", checkType = "B", description = "Post unsprint")
public class WTapB extends Check {

    private double threshold;
    private long lastFlying;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                lastFlying = System.currentTimeMillis();

                break;
            }

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket actionPacket =
                        new WrappedInEntityActionPacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || user.getLastTeleportTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())
                        || !user.isChunkLoaded()
                        || user.getConnectionMap().size() > 1
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())) {
                    return;
                }

                if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING) {
                    if ((System.currentTimeMillis() - lastFlying) < 5L) {
                        if (++threshold > 5) {
                            flag(user, "Post Sprint");
                        }
                    } else {
                        threshold = 0;
                    }
                }
                break;
            }
        }
    }
}