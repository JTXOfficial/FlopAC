package me.jtx.flopac.checks.movement.sneak;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Sneak", checkType = "B", canPunish = false, description = "Checks consistency of the players Sneaks")
public class SneakB extends Check {

    private List<Long> sneakList = new ArrayList<>();
    private double lastSTD;

    @Override
    public void onPacket(PacketEvent event) {

        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket actionPacket =
                        new WrappedInEntityActionPacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel()
                        || !user.isChunkLoaded()
                        || user.getTick() < 60) {
                    return;
                }

                if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING) {
                    sneakList.add(System.currentTimeMillis());

                    if (sneakList.size() == 25) {
                        double std = MathUtil.getStandardDeviation(sneakList);

                        double stdDiff = Math.abs(std - lastSTD);

                        if (stdDiff < .7) {
                            flag(user, "Invalid Sneaking");
                        }

                        lastSTD = std;
                        sneakList.clear();
                    }
                }

                break;
            }
        }
    }
}
