package com.beastsmc.flytimer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        FlyTimer.instance.getLogger().info("Player: " + p.getName() + "; CanFly: " + p.getAllowFlight() + "; IsFlying: " + p.isFlying());
        Long flightTime = FlyTimer.instance.fliers.get(p.getName().toLowerCase());
        if (flightTime == null) return;
        if (flightTime > System.currentTimeMillis()) {
            p.setAllowFlight(true);
        } else {
            FlyRemoverTask.expirePlayerFlight(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        FlyTimer.instance.getLogger().info("Player: " + p.getName() + "; CanFly: " + p.getAllowFlight() + "; IsFlying: " + p.isFlying());
    }
}
