package me.scifi.hcf.elevators;

public enum Direction {

    UP("UP"), DOWN("DOWN");

    public final String direction;

    Direction(String direction){
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }
}
