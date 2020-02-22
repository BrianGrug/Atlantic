package me.scifi.hcf.ktk;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class KingManager {

    public Player kingPlayer;

    public boolean isEventActive() {
        return kingPlayer != null;
    }

    public void removeKing(boolean isCancelled) {
        if (isCancelled) {
            kingPlayer = null;
        }

    }

}
