package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "E", lagBack = false, description = "Check if player attacks while dead", punishmentVL = 2)
public class KillauraE extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.shouldCancel()
                            || user.getConnectionProcessor().isLagging()
                            || user.getTick() < 60) {

                        threshold = 0;
                        return;
                    }

                    if (user.getPlayer().isDead()) {
                        if (threshold++ > 4) {
                            flag(user, "Attacked while dead?");
                        }
                    } else {
                        threshold = 0;
                    }

                }
                break;
            }
        }
    }
}