package me.jtx.flopac.checks.combat.hitbox;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jtx.flopac.util.MathUtil;
import me.jtx.flopac.util.PastLocation;
import me.jtx.flopac.util.PlayerLocation;
import me.jtx.flopac.util.block.RayTrace;
import me.jtx.flopac.util.box.BoundingBox;
import me.jtx.flopac.util.world.CollisionBox;
import me.jtx.flopac.util.world.EntityData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Hitbox", checkType = "B", canPunish = false, punishmentVL = 15)
public class HitboxB extends Check {

    private double threshold;
    private PastLocation hitboxLocations = new PastLocation();
    private List<BoundingBox> boundingBoxList = new ArrayList<>();
    private boolean outsideHitbox;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.shouldCancel()
                            || user.getTick() < 60
                            || user.getConnectionProcessor().getClientTick() > 18
                            || user.getCombatProcessor().getCancelTicks() > 0) {
                        threshold = 0;
                        return;
                    }

                    boolean canFlag = user.getMovementProcessor().getYawDeltaClamped() > 0.1
                            && user.getMovementProcessor().getYawDeltaClamped() < 20
                            && user.getMovementProcessor().getDeltaXZ() > 0.1;

                    if (outsideHitbox && canFlag) {
                        if (++threshold > 7) {
                            flag(user, "Attacking outside hitbox");
                        }
                    } else {
                        threshold -= Math.min(threshold, 2.5);
                    }

                }
                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getCombatProcessor().getLastAttackedEntity() != null) {
                    hitboxLocations.addLocation(user.getCombatProcessor()
                            .getLastAttackedEntity().getLocation());
                }

                Location location = user.getCurrentLocation().clone()
                        .toBukkitLocation(user.getPlayer().getWorld());

                LivingEntity livingEntity = (LivingEntity) user.getCombatProcessor().getLastAttackedEntity();

                List<PlayerLocation> pastLocation = hitboxLocations.getEstimatedLocation(event.getTimestamp(),
                        user.getConnectionProcessor().getTransPing(),
                        user.getConnectionProcessor().getDropTransTime() + 150L);

                if (user.getCombatProcessor().getCancelTicks() > 0) {
                    threshold = 0;
                    return;
                }

                if (pastLocation.size() > 0) {

                    if (livingEntity != null && location != null) {

                        if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(5)) {
                            pastLocation.forEach(loc1 ->
                                    boundingBoxList.add(MathUtil.getHitboxV2(livingEntity, loc1, user)));

                            location.setY(location.getY() + (user.getPlayer().isSneaking() ? 1.53 - 0.4f
                                    : user.getPlayer().getEyeHeight() - .4f));

                            RayTrace trace = new RayTrace(location.toVector(),
                                    user.getPlayer().getEyeLocation().getDirection());

                            boolean outsideHitbox = boundingBoxList.stream().noneMatch(box ->
                                    trace.intersects(box, box.getMinimum().distance(location.toVector())
                                            + 1.0, .4f));

                            this.outsideHitbox = outsideHitbox;

                            boundingBoxList.clear();
                            pastLocation.clear();
                        }
                    }
                }

                break;
            }
        }
    }

    private static CollisionBox getHitbox(Entity entity, PlayerLocation loc) {
        return EntityData.getEntityBox(loc, entity);
    }
}
