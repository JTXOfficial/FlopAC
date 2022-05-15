package me.jtx.flopac.checks.old;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.util.EntityUtil;

@CheckInformation(checkName = "Flight", checkType = "C", lagBack = true, description = "Checks if the player is jumping higher than usual")
public class FlightC extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)
                        || user.getBlockData().slimeTimer.hasNotPassed(20)
                        || checkConditions(user)) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double maxJumpHeight = 0.42F + (user.getPotionProcessor().getJumpAmplifier() * 0.2D);

                if (user.getBlockData().stairSlabTimer.hasNotPassed(20)
                        || user.getBlockData().fenceTicks > 0) {
                    maxJumpHeight = 0.5;
                }

                if (EntityUtil.isNearBoat(user)) {
                    maxJumpHeight = 0.6000000238418579D;
                }

                if (user.getBlockData().bedTicks > 0) {
                    maxJumpHeight = 0.5625F;
                }

                if (!user.getMovementProcessor().isOnGround() && user.getMovementProcessor().isLastGround()) {
                    if (deltaY > maxJumpHeight) {
                        flag(user, "Jumping Higher Than Legit ", "" + deltaY, "" + maxJumpHeight);
                    }
                }
            }
        }
    }
    boolean checkConditions(User user) {
        return user.getBlockData().waterTicks > 0
                || user.getTick() < 60
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().climbableTimer.hasNotPassed();
    }
}
