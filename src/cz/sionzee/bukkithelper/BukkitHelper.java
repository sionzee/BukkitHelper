package cz.sionzee.bukkithelper;

import com.google.common.collect.Lists;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * @author siOnzee
 * @version 1.0
 * BukkitHelper is class which make you developing better and faster
 */
public class BukkitHelper {
    private static final Field playerListField;
    private static final Field playersByNameField;
    private static final Field bukkitEntityField;

    static {
        Field playerListField1;
        try {
            playerListField1 = CraftServer.class.getDeclaredField("playerList");
            playerListField1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            playerListField1 = null;
            e.printStackTrace();
        }
        playerListField = playerListField1;

        Field playersByNameField1;
        try {
            playersByNameField1 = PlayerList.class.getDeclaredField("playersByName");
            playersByNameField1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            playersByNameField1 = null;
            e.printStackTrace();
        }
        playersByNameField = playersByNameField1;

        Field bukkitEntityField1;
        try {
            bukkitEntityField1 = Entity.class.getDeclaredField("bukkitEntity");
            bukkitEntityField1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            bukkitEntityField1 = null;
            e.printStackTrace();
        }
        bukkitEntityField = bukkitEntityField1;
    }

    public static <NewPlayer extends CraftPlayer> void ReplacePlayer(Player player, Class<NewPlayer> newPlayerClass) {
        try {
            CraftServer server = (CraftServer) Bukkit.getServer();
            PlayerList playerList = (PlayerList) playerListField.get(server);
            Map<String, EntityPlayer> playersByName = (Map<String, EntityPlayer>) playersByNameField.get(playerList);
            String name = player.getName();
            EntityPlayer entityPlayer = playersByName.get(name);
            bukkitEntityField.set(entityPlayer, CreateCustomPlayer(server, entityPlayer, (CraftPlayer) player, newPlayerClass));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void RestorePlayer(Player player) {
        try {
            CraftServer server = (CraftServer) Bukkit.getServer();
            PlayerList playerList = (PlayerList) playerListField.get(server);
            Map<String,EntityPlayer> playersByName = (Map<String, EntityPlayer>) playersByNameField.get(playerList);
            String name = player.getName();
            EntityPlayer entityPlayer = playersByName.get(name);
            bukkitEntityField.set(entityPlayer, CreateOriginalPlayer(server, entityPlayer, player));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static <NewPlayer extends CraftPlayer> NewPlayer CreateCustomPlayer(CraftServer craftServer, EntityPlayer entityPlayer, CraftPlayer craftPlayer, Class<NewPlayer> newPlayerClass) {
        NewPlayer extendedPlayer;
        try {
            extendedPlayer = newPlayerClass.getDeclaredConstructor(CraftServer.class, EntityPlayer.class).newInstance(craftServer, entityPlayer);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        craftPlayer.recalculatePermissions();
        CopyAllReferencesTo(craftPlayer, extendedPlayer);
        return extendedPlayer;
    }

    static CraftPlayer CreateOriginalPlayer(CraftServer server, EntityPlayer entity, Player player) {
        CraftPlayer craftPlayer = new CraftPlayer(server, entity);
        player.recalculatePermissions();
        CopyAllReferencesTo(player, craftPlayer);
        return craftPlayer;
    }

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
