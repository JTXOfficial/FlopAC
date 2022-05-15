package me.jtx.flopac.checks.misc.badpackets;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.jtx.flopac.util.RunUtils;

@CheckInformation(checkName = "BadPackets", checkType = "K", description = "NCP NoSlow check")
public class BadPacketsK extends Check {

    private double threshold;
    private int ticks, digTick, placeTick;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();
        switch (event.getType()) {
            case Packet.Client.BLOCK_DIG: {

                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    this.digTick = this.ticks;
                }

                break;
            }

            case Packet.Client.BLOCK_PLACE: {
                if (user.getPredictionProcessor().isUseSword() || user.getPredictionProcessor().isUseItem()) {
                    this.placeTick = this.ticks;
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                this.ticks++;
                if (user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(20
                        + user.getConnectionProcessor().getClientTick())) {
                    threshold = 0;
                    return;
                }

                int placeDelta = ticks - placeTick;
                int digDelta = ticks - digTick;

                if (placeDelta == digDelta && placeDelta < 5 && digDelta < 5) {

                    if (++threshold > 6) {
                        flag(user, "Spamming place/dig packets");
                        threshold = 6;
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);
                }

                break;
            }
        }
    }

    void swapItem(User user) {
        user.getPredictionProcessor().setUseItem(false);
        user.getPredictionProcessor().setUseSword(false);

        // main thread to be safe
        RunUtils.task(() -> {
            int currentSlot = user.getPlayer().getInventory().getHeldItemSlot();
            int toSwitch = (currentSlot < 8 ? currentSlot + 1 : 0);
            user.getPlayer().getInventory().setHeldItemSlot(toSwitch);
        });
    }
}