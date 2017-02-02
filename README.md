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

    @EventHandler(priority = EventPriority.HIGHEST) // To call it latest
    void onLeave(PlayerQuitEvent event) {
        BukkitHelperTools.OnLeave(event.getPlayer());
    }
}
```