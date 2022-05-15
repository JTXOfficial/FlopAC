package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockPlacePacket;

@CheckInformation(checkName = "Killaura", checkType = "R", lagBack = false, description = "Invalid Block Values")
public class KillauraR extends Check {

    private int blockX, blockY, blockZ;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.BLOCK_PLACE: {
                WrappedInBlockPlacePacket placePacket = new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (user.isSword(placePacket.getItemStack())) {
                    blockX = placePacket.getPosition().getX();
                    blockY = placePacket.getPosition().getY();
                    blockZ = placePacket.getPosition().getZ();
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {


                if (blockZ != 0 && blockY != 0 && blockX != 0) {
                    if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(1)) {
                        if (blockX != -1 || blockY != -1 || blockZ != -1) {
                            flag(user, "Invalid blocking state");
                        }
                    }
                }

                blockZ = blockX = blockY = 0;
                break;
            }
        }
    }
}