package com.doctordark.util.chat;

import org.bukkit.potion.*;
import org.apache.commons.lang.*;
import org.bukkit.craftbukkit.v1_7_R4.inventory.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import net.minecraft.server.v1_7_R4.*;

public class ChatUtil
{
    public static String getName(final ItemStack stack) {
        if (stack.tag != null && stack.tag.hasKeyOfType("display", 10)) {
            final NBTTagCompound nbttagcompound = stack.tag.getCompound("display");
            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return nbttagcompound.getString("Name");
            }
        }
        return stack.getItem().a(stack) + ".name";
    }
    
    public static Trans localFromItem(final org.bukkit.inventory.ItemStack stack) {
        if (stack.getType() == Material.POTION && stack.getData().getData() == 0) {
            final Potion potion = Potion.fromItemStack(stack);
            if (potion != null) {
                final PotionType type = potion.getType();
                if (type != null && type != PotionType.WATER) {
                    final String effectName = (potion.isSplash() ? "Splash " : "") + WordUtils.capitalizeFully(type.name().replace('_', ' ')) + " L" + potion.getLevel();
                    return fromItemStack(stack).append(" of " + effectName);
                }
            }
        }
        return fromItemStack(stack);
    }
    
    public static Trans fromItemStack(final org.bukkit.inventory.ItemStack stack) {
        final ItemStack nms = CraftItemStack.asNMSCopy(stack);
        final NBTTagCompound tag = new NBTTagCompound();
        nms.save(tag);
        return new Trans(getName(nms), new Object[0]).setColor(ChatColor.getByChar(nms.w().e.getChar())).setHover(HoverAction.SHOW_ITEM, (IChatBaseComponent)new ChatComponentText(tag.toString()));
    }
    
    public static void reset(final IChatBaseComponent text) {
        final ChatModifier modifier = text.getChatModifier();
        modifier.a((ChatHoverable)null);
        modifier.setChatClickable((ChatClickable)null);
        modifier.setBold(false);
        modifier.setColor(EnumChatFormat.RESET);
        modifier.setItalic(false);
        modifier.setRandom(false);
        modifier.setStrikethrough(false);
        modifier.setUnderline(false);
    }
    
    public static void send(final CommandSender sender, final IChatBaseComponent text) {
        if (sender instanceof Player) {
            final Player player = (Player)sender;
            final PacketPlayOutChat packet = new PacketPlayOutChat(text, true);
            final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            entityPlayer.playerConnection.sendPacket((Packet)packet);
        }
        else {
            sender.sendMessage(text.c());
        }
    }
}