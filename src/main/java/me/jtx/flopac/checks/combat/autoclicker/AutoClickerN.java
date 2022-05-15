package me.jtx.flopac.checks.combat.autoclicker;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "N", punishmentVL = 3, description = "Clicker Flaw")
public class AutoClickerN extends Check {

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
                        delays.add(skewness);

                        if (delays.size() == 25) {

                            double average = MathUtil.getAverage(delays);

                            if (average < -2) {
                                if (++threshold > 3) {
                                    flag(user);
                                }
                            } else {
                                threshold -= Math.min(threshold, 0.25);
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
