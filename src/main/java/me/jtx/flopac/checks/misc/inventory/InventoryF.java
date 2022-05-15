package me.jtx.flopac.checks.misc.inventory;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Inventory", checkType = "F", lagBack = false, punishmentVL = 10)
public class InventoryF extends Check {

    private boolean clickWindow;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.WINDOW_CLICK: {
                clickWindow = true;
                break;
            }

            case Packet.Client.USE_ENTITY: {
                User user = event.getUser();

                WrappedInUseEntityPacket useEntityPacket =
                        new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel()
                        || user.getCombatProcessor().getVelocityTicks() <= 9
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getBlockData().iceTimer.hasNotPassed(20)
                        || user.getBlockData().slimeTimer.hasNotPassed(20)
                        || user.getTick() < 60
                        || !user.isChunkLoaded()
                        || user.getBlockData().pistonTicks > 0) {
                    return;
                }

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK
                        && clickWindow) {
                    flag(user, "Clicking in inventory while attacking");
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                clickWindow = false;
                break;
            }
        }
    }
}
