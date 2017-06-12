# BukkitHelper
This is a BukkitHelper library

## Installation

Copy content of src (without testplugin package) into your plugin.

## Usage

* Create a class which extends CraftPlayer.
* Do not add parameters to class constructor. (Let it default (CraftServer, EntityPlayer))
* Do not forget to add super(server, entityPlayer);

There is class which have to make implementation easier (use if you want)
__BukkitHelperTools__

## Update 1.1
* Accessing classes via reflection by finding class (it's add support for cross versions)
* Added Maven Support
* Added EventHelper class what allows auto register listeners. You don't need care about it more.

### BukkitHelperTools

```java
public class YourPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        BukkitHelperTools.OnEnable(TestPlayer.class);
    }

    @Override
    public void onDisable() {
        BukkitHelperTools.OnDisable();
    }
    
    /* Do not forget to register listeners */

    @EventHandler(priority = EventPriority.LOWEST) // To call it first
    void onJoin(PlayerJoinEvent event) {
        BukkitHelperTools.OnJoin(event.getPlayer(), TestPlayer.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // To call it last
    void onLeave(PlayerQuitEvent event) {
        BukkitHelperTools.OnLeave(event.getPlayer());
    }
}
```

### EventHelper
This class allow you register register Bukkit Listener automatically.
There is a Consumer for your custom constructor declaration.
If you don't have idea how to use it, copy it from example below this and edit package name to your package.

```java
public class YourPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
         EventHelper.RegisterAllListeners("cz.sionzee.testplugin", (listenerClass -> {
            try {
                Bukkit.getPluginManager().registerEvents(listenerClass.newInstance(), this);
                Bukkit.broadcastMessage("Listener " + listenerClass.getSimpleName() + " was registered automatically.");
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }));
    }
}
```