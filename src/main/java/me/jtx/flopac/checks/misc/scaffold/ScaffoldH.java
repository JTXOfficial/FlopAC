package me.jtx.flopac.checks.misc.scaffold;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import org.bukkit.Material;

@CheckInformation(checkName = "Scaffold", checkType = "H", lagBack = false, description = "Sprinting while placing blocks", punishmentVL = 12)
public class ScaffoldH extends Check {

    private double threshold;

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

                double pitch = user.getCurrentLocation().getPitch();

                if (user.getPlayer().getEyeLocation().add(0, -3, 0).getBlock().getType() == Material.AIR) {
                    if (pitch > 80) {

                        int faceInt = blockPlace.getFace().b();

                        if (faceInt >= 0 && faceInt <= 3) {
                            if (user.getMovementProcessor().isSprinting()) {
                                if (threshold++ > 12) {
                                    flag(user, "Sprinting while scaffolding");
                                }
                            } else {
                                threshold -= Math.min(threshold, 0.255);
                            }
                        } else {
                            threshold = 0;
                        }
                    }
                }

                break;
            }
        }
    }
}