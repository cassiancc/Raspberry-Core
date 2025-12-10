package cc.cassian.raspberry.common.api.leash;

import net.minecraft.world.phys.Vec3;

public class LeashState {
    public Vec3 offset = Vec3.ZERO;
    public Vec3 start = Vec3.ZERO;
    public Vec3 end = Vec3.ZERO;
    public int startBlockLight = 0;
    public int endBlockLight = 0;
    public int startSkyLight = 15;
    public int endSkyLight = 15;
    public boolean slack = true;
}