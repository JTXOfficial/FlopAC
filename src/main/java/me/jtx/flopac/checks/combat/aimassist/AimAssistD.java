package me.jtx.flopac.checks.combat.aimassist;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jtx.flopac.util.MathUtil;

@CheckInformation(checkName = "AimAssist", checkType = "D", lagBack = false, punishmentVL = 25, canPunish = false)
public class AimAssistD extends Check {

    private double threshold;

    private float lastPitchDifference;
    private float lastYawDifference;

    private final double offset = Math.pow(2.0, 24.0);
    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                float pitchDifference = Math.abs(user.getCurrentLocation().getPitch()
                        - user.getLastLocation().getPitch());

                float yawDifference = Math.abs(user.getCurrentLocation().getYaw()
                        - user.getLastLocation().getYaw());

                float yawAccel = Math.abs(pitchDifference - lastPitchDifference);
                float pitchAccel = Math.abs(yawDifference - lastYawDifference);

                long gcd = MathUtil.gcd((long) (pitchDifference * offset), (long) (lastPitchDifference * offset));


                if (user.getCombatProcessor().getCancelTicks() > 0) {
                    threshold = 0;
                    return;
                }

                if (yawDifference > 2.0F && yawAccel > 1.0F && pitchAccel > 0.0F && pitchDifference > 0.009f) {

                    if (gcd < 131072L && pitchAccel < 6.5) {
                        threshold += 0.89;

                        if (threshold > 12.5) {
                            flag(user);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.25);
                    }
                }

                lastYawDifference = yawDifference;
                lastPitchDifference = pitchDifference;
            }
        }
    }
}
