package me.jtx.flopac.checks.combat.aimassist;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AimAssist", checkType = "G", lagBack = false, punishmentVL = 25)
public class AimAssistG extends Check {

    private double threshold;
    private List<Double> deltaPitchList = new ArrayList<>();

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(2)) {
                    double deltaPitch = Math.abs(user.getCurrentLocation().getPitch()
                            - user.getLastLocation().getPitch());

                    //Bukkit.broadcastMessage(""+deltaPitch);

                    if (deltaPitch > 0.8) {
                        deltaPitchList.add(deltaPitch);

                        if (deltaPitchList.size() > 125) {
                            double std = MathUtil.getStandardDeviation(deltaPitchList);

                            if (std < 0.9) {
                                flag(user, "Pitch consistency");
                            }

                            deltaPitchList.clear();
                        }
                    }

                }

                break;
            }

        }
    }
}
