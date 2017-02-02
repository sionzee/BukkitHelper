package cz.sionzee.testplugin;

import net.minecraft.server.v1_11_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TestPlayer extends CraftPlayer {
    private final Map<String, Object> __data;

    public TestPlayer(CraftServer server, EntityPlayer entity) {
        super(server, entity);
        __data = new HashMap<>();
    }

    public Object getData(String key) {
        return this.__data.get(key);
    }

    public Object setData(String key, Object data) {
        this.__data.put(key, data);
        return data;
    }

    @Nullable
    public Object setDataReturnExists(String key, Object data) {
        return this.__data.put(key, data);
    }

    public Object removeData(String key) {
        return this.__data.remove(key);
    }

    public boolean containsData(String key) {
        return this.__data.containsKey(key);
    }

    @Override
    protected void finalize() throws Throwable {
        __data.clear();
        super.finalize();
    }
}
