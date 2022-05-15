package me.jtx.flopac.checks.combat.killaura;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Killaura", checkType = "K", lagBack = false, description = "Autoblock Delay Check", punishmentVL = 3)
public class KillauraK extends Check {

    private List<Integer> delays = new ArrayList<>();
    private int movements;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                movements++;
                break;
            }

            case Packet.Client.BLOCK_DIG: {

                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    if (movements < 10) {
                        delays.add(movements);

                        if (delays.size() == 25) {
                            double std = MathUtil.getStandardDeviation(delays);

                            if (std < 0.34) {
                                if (threshold++ > 1) {
                                    flag(user, "Blocking to consistent");
                                }
                            } else {
                                threshold -= Math.min(threshold, .5);
                            }

                            delays.clear();
                        }

                    }
                    movements = 0;
                }
                break;
            }
        }
    }
}