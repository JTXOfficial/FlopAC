package me.jtx.flopac.util.world.types;


import me.jtx.flopac.tinyprotocol.api.ProtocolVersion;
import me.jtx.flopac.util.world.CollisionBox;
import org.bukkit.block.Block;

public interface CollisionFactory {
    CollisionBox fetch(ProtocolVersion version, Block block);
}