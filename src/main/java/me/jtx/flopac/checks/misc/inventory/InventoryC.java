package me.jtx.flopac.checks.misc.inventory;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInWindowClickPacket;
import me.jtx.flopac.util.MathUtil;
import me.jtx.flopac.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Inventory", checkType = "C", lagBack = false, punishmentVL = 9)
public class InventoryC extends Check {

    private int shiftClickTicks;
    private Long lastClickWindow;
    private List<Long> delays = new ArrayList<>();
    private double lastStd, threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.WINDOW_CLICK: {
                User user = event.getUser();

                WrappedInWindowClickPacket clickPacket = new WrappedInWindowClickPacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel() || user.getTick() < 60 || !user.isChunkLoaded()) {
                    return;
                }

                if (clickPacket.getAction().isShiftClick()) {
                    shiftClickTicks++;
                }

                if (clickPacket.getAction() == WrappedInWindowClickPacket.ClickType.DRAG || shiftClickTicks > 1) {
                    return;
                }

                if (clickPacket.getAction().isLeftClick() || clickPacket.getAction().isShiftClick()) {
                    long time = System.currentTimeMillis();

                    if (lastClickWindow != null) {
                        long change = TimeUtils.elapsed((time - lastClickWindow));

                        delays.add(change);

                        if (delays.size() == 5) {
                            double std = MathUtil.getStandardDeviation(delays);

                            double changeStd = Math.abs(std - lastStd);


                            if (changeStd < 0.7) {
                                if (++threshold > 2) {
                                    flag(user, "Possibily using ChestStealer or Refill");
                                }
                            } else {
                                threshold -= Math.min(threshold, 0.5);
                            }

                            delays.clear();

                            lastStd = std;
                        }
                    }

                    lastClickWindow = time;
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                shiftClickTicks = 0;
                break;
            }
        }
    }
}
