package de.luzifer.core.model.punishment.implementations;

import de.luzifer.core.model.punishment.Punishment;
import de.luzifer.core.model.user.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BanPunishment implements Punishment {

    @Override
    public void punish(User user) {

        Player player = user.getPlayer();

        if (Variables.executeBanCommand != null || !Variables.executeBanCommand.isEmpty()) {

            String command = Variables.executeBanCommand;
            command = command.replace("%player%", player.getName()).replace("&", "§");

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, + Variables.unbanAfterHours);

        String finalDate = dateFormat.format(calendar.getTime());
        ArrayList<String> reasons = new ArrayList<>(Variables.BAN_REASON);
        String reason = "§cAnti§4AC \n " + String.join("\n ", reasons).replace("&", "§").replace("%date%", finalDate);

        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason, calendar.getTime(), "AntiAC");
    }
}
