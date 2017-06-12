package cz.sionzee.testplugin;

import cz.sionzee.bukkithelper.BukkitHelperTools;
import cz.sionzee.bukkithelper.events.EventHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class TestPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        BukkitHelperTools.OnEnable(TestPlayer.class);
        Bukkit.getPluginManager().registerEvents(this, this);

        //Autoregister listeners
        EventHelper.RegisterAllListeners("cz.sionzee.testplugin", (listenerClass -> {
            try {
                Bukkit.getPluginManager().registerEvents(listenerClass.newInstance(), this);
                Bukkit.broadcastMessage("Listener " + listenerClass.getSimpleName() + " was registered automatically.");
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void onDisable() {
        BukkitHelperTools.OnDisable();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onJoin(PlayerJoinEvent event) {
        BukkitHelperTools.OnJoin(event.getPlayer(), TestPlayer.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onLeave(PlayerQuitEvent event) {
        BukkitHelperTools.OnLeave(event.getPlayer());
    }

    /* EXAMPLE */

    @EventHandler
    void onChat(AsyncPlayerChatEvent event) {
        TestPlayer testPlayer = (TestPlayer) event.getPlayer();

        if(testPlayer.containsData("lastMessage")) testPlayer.sendMessage("Your last message is \"" + testPlayer.getData("lastMessage") + '"');
        else testPlayer.sendMessage("You sent first message! Yaaaaaaaaay");

        testPlayer.setData("lastMessage", event.getMessage());
    }

    public static Collection<TestPlayer> getOnlinePlayers() {
        return (Collection<TestPlayer>) Bukkit.getOnlinePlayers();
    }
}
