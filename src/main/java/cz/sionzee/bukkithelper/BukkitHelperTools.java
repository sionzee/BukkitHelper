package cz.sionzee.bukkithelper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitHelperTools {

    public static <NewPlayer extends Player> void OnEnable(Class<NewPlayer> newPlayerClass) {
        Bukkit.getOnlinePlayers().forEach(p -> BukkitHelper.ReplacePlayer(p, newPlayerClass));
    }

    public static void OnDisable() {
        Bukkit.getOnlinePlayers().forEach(BukkitHelper::RestorePlayer);
        System.gc();
    }

    public static <NewPlayer extends Player> void OnJoin(Player player, Class<NewPlayer> newPlayerClass) {
        BukkitHelper.ReplacePlayer(player, newPlayerClass);
    }

    public static void OnLeave(Player player) {
        BukkitHelper.RestorePlayer(player);
        System.gc();
    }
}
