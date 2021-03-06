package me.jtx.flopac.checks.misc.scaffold;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.jtx.flopac.util.MathUtil;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Scaffold", checkType = "B", lagBack = false, punishmentVL = 10)
public class ScaffoldB extends Check {

    private List<Float> placeList = new ArrayList<>();
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.BLOCK_PLACE: {
                User user = event.getUser();

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());


                if (user.shouldCancel() || user.getTick() < 60) {
                    threshold = 0;
                    placeList.clear();
                    return;
                }


                float vecY = blockPlace.getVecY();

                int faceInt = blockPlace.getFace().b();

                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (yaw > 0) {
                    if (user.getBlockPlaced().getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                        if (faceInt >= 0 && faceInt <= 3) {
                            placeList.add(vecY);

                            if (placeList.size() == 10) {
                                double std = MathUtil.getStandardDeviation(placeList);

                                if (std < 0.06) {
                                    if (++threshold > 2) {
                                        flag(user, "HitVec Consistency");
                                    }
                                } else {
                                    threshold -= Math.min(threshold, 0.25);
                                }

                                placeList.clear();
                            }
                        }
                    }
                }
                break;
            }
        }
    }
}