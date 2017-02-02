package cz.sionzee.bukkithelper;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BukkitHelperTools {

    public static <NewPlayer extends CraftPlayer> void OnEnable(Class<NewPlayer> newPlayerClass) {
        Bukkit.getOnlinePlayers().forEach(p -> BukkitHelper.ReplacePlayer(p, newPlayerClass));
    }

    public static void OnDisable() {
        Bukkit.getOnlinePlayers().forEach(BukkitHelper::RestorePlayer);
        System.gc();
    }

    public static <NewPlayer extends CraftPlayer> void OnJoin(Player player, Class<NewPlayer> newPlayerClass) {
        BukkitHelper.ReplacePlayer(player, newPlayerClass);
    }

    public static void OnLeave(Player player) {
        BukkitHelper.RestorePlayer(player);
        System.gc();
    }
}
