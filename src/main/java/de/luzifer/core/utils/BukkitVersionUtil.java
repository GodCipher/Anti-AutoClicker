package de.luzifer.core.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class BukkitVersionUtil {

    public double get() {
        return current();
    }

    public boolean isOver(double version) {
        return current() < version;
    }

    public boolean is(double version) {
        return current() == version;
    }

    private double current() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        return Double.parseDouble(version.split("\\.")[1]);
    }
}
