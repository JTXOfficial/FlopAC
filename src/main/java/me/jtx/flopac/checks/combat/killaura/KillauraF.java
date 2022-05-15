package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jtx.flopac.util.Verbose;

@CheckInformation(checkName = "Killaura", checkType = "F", lagBack = false, description = "Hiss miss ratio", punishmentVL = 25)
public class KillauraF extends Check {

    private double swings, attacks;
    private Verbose threshold = new Verbose();

    /**
     * Hit miss ratio detection, currently trying to find a better way of detecting this.
     */

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.getCombatProcessor().getCancelTicks() > 0) {
                        attacks = 0;
                        swings = 0;
                        return;
                    }

                    double yawDiff = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());
                    if (yawDiff > 3.5f && yawDiff < 120 && user.getMovementProcessor().getDeltaXZ() > 0.1
                            && user.getCombatProcessor().isInsideHitbox() && user.getCombatProcessor().getCancelTicks() < 1) {
                        ++attacks;
                    }

                }
                break;
            }

            case Packet.Client.ARM_ANIMATION: {
                if (user.shouldCancel()
                        || user.getTick() < 60) {
                    threshold.setVerbose(0);
                    attacks = 0;
                    swings = 0;
                    return;
                }

                if (swings > 100) {
                    swings = attacks = 0;
                }

                ++swings;

                double ratio = (attacks / swings) * 100;

                if (ratio < 50) {
                    ratio = 50;
                }

                if (ratio > 75 && attacks > 5 && swings > 5) {
                    if (threshold.flag(4, 3000L)) {
                        flag(user, "Aim is to accurate [H:M]");
                    }
                }

                break;
            }
        }
    }
}