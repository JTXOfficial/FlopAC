package me.jtx.flopac.checks.combat.autoclicker;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.GraphUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "F", canPunish = false, enabled = false, description = "Graph Clicker Check")
public class AutoClickerF extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
    private List<Double> outlierList = new ArrayList<>();
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

                    if (delays.size() == 50) {

                        int outliers = (int) delays.stream()
                                .filter(delay -> delay > 3)
                                .count();

                        outlierList.add((double) outliers);

                        if (outlierList.size() > 2) {
                            GraphUtil.GraphResult graphResult = GraphUtil.getGraph(outlierList);

                            int positives = graphResult.getPositives(), negatives = graphResult.getNegatives();

                            if (positives == 0 && negatives > 2) {
                                if (++threshold > 5) {
                                    flag(user, "Abnormal Click Drops");
                                }
                            } else {
                                threshold -= Math.min(threshold, 1.5);
                            }

                            outlierList.clear();
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
