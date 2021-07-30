package de.vantrex.jointutorial.player;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerUtil {

    public static void sendTitle(final Player player, final String text, final int fadein, final int showtime, final int fadeout) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(fadein, showtime, fadeout));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), fadein, showtime, fadeout));
    }

    public static void sendSubTitle(final Player player, final String text, final int fadein, final int showtime, final int fadeout) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(fadein, showtime, fadeout));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), fadein, showtime, fadeout));
    }

    public static void clear(final Player player) {
        for (final PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFireTicks(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setMaximumNoDamageTicks(20);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        player.setFallDistance(0.0f);
        player.getInventory().setHeldItemSlot(0);
        player.updateInventory();
    }

    public static void hidePlayer(Player hiding, Player from) {
        from.hidePlayer(hiding);
        EntityPlayer nmsFrom = ((CraftPlayer) from).getHandle();
        EntityPlayer nmsHiding = ((CraftPlayer) hiding).getHandle();
        nmsFrom.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, nmsHiding));
    }

}
