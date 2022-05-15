package me.jtx.flopac.checks.combat.velocity;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Velocity", punishmentVL = 10, checkType = "E", description = "Checks if the player spoofs on ground while taking kb")
public class VelocityE extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (user.getLastFallDamageTimer().hasNotPassed(20)
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().underBlockTicks > 0
                        || user.getLastFireTickTimer().hasNotPassed(20)
                        || user.getBlockData().collidesHorizontal
                        || user.getTick() < 60
                        || user.shouldCancel()) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double velocity = user.getCombatProcessor().getVelocityV();

                if (user.getCombatProcessor().getVelocityTicks() == 1) {
                    if (deltaY < 0.42f && velocity < 2 && velocity > 0.2) {
                        if (user.getMovementProcessor().isOnGround() && user.getMovementProcessor().isLastGround()) {
                            if (++threshold > 9.2f) {
                                flag(user, "Spoof ground velocity");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.75);
                        }
                    }
                }

                break;
            }
        }
    }
}
