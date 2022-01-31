package de.luzifer.core.utils;

import de.luzifer.core.model.events.actionbarapi.ActionBarMessageEvent;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@UtilityClass
public class ActionBarUtil {
    
    private static String nmsver;
    private static boolean useOldMethods;
    
    static {
        useOldMethods = false;
    }
    
    public void load() {
        
        ActionBarUtil.nmsver = Bukkit.getServer().getClass().getPackage().getName();
        ActionBarUtil.nmsver = ActionBarUtil.nmsver.substring(nmsver.lastIndexOf(".") + 1);
    
        if (ActionBarUtil.nmsver.equalsIgnoreCase("v1_8_R1") || ActionBarUtil.nmsver.startsWith("v1_7_")) ActionBarUtil.useOldMethods = true;
    }
    
    public void sendActionBar(final Player player, final String message) {
        
        if (!player.isOnline()) return;
        
        ActionBarMessageEvent actionBarMessageEvent = new ActionBarMessageEvent(player, message);
        Bukkit.getPluginManager().callEvent(actionBarMessageEvent);
        if (actionBarMessageEvent.isCancelled()) return;
        
        if (getBukkitVersion() >= 16) {
            
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            return;
        }
        
        try {
            final Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            final Object craftPlayer = craftPlayerClass.cast(player);
            final Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            final Class<?> packetClass = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Object packet;
            if (useOldMethods) {
                final Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
                final Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                final Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                final Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(cbc, 2);
            } else {
                final Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
                final Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                try {
                    final Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType");
                    final Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                    Object chatMessageType = null;
                    for (final Object obj : chatMessageTypes) {
                        if (obj.toString().equals("GAME_INFO")) {
                            chatMessageType = obj;
                        }
                    }
                    final Object chatCompontentText = chatComponentTextClass.getConstructor(String.class).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass).newInstance(chatCompontentText, chatMessageType);
                } catch (ClassNotFoundException cnfe) {
                    final Object chatCompontentText2 = chatComponentTextClass.getConstructor(String.class).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(chatCompontentText2, (byte) 2);
                }
            }
            final Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            final Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            final Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            final Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            final Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private double getBukkitVersion() {
        
        String version = Bukkit.getBukkitVersion().split("-")[0];
        return Double.parseDouble(version.split("\\.")[1]);
    }
    
}
