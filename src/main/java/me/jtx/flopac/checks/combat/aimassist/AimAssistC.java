package me.jtx.flopac.checks.combat.aimassist;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "AimAssist", checkType = "C", lagBack = false, punishmentVL = 10, canPunish = false)
public class AimAssistC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                double pitch = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());
                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (pitch > 3.0 && yaw < 0.0001D) {
                    if (threshold++ > 3) {
                        flag(user, "Low Yaw Change");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);
                }
            }
        }
    }
}
