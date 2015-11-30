package com.beastsmc.flytimer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class FlyRemoverTask extends BukkitRunnable {
    public void run() {
        ArrayList<Player> remove = new ArrayList<Player>();
        for (String player : FlyTimer.instance.fliers.keySet()) {
            Player p = Bukkit.getPlayer(player);
            if (p == null) continue;
            final long secondsLeft = (FlyTimer.instance.fliers.get(player) - System.currentTimeMillis()) / 1000;
            if (secondsLeft == 0) {
                remove.add(p);
            } else if ((secondsLeft >= 1 && secondsLeft <= 5) || secondsLeft == 30 || secondsLeft == 60 || secondsLeft == 120) {
                p.sendMessage(ChatColor.RED + "You have only " + ChatColor.BOLD + ChatColor.GOLD + secondsLeft + ChatColor.RED + " seconds of flight left");
            }
        }
        for (Player p : remove) {
            expirePlayerFlight(p);
        }
    }

    public static void expirePlayerFlight(Player p) {
        FlyTimer.instance.getLogger().info("Expiring flight for: " + p.getName());
        //Expire
        p.sendMessage(ChatColor.RED + "Your flight time has expired!");
        p.setAllowFlight(false);
        p.setFlying(false);

        //Teleport to safe block
        p.teleport(getSafeLandingLocation(p.getLocation()));
        p.sendMessage(ChatColor.RED + "You have been teleported safely to the ground");

        FlyTimer.instance.fliers.remove(p.getName().toLowerCase());
    }

    public static Location getSafeLandingLocation(Location pLoc) {
        int pX = pLoc.getBlockX();
        int pY = pLoc.getBlockY();
        int pZ = pLoc.getBlockZ();
        if (pY <= 0) return pLoc.getWorld().getSpawnLocation(); //Player is in void, cannot land them so send to spawn
        if (pY > 255) pY = 255; //Player is above all possible blocks, set to highest possible block to save time

        while (pLoc.getWorld().getBlockAt(pX, pY, pZ).getType() == Material.AIR && pY >= 0) {
            pY--;
        }

        //If in column of air, iteration could reach -1, in which case send to World spawn
        return (pY >= 0) ? new Location(pLoc.getWorld(), pLoc.getX(), pY + 1, pLoc.getZ(), pLoc.getYaw(), pLoc.getPitch()) : pLoc.getWorld().getSpawnLocation();
    }
}
