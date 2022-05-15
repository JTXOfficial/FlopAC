package me.jtx.flopac.checks.misc.pingspoof;

import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

@CheckInformation(checkName = "PingSpoof", lagBack = false, canPunish = false, description = "Blocks Ping Spoofing")
public class PingSpoofA extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(5)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    return;
                }

                boolean canKick = user.getTick() > 1000;

                if (user.getConnectionMap().size() > (canKick ? 19 : 45)
                        && user.getConnectionProcessor().getDropTransTime() > TimeUnit.SECONDS.toMillis(7L)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            devFlag(user, "Got disconnected due to abnormal lag");
                            user.getPlayer().kickPlayer("Disconnected.");
                        }
                    }.runTask(FlopAC.getInstance());
                }

                break;
            }
        }
    }
}