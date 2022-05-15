package me.jtx.flopac.checks.movement.flight;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "B", canPunish = false, description = "Checks if the player is spoofing ground while 1/64")
public class FlightB extends Check {

    private int threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || !user.isChunkLoaded()
                        || user.getLastBlockPlaceTimer().hasNotPassed(20)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)
                        || user.getTick() < 60) {
                    return;
                }

                if (user.getGhostBlockProcessor().getGhostBlockTeleportTimer().hasNotPassed(1)) {
                    if (++threshold > 6) {
                        flag(user, "Possibly using Fly/Nofall");
                    }
                } else {
                    threshold = 0;
                }
            }
        }
    }
}