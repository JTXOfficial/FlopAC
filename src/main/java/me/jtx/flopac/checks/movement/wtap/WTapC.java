package me.jtx.flopac.checks.movement.wtap;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import me.jtx.flopac.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "WTap", checkType = "B", description = "Post unsprint")
public class WTapC extends Check {

    private double threshold;
    private boolean releasePacket;
    private int flying;
    private List<Integer> wtapDelays = new ArrayList<>();

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                ++this.flying;
                this.releasePacket = false;

                break;
            }

            case Packet.Client.BLOCK_DIG: {

                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    this.releasePacket = true;
                }
                break;
            }

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket actionPacket =
                        new WrappedInEntityActionPacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || user.getLastTeleportTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())
                        || !user.isChunkLoaded()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())) {
                    return;
                }


                if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING) {
                    if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(20) && !releasePacket) {
                        this.wtapDelays.add(this.flying);

                        if (this.wtapDelays.size() == 20) {

                            double std = MathUtil.getStandardDeviation(this.wtapDelays);

                            wtapDelays.clear();
                        }
                    }
                } else if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING) {
                    this.flying = 0;
                }

                break;
            }
        }
    }
}