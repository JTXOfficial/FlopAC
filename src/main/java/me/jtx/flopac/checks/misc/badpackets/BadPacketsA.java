package me.jtx.flopac.checks.misc.badpackets;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "BadPackets", lagBack = false, punishmentVL = 1)
public class BadPacketsA extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || !user.isChunkLoaded()) {
                    return;
                }

                double pitch = Math.abs(user.getCurrentLocation().getPitch());

                double maxPitch = user.getBlockData().climbable ? 91.11F : 90.0F;

                if (pitch > maxPitch) {
                    flag(user,
                            "pitch: " + pitch
                    );
                }

                break;
            }
        }
    }
}
