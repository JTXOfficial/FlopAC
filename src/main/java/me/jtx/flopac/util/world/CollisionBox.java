package me.jtx.flopac.util.world;

import me.jtx.flopac.util.world.types.SimpleCollisionBox;
import me.jtx.flopac.tinyprotocol.packet.types.WrappedEnumParticle;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface CollisionBox {
    boolean isCollided(CollisionBox other);
    void draw(WrappedEnumParticle particle, Collection<? extends Player> players);
    CollisionBox copy();
    CollisionBox offset(double x, double y, double z);
    void downCast(List<SimpleCollisionBox> list);
    boolean isNull();
}