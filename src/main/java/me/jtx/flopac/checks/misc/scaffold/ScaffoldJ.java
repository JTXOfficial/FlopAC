package me.jtx.flopac.checks.misc.scaffold;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.jtx.flopac.util.MathUtil;
import org.bukkit.Material;

@CheckInformation(checkName = "Scaffold", checkType = "J", punishmentVL = 15)
public class ScaffoldJ extends Check {

    private double threshold, lastYaw;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.BLOCK_PLACE: {

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel() || user.getTick() < 60) {
                    threshold = 0;
                    return;
                }

             //   double pitch = user.getMovementProcessor().getPitchDelta();

                double calculate = MathUtil.yawCheck(user.getCurrentLocation().getYaw(), lastYaw);

                double yawCompare = Double.compare(user.getCurrentLocation().getYaw(), 0.0);
                double yawDiffCompare = Double.compare(user.getCurrentLocation().getYaw(), lastYaw);

                if (user.getMovementProcessor().getDeltaY() != 0) {
                    threshold -= Math.min(threshold, 0.25);
                }
                if (user.getLastBlockPlaceTimer().hasNotPassed(2)) {
                    if (user.getPlayer().getEyeLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {

                        int faceInt = blockPlace.getFace().b();

                        if (faceInt >= 0 && faceInt <= 3) {
                            if (calculate <= 94) {
                                if (yawCompare == -1 || yawDiffCompare != 0) {
                                    if (++threshold > 15) {
                                        flag(user);
                                    }
                                } else {
                                    threshold -= Math.min(threshold, 0.85f);
                                }
                            }
                        }
                    }
                }

                this.lastYaw = user.getCurrentLocation().getYaw();


                break;
            }
        }
    }
}