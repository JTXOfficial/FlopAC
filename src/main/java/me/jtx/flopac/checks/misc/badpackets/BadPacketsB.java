package me.jtx.flopac.checks.misc.badpackets;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInFlyingPacket;

@CheckInformation(checkName = "BadPackets", checkType = "B", lagBack = false, punishmentVL = 1)
public class BadPacketsB extends Check {

    private int streaks = 0;

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
                        || !user.isChunkLoaded()) {
                    streaks = 0;
                    return;
                }

                WrappedInFlyingPacket flyingPacket = new WrappedInFlyingPacket(event.getPacket(), user.getPlayer());

                if (flyingPacket.isPos() || user.getPlayer().isInsideVehicle()) {
                    streaks = 0;
                } else if (streaks++ > 20) {
                    flag(user, "Invalid Game Tick");
                }
                break;
            }
        }
    }
}