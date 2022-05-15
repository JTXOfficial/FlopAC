package me.jtx.flopac.checks.movement.strafe;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@CheckInformation(checkName = "Strafe", canPunish = false, description = "Detects if player is strafing in the air")
public class Strafe extends Check {

    private static final Set<Integer> DIRECTIONS = new HashSet<>(Arrays.asList(45, 90, 135, 180));
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
                        || user.getLastTeleportTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())
                        || !user.isChunkLoaded()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())) {
                    return;
                }

                if (user.getLastLocation() != null && user.getLastLastLocation() != null) {
                    float moveAngle = MathUtil.getMoveAngle(user.getLastLastLocation(), user.getLastLocation());

                    double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                    double yaw = user.getMovementProcessor().getYawDeltaClamped();

                    if (yaw > 0.001 && yaw <= 360) {
                        if (deltaXZ > 0.01 && !user.getMovementProcessor().isOnGround()) {
                            DIRECTIONS.forEach(direction -> {

                                double change = Math.abs(direction - moveAngle);

                                if (change < 0.0001F) {
                                    if (threshold++ > 4) {
                                        flag(user, "Strafing in the air");
                                    }
                                } else {
                                    threshold -= Math.min(threshold, 0.02f);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}