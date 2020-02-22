package com.doctordark.util.chat;
//Same as Doctor's

import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import java.util.*;

public class Text extends ChatComponentText
{
    public Text() {
        super("");
    }
    
    public Text(final String string) {
        super(string);
    }
    
    public Text(final Object object) {
        super(String.valueOf(object));
    }
    
    public static Trans fromItemStack(final ItemStack stack) {
        return ChatUtil.fromItemStack(stack);
    }
    
    public Text append(final Object object) {
        return this.append(String.valueOf(object));
    }
    
    public Text append(final String text) {
        return (Text)this.a(text);
    }
    
    public Text append(final IChatBaseComponent node) {
        return (Text)this.addSibling(node);
    }
    
    public Text append(final IChatBaseComponent... nodes) {
        for (final IChatBaseComponent node : nodes) {
            this.addSibling(node);
        }
        return this;
    }
    
    public Text localText(final ItemStack stack) {
        return this.append((IChatBaseComponent)ChatUtil.localFromItem(stack));
    }
    
    public Text appendItem(final ItemStack stack) {
        return this.append((IChatBaseComponent)ChatUtil.fromItemStack(stack));
    }
    
    public Text setBold(final boolean bold) {
        this.getChatModifier().setBold(bold);
        return this;
    }
    
    public Text setItalic(final boolean italic) {
        this.getChatModifier().setItalic(italic);
        return this;
    }
    
    public Text setUnderline(final boolean underline) {
        this.getChatModifier().setUnderline(underline);
        return this;
    }
    
    public Text setRandom(final boolean random) {
        this.getChatModifier().setRandom(random);
        return this;
    }
    
    public Text setStrikethrough(final boolean strikethrough) {
        this.getChatModifier().setStrikethrough(strikethrough);
        return this;
    }
    
    public Text setColor(final ChatColor color) {
        this.getChatModifier().setColor(EnumChatFormat.valueOf(color.name()));
        return this;
    }
    
    public Text setClick(final ClickAction action, final String value) {
        this.getChatModifier().setChatClickable(new ChatClickable(action.getNMS(), value));
        return this;
    }
    
    public Text setHover(final HoverAction action, final IChatBaseComponent value) {
        this.getChatModifier().a(new ChatHoverable(action.getNMS(), value));
        return this;
    }
    
    public Text setHoverText(final String text) {
        return this.setHover(HoverAction.SHOW_TEXT, (IChatBaseComponent)new Text(text));
    }
    
    public Text reset() {
        ChatUtil.reset((IChatBaseComponent)this);
        return this;
    }
    
    public IChatBaseComponent f() {
        return (IChatBaseComponent)this.h();
    }
    
    public String toRawText() {
        return this.c();
    }
    
    public void send(final CommandSender sender) {
        ChatUtil.send(sender, (IChatBaseComponent)this);
    }
    
    public void broadcast() {
        this.broadcast(null);
    }
    
    public void broadcast(final String permission) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (permission == null || player.hasPermission(permission)) {
                this.send((CommandSender)player);
            }
        }
        this.send((CommandSender)Bukkit.getConsoleSender());
    }
}