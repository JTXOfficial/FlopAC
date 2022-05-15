package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "O", canPunish = false, description = "Switch Aura Check")
public class KillauraO extends Check {

    private int threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (user.getCombatProcessor().getLastAttackedEntity() != null
                            && user.getCombatProcessor().getLastLastAttackedEntity() != null) {
                        if (user.getCombatProcessor().getLastAttackedEntity().getEntityId() !=
                                user.getCombatProcessor().getLastLastAttackedEntity().getEntityId()) {

                            if (++threshold > 2) {
                                flag(user, "Multi-Aura/Switching between entities rapidly");
                            }
                        } else {
                            threshold = 0;
                        }
                    } else {
                        threshold = 0;
                    }
                }

                break;
            }


            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                if (user.getCombatProcessor().getUseEntityTimer().passed(0)) {
                    threshold = 0;
                }
                break;
            }
        }
    }
}