package me.jtx.flopac.checks.movement.step;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.EntityUtil;

@CheckInformation(checkName = "Step", description = "Checks if player goes up blocks higher than legit", canPunish = false)
public class StepA extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getBlockData().waterTicks > 0
                        || user.getTick() < 60
                        || user.shouldCancel()
                        || user.getBlockData().bedTicks > 0
                        || user.getBlockData().slabTicks > 0
                        || user.getBlockData().stairTicks > 0
                        || user.getBlockData().fenceTicks > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().piston
                        || user.getBlockData().carpetTicks > 0
                        || user.getBlockData().underBlock
                        || user.getBlockData().webTicks > 0
                        || EntityUtil.isOnBoat(user)
                        || !user.isChunkLoaded()
                        || user.getVehicleTicks() > 0
                        || user.getLastFallDamageTimer().hasNotPassed(20)
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();
                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                boolean ground = user.getMovementProcessor().isOnGround();

                if (deltaY > 0.0 && lastDeltaY > 0.0 && ground) {
                    flag(user, "Going up blocks abnormal (1)");
                } else if (deltaY > 0.42f && lastDeltaY >= 0.0 && ground) {
                    flag(user, "Going up blocks abnormal (2)");
                }
            }
        }
    }
}
