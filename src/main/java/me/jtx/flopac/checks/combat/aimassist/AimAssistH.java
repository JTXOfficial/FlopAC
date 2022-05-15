package me.jtx.flopac.checks.combat.aimassist;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "AimAssist", checkType = "H", lagBack = false, punishmentVL = 5)
public class AimAssistH extends Check {

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

                    double deltaYaw = user.getMovementProcessor().getYawDeltaClamped();

                    double deltaMouse = Math.abs(deltaYaw - user.getMouseDeltaX());

                    if (deltaMouse > 60.0 && deltaMouse != 360 && deltaMouse < 360) {
                        if (deltaYaw >= 100) {
                            flag(user, "Head Snapping", "dm="+deltaMouse + ", y=" + deltaYaw);
                        }
                    }

                    break;
                }
            }
        }
    }
}
