package me.jtx.flopac.checks.misc.scaffold;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.jtx.flopac.util.TimeUtils;
import org.bukkit.Material;

@CheckInformation(checkName = "Scaffold", checkType = "I", punishmentVL = 15)
public class ScaffoldI extends Check {

    private double threshold, lastYaw, lastPitch;
    private long lastCompare;

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

                double pitch = user.getMovementProcessor().getPitchDelta();
                double yaw = user.getMovementProcessor().getYawDeltaClamped();

                double yawChange = Math.abs(yaw - lastYaw);
                double pitchChange = Math.abs(pitch - lastPitch);

                double compare = Double.compare(yawChange, pitchChange);

                if ((System.currentTimeMillis() - lastCompare) > 550L) {
                    threshold = 0;
                }

                if (user.getLastBlockPlaceTimer().hasNotPassed(2)) {

                    if (user.getPlayer().getEyeLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {

                        int faceInt = blockPlace.getFace().b();

                        if (faceInt >= 0 && faceInt <= 3) {
                            if (compare < 0.0) {
                                lastCompare = System.currentTimeMillis();
                                if (threshold++ > 15) {
                                    flag(user, "l=" + TimeUtils.elapsed(lastCompare), "t=" + threshold);
                                }
                            }
                        }
                    }
                }

                this.lastYaw = yaw;
                this.lastPitch = pitch;

                break;
            }
        }
    }
}