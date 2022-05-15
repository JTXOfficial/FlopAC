package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Killaura", checkType = "B", canPunish = false, punishmentVL = 250, description = "Post Swing Check")
public class KillauraB extends Check {

    private long lastFlyingPacket;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                lastFlyingPacket = System.currentTimeMillis();

                break;
            }

            case Packet.Client.ARM_ANIMATION: {

                if (user.shouldCancel()
                        || user.getConnectionProcessor().getClientTick() > 5
                        || user.getConnectionProcessor().getFlyingTick() > 1
                        || user.getConnectionProcessor().getDropTransTime() > 50L
                        || user.getTick() < 60) {

                    threshold = 0;
                    return;
                }

                if (user.getConnectionProcessor().getFlyingTick() > 3) {
                    threshold = 0;
                }

                if ((System.currentTimeMillis() - lastFlyingPacket) <= 5L) {
                    if (threshold++ > 10) {
                        flag(user, "Sent swing packet late");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);

                }

                break;
            }
        }
    }
}