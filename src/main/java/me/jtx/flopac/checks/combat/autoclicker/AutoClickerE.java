package me.jtx.flopac.checks.combat.autoclicker;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "E", lagBack = false, description = "Vape Autoclicker Detection")
public class AutoClickerE extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
    private double threshold;

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

                    if (delays.size() == 1000) {

                        int outliers = (int) delays.stream()
                                .filter(delay -> delay > 3)
                                .count();

                        if (outliers < 7) {
                            if (++threshold > 1) {
                                flag(user, "Outliers");
                            }
                        } else {
                            threshold -= Math.min(threshold, 1.5);
                        }

                        delays.clear();
                    }

                }
                movements = 0;
                break;
            }
        }
    }
}
