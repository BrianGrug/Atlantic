package com.doctordark.util.command;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents an argument used for a Bukkit command.
 */
public abstract class CommandArgument {

    private final String name;
    protected boolean isPlayerOnly = false;
    protected String description;
    protected String permission;
    protected String[] aliases;

    /**
     * Constructs a new {@link CommandArgument} with a given name,
     * description and set of aliases.
     *
     * @param name        the name to construct with
     * @param description the description to construct with
     */
    public CommandArgument(String name, String description) {
        this(name, description, (String) null);
    }

    /**
     * Constructs a new {@link CommandArgument} with a given name,
     * description and set of aliases.
     *
     * @param name        the name to construct with
     * @param description the description to construct with
     * @param permission  the required permission node
     */
    public CommandArgument(String name, String description, String permission) {
        this(name, description, permission, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Constructs a new {@link CommandArgument} with a given name,
     * description and set of aliases.
     *
     * @param name        the name to construct with
     * @param description the description to construct with
     * @param aliases     array of aliases to construct with
     */
    public CommandArgument(String name, String description, String[] aliases) {
        this(name, description, null, aliases);
    }

    /**
     * Constructs a new {@link CommandArgument} with a given name, permission,
     * description and set of aliases.
     *
     * @param name        the name to construct with
     * @param description the description to construct with
     * @param permission  the required permission node
     * @param aliases     array of aliases to construct with
     */
    public CommandArgument(String name, String description, String permission, String[] aliases) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }

    /**
     * Gets the name of this {@link CommandArgument}.
     *
     * @return the name
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Checks if this {@link CommandArgument} is only executable
     * for players.
     *
     * @return the result
     */
    public boolean isPlayerOnly() {
        return isPlayerOnly;
    }

    /**
     * Gets the description of this {@link CommandArgument}.
     *
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Gets the permission of this {@link CommandArgument}.
     *
     * @return the permission
     */
    public final String getPermission() {
        return this.permission;
    }

    /**
     * Gets the aliases used for this {@link CommandArgument}.
     *
     * @return array of aliases
     */
    public final String[] getAliases() {
        if (aliases == null) {
            aliases = ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return Arrays.copyOf(aliases, aliases.length);
    }

    /**
     * Gets the usage for this {@link CommandArgument}.
     *
     * @param label the label to check for
     * @return the usage message
     */
    public abstract String getUsage(String label);

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(CommandSender, Command, String, String[])
     */
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws IOException;

    /**
     * @see org.bukkit.command.TabCompleter#onTabComplete(CommandSender, Command, String, String[])
     */
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandArgument)) return false;

        CommandArgument that = (CommandArgument) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (permission != null ? !permission.equals(that.permission) : that.permission != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aliases, that.aliases)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (permission != null ? permission.hashCode() : 0);
        result = 31 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
        return result;
    }
}