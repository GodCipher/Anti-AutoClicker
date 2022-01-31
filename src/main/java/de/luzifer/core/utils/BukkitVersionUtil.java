package de.luzifer.core.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class BukkitVersionUtil {

    public static double get() {
        return current();
    }

    public static boolean isOver(double version) {
        return current() < version;
    }

    public static boolean is(double version) {
        return current() == version;
    }

    private static double current() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        return Double.parseDouble(version.split("\\.")[1]);
    }
}
