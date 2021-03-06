package me.jtx.flopac.base.user.objects;

import me.jtx.flopac.base.user.User;
import me.jtx.flopac.util.EventTimer;

public class BlockData {
    public boolean sign, collideSlime, door, lillyPad, skull, cake, carpet, collidesHorizontal, onGround = false, lastOnGround = false, nearWater, nearLava, nearIce, climbable, slime, piston, snow, fence,
            bed, stair, slab, underBlock, web, shulker, insideBlock;
    public int signTicks, lillyPadTicks, lavaTicks, waterTicks, pistonTicks, skullTicks, cakeTicks, carpetTicks, climbableTicks, iceTicks, slimeTicks, snowTicks, fenceTicks, bedTicks,
            stairTicks, slabTicks, underBlockTicks, webTicks, shulkerTicks;
    public double lastBlockY;
    public EventTimer collideSlimeTimer, movingUpTimer, climbableTimer, iceTimer, slimeTimer, stairSlabTimer, blockAboveTimer;

    public void setupTimers(User user) {
        this.movingUpTimer = new EventTimer(20, user);
        this.climbableTimer = new EventTimer(60, user);
        this.iceTimer = new EventTimer(100, user);
        this.slimeTimer = new EventTimer(60, user);
        this.stairSlabTimer = new EventTimer(100, user);
        this.blockAboveTimer = new EventTimer(60, user);
        this.collideSlimeTimer = new EventTimer(20, user);
    }
}
