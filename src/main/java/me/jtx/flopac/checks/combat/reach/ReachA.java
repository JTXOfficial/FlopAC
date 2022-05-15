package me.jtx.flopac.checks.combat.reach;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jtx.flopac.util.MathUtil;
import me.jtx.flopac.util.PastLocation;
import me.jtx.flopac.util.PlayerLocation;
import me.jtx.flopac.util.world.CollisionBox;
import me.jtx.flopac.util.world.EntityData;
import me.jtx.flopac.util.world.types.RayCollision;
import me.jtx.flopac.util.world.types.SimpleCollisionBox;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CheckInformation(checkName = "Reach", lagBack = false, punishmentVL = 15, description = "Basic PastLocation 3.2 Reach Check.")
public class ReachA extends Check {

    private PastLocation reachATargetLocations = new PastLocation();

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.shouldCancel()
                            || user.getConnectionProcessor().isLagging()
                            || user.getTick() < 60
                            || user.getCombatProcessor().getCancelTicks() > 0) {
                        threshold = 0;
                        return;
                    }

                    List<SimpleCollisionBox> simpleBoxes = new ArrayList<>();

                    reachATargetLocations.getEstimatedLocation(event.getTimestamp(),
                            user.getConnectionProcessor().getTransPing(), 200L)
                            .stream()
                            .map(loc -> getHitbox(user.getCombatProcessor().getLastAttackedEntity(), loc)).collect(Collectors.toList())
                            .forEach(box -> box.downCast(simpleBoxes));

                    double distance = 69, horzDistance = 69;
                    int a = 0, collided = 0;

                    for (PlayerLocation location : Arrays.asList(user.getCurrentLocation().clone(),
                            user.getLastLocation().clone())) {
                        location.setY(location.getY() + 1.64f);


                        RayCollision ray = new RayCollision(location.toVector(), MathUtil.getDirection(location));


                        for (SimpleCollisionBox collisionBox : simpleBoxes) {

                            SimpleCollisionBox simpleCollisionBox = collisionBox.copy();
                            simpleCollisionBox.expand(0.1);
                            double hitBoxExpand = RayCollision.distance(ray, simpleCollisionBox);

                            horzDistance = Math.min(horzDistance, simpleCollisionBox.max()
                                    .midpoint(simpleCollisionBox.min())
                                    .setY(0).distance(location.toVector().setY(0)) - .4);

                            if (hitBoxExpand == -1) {
                                a++;
                                continue;
                            } else {
                                collided++;
                            }

                            distance = Math.min(distance, hitBoxExpand);

                        }
                    }

                    if (distance >= 3.2 && horzDistance >= 3.2 && collided >= 2) {
                        flag(user, "r="+distance + " hr="+horzDistance + " c="+collided);
                    }

                    reachATargetLocations.addLocation(user.getReachProcessor().getReachData().getCustomLocation());
                }

                break;
            }
        }
    }


    private static CollisionBox getHitbox(Entity entity, PlayerLocation loc) {
        return EntityData.getEntityBox(loc, entity);
    }
}
