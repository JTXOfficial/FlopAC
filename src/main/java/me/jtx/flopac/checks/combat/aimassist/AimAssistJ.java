package me.jtx.flopac.checks.combat.aimassist;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "AimAssist", checkType = "J", canPunish = false, punishmentVL = 10)
public class AimAssistJ extends Check {

    private double threshold;
    private double lastDeltaYaw;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {

                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.getTick() < 60 || user.shouldCancel()) {
                        return;
                    }

                    double yaw = user.getMovementProcessor().getYawDeltaClamped();

                    double difference = Math.abs(yaw - lastDeltaYaw);

                    if (difference == 0 && yaw > 2.0 && lastDeltaYaw > 2.0) {
                        if (threshold++ > 6) {
                            flag(user, "Invalid yaw speed");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.25);
                    }


                    lastDeltaYaw = yaw;
                }
                break;
            }
        }
    }
}
