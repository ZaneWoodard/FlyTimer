package com.beastsmc.flytimer;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class FlyTimer extends JavaPlugin {
    public static FlyTimer instance;
    public static FlyRemoverTask removerTask;
    public HashMap<String, Long> fliers;

    public void onEnable() {
        instance = this;
        fliers = new HashMap<String, Long>();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("flytimer").setExecutor(this);

        removerTask = new FlyRemoverTask();
        removerTask.runTaskTimer(this, 20, 20);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("flytimer")) {
            sender.sendMessage("Invalid command");
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage("Invalid argument length. Requires 2.");
            return false;
        }

        final String playerName = args[0].toLowerCase();
        final String duration = args[1];

        if (!playerName.matches("[a-zA-Z0-9_]{1,16}")) {
            sender.sendMessage("First argument must be player name! " + playerName + " does not match! Ex: KablooieKablam");
            return false;
        }
        if (!duration.matches("[0-9]{1,8}(s|m|h|d)")) {
            sender.sendMessage("Second argument must be duration! " + duration + " does not match! Ex: 60s, 1m, 1h, 1d");
            return false;
        }


        char unit = args[1].charAt(args[1].length() - 1);
        long time = Integer.parseInt(args[1].substring(0, args[1].length() - 1));

        long milliDuration = durationFormatToMillis(time, unit);
        Long baseTime = fliers.get(playerName);
        long expiration = (baseTime == null) ? System.currentTimeMillis() + milliDuration : baseTime + milliDuration;
        fliers.put(playerName, expiration);

        final Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            player.setAllowFlight(true);
            player.sendMessage(ChatColor.GREEN + "You have been awarded " + duration + " of flight!");
        }
        getLogger().info(duration + " of fly time awarded to " + playerName);
        return true;
    }


    public static long durationFormatToMillis(long time, char unit) {
        long millis = time;
        switch (unit) {
            case 'd':
                millis *= 24;
            case 'h':
                millis *= 60;
            case 'm':
                millis *= 60;
            case 's':
                millis *= 1000;
        }
        return millis;
    }
}
