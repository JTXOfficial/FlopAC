package me.jtx.flopac.checks.misc.badpackets;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInCustomPayloadPacket;

@CheckInformation(checkName = "BadPackets", checkType = "G", lagBack = false, punishmentVL = 10, canPunish = false)
public class BadPacketsG extends Check {

    private int threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.CUSTOM_PAYLOAD: {
                User user = event.getUser();

                WrappedInCustomPayloadPacket packet
                        = new WrappedInCustomPayloadPacket(event.getPacket());

                String tag = packet.getTag();

                if (tag.equals("MC|Brand") || tag.equals("REGISTER")) {
                    threshold++;
                    if (threshold > 7) {
                        flag(user, "Invalid CustomPayloads");
                    }
                }

                break;
            }
        }
    }
}