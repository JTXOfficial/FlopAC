package me.jtx.flopac.checks.combat.autoclicker;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "D", lagBack = false, description = "Checks if the player is clicking consistently")
public class AutoClickerD extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
    private double threshold, lastStd;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || user.getLastBlockPlaceTimer().hasNotPassed(20)
                        || user.getMovementProcessor().getLastBlockDigTimer().hasNotPassed(20)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)) {
                    movements = 20;
                    return;
                }

                movements++;

                break;
            }

            case Packet.Client.ARM_ANIMATION: {
                if (movements < 10) {
                    delays.add(movements);

                    if (delays.size() == 75) {
                        double std = MathUtil.getStandardDeviation(delays);

                        if (Math.abs(std - lastStd) < 0.02) {
                            if (threshold++ > 2) {
                                flag(user, "Clicking to consistent [S-LS]");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.45);
                        }

                        lastStd = std;
                        delays.clear();
                    }
                }
                movements = 0;
                break;
            }
        }
    }
}
