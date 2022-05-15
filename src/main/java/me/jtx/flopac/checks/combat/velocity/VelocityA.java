package me.jtx.flopac.checks.combat.velocity;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Velocity", canPunish = false, description = "0% Vertical Velocity")
public class VelocityA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (user.getCurrentLocation().getY() > user.getLastLocation().getY()
                        || user.getLastFallDamageTimer().hasNotPassed(20)
                        || user.getVehicleTicks() > 0
                        || user.getTick() < 60
                        || user.getLastFireTickTimer().hasNotPassed(20)
                        || user.getBlockData().collidesHorizontal
                        || user.shouldCancel()) {
                    threshold = 0;
                    return;
                }


                double deltaY = user.getMovementProcessor().getDeltaY();

                double velocity = user.getCombatProcessor().getVelocityV();

                if (user.getLastAttackByEntityTimer().hasNotPassed(20)
                        || user.getLastShotByArrowTimer().hasNotPassed(20)) {

                    if (user.getCombatProcessor().getVelocityTicks() == 1
                            && user.getMovementProcessor().isLastGround()) {

                        if ((deltaY / velocity) == 0.0) {
                            if (threshold++ > 2) {
                                flag(user, "No Vertical Knockback");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.0626f);
                        }
                    }
                }

                break;
            }
        }
    }
}
