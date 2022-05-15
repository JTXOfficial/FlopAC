package me.jtx.flopac.checks.combat.aimassist;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "AimAssist", checkType = "I", punishmentVL = 10, canPunish = false)
public class AimAssistI extends Check {

    private double threshold;

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

                    double deltaPitch = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());

                    double mouseY = user.getMouseDeltaY();

                    if (mouseY > 10000 && deltaPitch < 4 && deltaPitch > 0.2) {
                        if (threshold++ > 5) {
                            flag(user, "Invalid pitch changes");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.6f);
                    }

                }
                break;
            }
        }
    }
}
