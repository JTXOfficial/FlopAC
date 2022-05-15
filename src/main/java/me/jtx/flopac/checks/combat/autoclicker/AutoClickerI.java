package me.jtx.flopac.checks.combat.autoclicker;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "I", punishmentVL = 25, description = "Drop time consistency")
public class AutoClickerI extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
    private List<Integer> lastDropList = new ArrayList<>();
    private double threshold, lastSTD;
    private long time;

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

                    double getCps = MathUtil.getCPS(delays);


                    if (delays.size() >= 20 && getCps > 8.5f) {

                        if (movements > 2 && movements < 6) {
                            threshold = 0;
                        } else {
                            threshold += 0.8;

                            if (threshold > 125) {
                                flag(user);
                            }
                        }

                    }

                    if (delays.size() >= 100) {
                        delays.clear();
                    }
                }
            }
            movements = 0;
            break;
        }
    }
}
