package com.doctordark.util.cuboid;

import org.bukkit.block.*;
import org.bukkit.*;

public class CoordinatePair {
    private final String worldName;
    private final int x;
    private final int z;

    public CoordinatePair(final Block block) {
        this(block.getWorld(), block.getX(), block.getZ());
    }

    public CoordinatePair(final World world, final int x, final int z) {
        this.worldName = world.getName();
        this.x = x;
        this.z = z;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoordinatePair)) {
            return false;
        }
        final CoordinatePair that = (CoordinatePair) o;
        if (this.x != that.x) {
            return false;
        }
        if (this.z != that.z) {
            return false;
        }
        if (this.worldName != null) {
            if (!this.worldName.equals(that.worldName)) {
                return false;
            }
        } else if (that.worldName != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (this.worldName != null) ? this.worldName.hashCode() : 0;
        result = 31 * result + this.x;
        result = 31 * result + this.z;
        return result;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }
}
