package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "Q", lagBack = false, description = "Dig Synced With Attack")
public class KillauraQ extends Check {

    private int digTick, attackTick;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    attackTick++;
                }
                break;
            }

            case Packet.Client.BLOCK_DIG: {
                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    digTick++;
                }
                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (digTick == attackTick && digTick > 0) {
                    if (++threshold > 3) {
                        flag(user, "Invalid release use item packet");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.025);
                }


                this.digTick = this.attackTick = 0;
                break;
            }
        }
    }
}