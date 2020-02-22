package com.doctordark.util.cuboid;

import org.bukkit.*;

import java.util.*;

public class NamedCuboid extends Cuboid {
    protected String name;

    public NamedCuboid(final Cuboid other) {
        super(other.getWorld(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
    }

    public NamedCuboid(final World world, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        super(world, x1, y1, z1, x2, y2, z2);
    }

    public NamedCuboid(final Location location) {
        super(location, location);
    }

    public NamedCuboid(final Location first, final Location second) {
        super(first, second);
    }

    public NamedCuboid(final Map<String, Object> map) {
        super(map);
        this.name = (String) map.get("name");
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        map.put("name", this.name);
        return map;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public NamedCuboid clone() {
        return (NamedCuboid) super.clone();
    }

    @Override
    public String toString() {
        return "NamedCuboid: " + this.worldName + ',' + this.x1 + ',' + this.y1 + ',' + this.z1 + "=>" + this.x2 + ',' + this.y2 + ',' + this.z2 + ':' + this.name;
    }
}
