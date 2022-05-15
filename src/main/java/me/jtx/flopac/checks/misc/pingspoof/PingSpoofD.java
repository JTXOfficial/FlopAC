package me.jtx.flopac.checks.misc.pingspoof;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "PingSpoof", checkType = "D", lagBack = false, description = "Blocks Low Timer")
public class PingSpoofD extends Check {

    private long lastFlying;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getTick() < 1000
                        || user.getLastTeleportTimer().hasNotPassed(5)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    return;
                }

                long now = System.currentTimeMillis();

                long delta = (now - this.lastFlying);

                if (delta > 100L && user.getConnectionProcessor().getDropTransTime() < 20) {

                }

                this.lastFlying = now;
                break;
            }
        }
    }
}