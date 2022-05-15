package me.jtx.flopac.checks.misc.pingspoof;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "PingSpoof", checkType = "B", lagBack = false, description = "Detects Ping Spoofing")
public class PingSpoofB extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel() || user.getTick() < 60) {
                    threshold = 0;
                    return;
                }


                break;
            }
        }
    }
}