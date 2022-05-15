package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "N", lagBack = false, description = "No Interact Check")
public class KillauraN extends Check {

    private int interactTick, attackTick;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT) {
                    interactTick = user.getTick();
                }

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    attackTick = user.getTick();
                }
                break;
            }

            case Packet.Client.BLOCK_DIG: {
                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(1)) {
                        double changeTick = Math.abs(attackTick - interactTick);

                        if (changeTick > 100) {
                            if (++threshold > 1) {
                                flag(user, "No interact while blocking");
                            }
                        } else {
                            threshold = 0;
                        }
                    }
                }
                break;
            }
        }
    }
}