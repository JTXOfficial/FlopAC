package me.jtx.flopac.checks.misc.timer;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Timer", checkType = "B", lagBack = false, punishmentVL = 4, description = "Detects Balance Timer Abuse")
public class TimerB extends Check {

    private long lastPacket = -1337L;

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                long now = System.currentTimeMillis();
                long delta = now - this.lastPacket;

                if (!user.shouldCancel() && user.getTick() > 120 && this.lastPacket > -1337L && user.isChunkLoaded()) {
                    if (delta > 100L) {
                        if (++threshold > 6) {
                            flag(user);
                            threshold = 0;
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.7f);
                    }
                }

                this.lastPacket = now;
                break;
            }
        }
    }
}