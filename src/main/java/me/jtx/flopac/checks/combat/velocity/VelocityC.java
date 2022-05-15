package me.jtx.flopac.checks.combat.velocity;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;

@CheckInformation(checkName = "Velocity",  checkType = "C", canPunish = false, description = "99% Horizontal Velocity Detection")
public class VelocityC extends Check {

    private double threshold, thresholdKBTick;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().collidesHorizontal
                        || user.getBlockData().underBlockTicks > 0
                        || user.getBlockData().lavaTicks > 20
                        || !user.isChunkLoaded()) {
                    threshold = 0;
                    return;
                }

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double velocityH = user.getCombatProcessor().getVelocityH();

                velocityH -= MathUtil.movingFlyingV3(user, false);

                double totalVelocity = deltaXZ / velocityH;

                if (totalVelocity < 0.99 && user.getCombatProcessor().getVelocityTicks() == 1) {
                    if (++threshold > 3) {
                        flag(user, "v="+totalVelocity);
                    }
                } else {
                    threshold -= Math.min(threshold, 0.025);
                }

                break;
            }
        }
    }
}
