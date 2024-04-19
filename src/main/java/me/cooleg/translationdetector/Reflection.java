package me.cooleg.translationdetector;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

    public static int getAnvilId() {
        try {
            Inventory inventory = Bukkit.createInventory(null, InventoryType.ANVIL);
            Method notchInventory = Class.forName(cbClass("inventory.CraftContainer")).getMethod("getNotchInventoryType", Inventory.class);

            Object value = notchInventory.invoke(null, inventory);

            try {
                Field[] fields = Class.forName("net.minecraft.world.inventory.Containers").getFields();

                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].get(null) == value) {
                        LogManager.getLogger(TranslationDetector.class).log(Level.INFO, "Spigot mapped anvil inventory ID: " + i);
                        return i;
                    }
                }
            } catch (Exception ignored) {}

            try {
                Field[] fields = Class.forName("net.minecraft.world.inventory.MenuType").getFields();

                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].get(null) == value) {
                        LogManager.getLogger(TranslationDetector.class).log(Level.INFO, "Mojang mapped anvil inventory ID: " + i);
                        return i;
                    }
                }
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}

        LogManager.getLogger(TranslationDetector.class).log(Level.ERROR, "No anvil inventory ID could be found. Falling back to 8 (1.20.3+).");
        return 8;
    }

    public static String cbClass(String clazz) {
        return CRAFTBUKKIT_PACKAGE + "." + clazz;
    }


}
