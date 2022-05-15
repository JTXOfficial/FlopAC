package me.jtx.flopac.checks.combat.hitbox;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Hitbox", lagBack = false, punishmentVL = 30)
public class HitboxA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 120
                        || user.getConnectionProcessor().isLagging()
                        || user.getCombatProcessor().getCancelTicks() > 0
                        || user.shouldCancel()) {
                    threshold = 0;
                    return;
                }

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(1)) {
                    if (!user.getCombatProcessor().isInsideHitbox()) {
                        if (threshold++ > 7) {
                            flag(user, "Expanded Hitbox");
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
