package me.scifi.hcf.faction.struct;

import org.bukkit.ChatColor;
import me.scifi.hcf.faction.type.Faction;

/**
 * Represents the {@link RegenStatus} of a {@link Faction}.
 */
public enum RegenStatus {

    FULL(ChatColor.GREEN.toString() + '\u25B6'), REGENERATING(ChatColor.GOLD.toString() + '\u21ea'), PAUSED(ChatColor.RED.toString() + '\u25a0');

    private final String symbol;

    RegenStatus(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the symbol of this {@link RegenStatus}.
     *
     * @return the regeneration status
     */
    public String getSymbol() {
        return symbol;
    }
}
