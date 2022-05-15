package me.jtx.flopac.util.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jtx.flopac.util.BlockUtil;
import me.jtx.flopac.util.box.BoundingBox;
import org.bukkit.block.Block;

@AllArgsConstructor @Getter
public class CollideEntry {
    private final Block block;
    private final BoundingBox boundingBox;

    public boolean isChunkLoaded() {
        return BlockUtil.isChunkLoaded(block.getLocation());
    }
}
