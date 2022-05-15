package me.jtx.flopac.checks.movement.jesus;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Jesus", canPunish = false, description = "Checks if the player is walking on water")
public class JesusA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getVehicleTicks() > 0
                        || !user.isChunkLoaded()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    threshold = 0;
                    return;
                }

                if (user.getBlockData().nearWater || user.getBlockData().nearLava) {
                    double deltaY = user.getMovementProcessor().getDeltaY();

                    if (user.getBlockData().underBlockTicks > 0 && deltaY == 0.0) {
                        threshold -= 1;
                    }

                    if (deltaY > -0.02 && deltaY < 0.02) {
                        if (!user.getBlockData().onGround) {
                            if (threshold++ > 12) {
                                flag(user, "Abnormal movements in water");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.75);
                        }
                    } else {
                        threshold -= Math.min(threshold, 1.75);
                    }
                } else {
                    threshold -= Math.min(threshold, 1.0);
                }

                break;
            }
        }
    }
}