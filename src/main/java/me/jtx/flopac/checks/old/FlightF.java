package me.jtx.flopac.checks.old;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.util.EntityUtil;

@CheckInformation(checkName = "Flight", checkType = "F", description = "Checks if the player tries to spoof on ground.")
public class FlightF extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (this.checkConditions(user)
                        || user.getLastBlockPlaceTimer().hasNotPassed(20)
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getBlockData().snowTicks > 0
                        || EntityUtil.isOnBoat(user)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(5)
                        || user.getLastTeleportTimer().hasNotPassed(20)) {
                    this.threshold = 0;
                    return;
                }

                if (user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        && user.getMovementProcessor().getDeltaXZ() < 0.39) {
                    threshold -= Math.min(threshold, 0.90);
                }

                if (user.getMovementProcessor().getDeltaY() < 0.0748) {
                    if (user.getMovementProcessor().isLastGround() && !user.getMovementProcessor().isServerYGround()) {

                        if ((threshold += 1.25) >= 4.3) {
                            flag(user, "Spoofing Ground");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.001f);
                    }
                }
            }
        }
    }

    boolean checkConditions(User user) {
        return user.getBlockData().waterTicks > 0
                || user.getTick() < 60
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0;
    }
}
