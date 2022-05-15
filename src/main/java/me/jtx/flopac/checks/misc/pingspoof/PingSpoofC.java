package me.jtx.flopac.checks.misc.pingspoof;

import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInformation(checkName = "PingSpoof", checkType = "C", lagBack = false, description = "Detects Ping Spoofing")
public class PingSpoofC extends Check {

    private double threshold;

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
                         || user.getPlayer().isDead()) {
                    threshold = 0;
                    return;
                }

                int keepSize = user.getConnectionMap2().size();
                int transSize = user.getConnectionMap().size();

                if (keepSize > transSize + 5) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            devFlag(user, "Got disconnected due to game slowing down.");
                            user.getPlayer().kickPlayer("Disconnected.");
                        }
                    }.runTask(FlopAC.getInstance());
                }

                break;
            }
        }
    }
}