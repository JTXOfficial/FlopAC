package me.jtx.flopac.checks.combat.aimassist;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AimAssist", checkType = "L", lagBack = false, punishmentVL = 10)
public class AimAssistL extends Check {

    private double threshold, lastSTD;
    private List<Double> deltaYawList = new ArrayList<>();

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 60 || user.shouldCancel()) {
                    return;
                }

                double yaw = user.getMovementProcessor().getYawDeltaClamped();

                if (yaw > 1.0) {
                    deltaYawList.add(yaw);

                    if (deltaYawList.size() >= 100) {
                        double std = MathUtil.getStandardDeviation(deltaYawList);

                        if (Math.abs(std - lastSTD) < 0.01) {
                            if (++threshold > 6) {
                                flag(user, "Invalid yaw movements");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.25);
                        }


                        lastSTD = std;
                        deltaYawList.clear();
                    }
                }

                break;
            }
        }
    }
}
