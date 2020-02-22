package com.doctordark.util;

import org.bukkit.configuration.serialization.*;
import com.google.common.base.*;
import java.util.*;
import org.bukkit.*;

public class PersistableLocation implements ConfigurationSerializable, Cloneable {
    private Location location;
    private World world;
    private String worldName;
    private UUID worldUID;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PersistableLocation(final Location location) {
        Preconditions.checkNotNull((Object) location, (Object) "Location cannot be null");
        Preconditions.checkNotNull((Object) location.getWorld(), (Object) "Locations' world cannot be null");
        this.world = location.getWorld();
        this.worldName = this.world.getName();
        this.worldUID = this.world.getUID();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public PersistableLocation(final World world, final double x, final double y, final double z) {
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        final float n = 0.0f;
        this.yaw = n;
        this.pitch = n;
    }

    public PersistableLocation(final String worldName, final double x, final double y, final double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        final float n = 0.0f;
        this.yaw = n;
        this.pitch = n;
    }

    public PersistableLocation(final Map<String, String> map) {
        this.worldName = map.get("worldName");
        this.worldUID = UUID.fromString(map.get("worldUID"));
        Object o = map.get("x");
        if (o instanceof String) {
            this.x = Double.parseDouble((String) o);
        } else {
            this.x = (double) o;
        }
        o = map.get("y");
        if (o instanceof String) {
            this.y = Double.parseDouble((String) o);
        } else {
            this.y = (double) o;
        }
        o = map.get("z");
        if (o instanceof String) {
            this.z = Double.parseDouble((String) o);
        } else {
            this.z = (double) o;
        }
        this.yaw = Float.parseFloat(map.get("yaw"));
        this.pitch = Float.parseFloat(map.get("pitch"));
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("worldName", this.worldName);
        map.put("worldUID", this.worldUID.toString());
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        map.put("yaw", Float.toString(this.yaw));
        map.put("pitch", Float.toString(this.pitch));
        return map;
    }

    public World getWorld() {
        Preconditions.checkNotNull((Object) this.worldUID, (Object) "World UUID cannot be null");
        Preconditions.checkNotNull((Object) this.worldName, (Object) "World name cannot be null");
        if (this.world == null) {
            this.world = Bukkit.getWorld(this.worldUID);
        }
        return this.world;
    }

    public void setWorld(final World world) {
        this.worldName = world.getName();
        this.worldUID = world.getUID();
        this.world = world;
    }

    public Location getLocation() {
        if (this.location == null) {
            this.location = new Location(this.getWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
        }
        return this.location;
    }

    public PersistableLocation clone() throws CloneNotSupportedException {
        try {
            return (PersistableLocation) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        return "PersistableLocation [worldName=" + this.worldName + ", worldUID=" + this.worldUID + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", yaw=" + this.yaw + ", pitch=" + this.pitch
                + ']';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersistableLocation)) {
            return false;
        }
        final PersistableLocation that = (PersistableLocation) o;
        if (Double.compare(that.x, this.x) != 0) {
            return false;
        }
        if (Double.compare(that.y, this.y) != 0) {
            return false;
        }
        if (Double.compare(that.z, this.z) != 0) {
            return false;
        }
        if (Float.compare(that.yaw, this.yaw) != 0) {
            return false;
        }
        if (Float.compare(that.pitch, this.pitch) != 0) {
            return false;
        }
        Label_0134: {
            if (this.world != null) {
                if (this.world.equals(that.world)) {
                    break Label_0134;
                }
            } else if (that.world == null) {
                break Label_0134;
            }
            return false;
        }
        Label_0167: {
            if (this.worldName != null) {
                if (this.worldName.equals(that.worldName)) {
                    break Label_0167;
                }
            } else if (that.worldName == null) {
                break Label_0167;
            }
            return false;
        }
        if (this.worldUID != null) {
            if (!this.worldUID.equals(that.worldUID)) {
                return false;
            }
        } else if (that.worldUID != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (this.world != null) ? this.world.hashCode() : 0;
        result = 31 * result + ((this.worldName != null) ? this.worldName.hashCode() : 0);
        result = 31 * result + ((this.worldUID != null) ? this.worldUID.hashCode() : 0);
        long temp = Double.doubleToLongBits(this.x);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.y);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.z);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        result = 31 * result + ((this.yaw != 0.0f) ? Float.floatToIntBits(this.yaw) : 0);
        result = 31 * result + ((this.pitch != 0.0f) ? Float.floatToIntBits(this.pitch) : 0);
        return result;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public UUID getWorldUID() {
        return this.worldUID;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setWorldUID(final UUID worldUID) {
        this.worldUID = worldUID;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public void setZ(final double z) {
        this.z = z;
    }

    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }
}
