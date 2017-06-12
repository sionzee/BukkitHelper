package cz.sionzee.bukkithelper;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * @author siOnzee
 * @version 1.1
 * BukkitHelper is class which make you developing better and faster
 */
public class BukkitHelper {
    private static final Field playerListField;
    private static final Field playersByNameField;
    private static final Field bukkitEntityField;

    static final String packageName;
    static final String version;

    static {

        packageName = Bukkit.getServer().getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf('.') + 1);

        Field playerListField1;
        try {
            playerListField1 = ReflectedAccess.GetCraftServerClass().getDeclaredField("playerList");
            playerListField1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            playerListField1 = null;
            e.printStackTrace();
        }
        playerListField = playerListField1;

        Field playersByNameField1;
        try {
            Assert.assertNotNull("PlayerListField is null!", playerListField);
            playersByNameField1 = playerListField.getType().getDeclaredField("playersByName");
            playersByNameField1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            playersByNameField1 = null;
            e.printStackTrace();
        }
        playersByNameField = playersByNameField1;

        Field bukkitEntityField1;
        try {
            bukkitEntityField1 = ReflectedAccess.GetEntityClass().getDeclaredField("bukkitEntity");
            bukkitEntityField1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            bukkitEntityField1 = null;
            e.printStackTrace();
        }
        bukkitEntityField = bukkitEntityField1;
    }

    static class ReflectedAccess {

        private static Class _craftServerClass;
        private static Class<?> _craftPlayerClass;
        private static Class _entityClass;
        private static Class _entityPlayerClass;

        static {
            try {
                _craftServerClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftServer");
                _craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
                _entityClass = Class.forName("net.minecraft.server." + version + ".Entity");
                _entityPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        static Class<?> GetCraftPlayerClass() {
            return _craftPlayerClass;
        }

        static Class<?> GetCraftServerClass() {
            return _craftServerClass;
        }

        static Class<?> GetEntityClass() {
            return _entityClass;
        }

        static Class<?> GetEntityPlayerClass() {
            return _entityPlayerClass;
        }

        static Object GetCraftServerInstance() {
            return _craftServerClass.cast(Bukkit.getServer());
        }

        static Object GetCraftPlayer(Player player) {
            return _craftPlayerClass.cast(player);
        }

        static Object CreateCraftPlayer(Object craftServer, Object entityPlayer) {
            Object result = null;
            try {
                Constructor<?> constructor = GetCraftPlayerClass().getConstructor(GetCraftServerClass(), GetEntityPlayerClass());
                result = constructor.newInstance(craftServer, entityPlayer);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }

            Assert.assertNotNull("Cannot find CraftPlayer constructor!", result);

            return result;
        }
    }


    public static <NewPlayer extends Player> void ReplacePlayer(Player player, Class<NewPlayer> newPlayerClass) {

        Assert.assertTrue("newPlayerClass object must extends CraftPlayer!", ReflectedAccess.GetCraftPlayerClass().isAssignableFrom(newPlayerClass));

        try {
            Object server = ReflectedAccess.GetCraftServerInstance();
            Object playerList = playerListField.get(server);
            Map<?, ?> playersByName = (Map<?, ?>) playersByNameField.get(playerList);
            String name = player.getName();
            Object entityPlayer = playersByName.get(name);
            bukkitEntityField.set(entityPlayer, CreateCustomPlayer(server, entityPlayer,  ReflectedAccess.GetCraftPlayer(player), newPlayerClass));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void RestorePlayer(Player player) {
        try {
            Object server = ReflectedAccess.GetCraftServerInstance();
            Object playerList = playerListField.get(server);
            Map<?, ?> playersByName = (Map<?, ?>) playersByNameField.get(playerList);
            String name = player.getName();
            Object entityPlayer = playersByName.get(name);
            bukkitEntityField.set(entityPlayer, CreateOriginalPlayer(server, entityPlayer, player));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static <NewPlayer extends Player> NewPlayer CreateCustomPlayer(Object craftServer, Object entityPlayer, Object craftPlayer, Class<NewPlayer> newPlayerClass) {

        Assert.assertTrue("craftServer object must be assignable from CraftServer!", ReflectedAccess.GetCraftServerClass().isAssignableFrom(craftServer.getClass()));
        Assert.assertTrue("entityPlayer object must be assignable from EntityPlayer!", ReflectedAccess.GetEntityPlayerClass().isAssignableFrom(entityPlayer.getClass()));
        Assert.assertTrue("craftPlayer object must be assignable from CraftPlayer!", ReflectedAccess.GetCraftPlayerClass().isAssignableFrom(craftPlayer.getClass()));
        Assert.assertTrue("newPlayerClass object must extends CraftPlayer!", ReflectedAccess.GetCraftPlayerClass().isAssignableFrom(newPlayerClass));

        NewPlayer extendedPlayer;
        try {
            extendedPlayer = newPlayerClass.getDeclaredConstructor(ReflectedAccess.GetCraftServerClass(), ReflectedAccess.GetEntityPlayerClass()).newInstance(craftServer, entityPlayer);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        ((Player) craftPlayer).recalculatePermissions();
        return CopyAllReferencesTo(craftPlayer, extendedPlayer);
    }

    static Object CreateOriginalPlayer(Object craftServer, Object entityPlayer, Player player) {
        Object craftPlayer = ReflectedAccess.CreateCraftPlayer(craftServer, entityPlayer);
        player.recalculatePermissions();
        CopyAllReferencesTo(player, craftPlayer);
        return craftPlayer;
    }

    @Nullable
    static <Output> Output CopyAllReferencesTo(Object from, Output to) {
        if(from == null || to == null)
            return null;

        if(from.getClass().getName().equals(to.getClass().getName())) return null;
        List<Field> targetFields = GetAllFields(to.getClass());
        for(Field field : GetAllFields(from.getClass())) {
            if(targetFields.stream().noneMatch(f -> f.getName().equalsIgnoreCase(field.getName())))
                continue;
            try {
                boolean isAccessible = field.isAccessible();
                if(Modifier.isFinal(field.getModifiers()))
                    AccessFinal(field);

                if(Modifier.isStatic(field.getModifiers()))
                    continue;

                field.setAccessible(true);
                field.set(to, field.get(from));
                field.setAccessible(isAccessible);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return to;
    }

    static List<Field> GetAllFields(Class<?> startClass) {
        List<Field> currentClassFields = Lists.newArrayList(startClass.getDeclaredFields());
        Class<?> parentClass = startClass.getSuperclass();
        if (parentClass != null) {
            List<Field> parentClassFields = GetAllFields(parentClass);
            currentClassFields.addAll(parentClassFields);
        }
        return currentClassFields;
    }

    static void AccessFinal(Field field) throws NoSuchFieldException, IllegalAccessException {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        modifiersField.setAccessible(false);
    }
}
