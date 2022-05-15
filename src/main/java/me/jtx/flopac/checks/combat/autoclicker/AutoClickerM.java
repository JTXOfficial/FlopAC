package me.jtx.flopac.checks.combat.autoclicker;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "M", punishmentVL = 3, description = "Clicker Flaw")
public class AutoClickerM extends Check {

    private int movements;
    private List<Double> delays = new ArrayList<>();
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.ARM_ANIMATION: {

                if (user.shouldCancel() || user.getTick() < 60) {
                    return;
                }

                double skewness = user.getCombatProcessor().getSkewness();
                double outlier = user.getCombatProcessor().getOutlier();
                double currentCps = user.getCombatProcessor().getCurrentCps();
                double kurtosis = user.getCombatProcessor().getKurtosis();
                double median = user.getCombatProcessor().getMedian();

                if (median < 2.5 && user.getCombatProcessor().getMovements().size() >= 20) {

                    if (currentCps > 8) {
                        delays.add(kurtosis);

                        if (delays.size() == 25) {


                            double std = MathUtil.getStandardDeviation(delays);

                            if (std < 0.1) {
                                if (++threshold > 2) {
                                    flag(user);
                                }
                            } else {
                                threshold -= Math.min(threshold, 0.15);
                            }

                            delays.clear();
                        }
                    }
                }

                break;
            }
        }
    }
}
