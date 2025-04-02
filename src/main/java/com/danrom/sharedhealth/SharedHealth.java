package com.danrom.sharedhealth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class SharedHealth extends JavaPlugin implements Listener {
    private final Set<Player> linkedPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);
        getCommand("linkhealth");
        getCommand("unlinkhealth");
        getLogger().info("sharedHealth is enabled.");
    }

    @Override
    public void onDisable() { getLogger().info("sharedHealth is disabled."); }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!linkedPlayers.contains(player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return;

        var knockback = player.getVelocity();

        for (Player linkedPlayer : linkedPlayers) {
            if (!linkedPlayer.equals(player)) {
                linkedPlayer.damage(event.getDamage());
                linkedPlayer.setVelocity(knockback);
            }
        }
    }

    public void linkPlayers(Player p1, Player p2) {
        linkedPlayers.clear();
        linkedPlayers.add(p1);
        linkedPlayers.add(p2);
        p1.sendMessage(ChatColor.GREEN + "Your health is now linked with " + ChatColor.GOLD + p2.getName());
        p2.sendMessage(ChatColor.GREEN + "Your health is now linked with " + ChatColor.GOLD + p1.getName());
    }

    public void unlinkPlayers() {
        for (Player player : linkedPlayers) {
            player.sendMessage( ChatColor.BOLD + "" + ChatColor.GREEN + "Health is now not linked.");
        }
        linkedPlayers.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("linkhealth")) {
            if (args.length != 2) return false;
            Player p1 = Bukkit.getPlayer(args[0]);
            Player p2 = Bukkit.getPlayer(args[1]);
            if (p1 == null || p2 == null) {
                sender.sendMessage(ChatColor.DARK_RED + "One of the players was not found!");
                return true;
            }
            linkPlayers(p1, p2);
            return true;
        }
        if (command.getName().equalsIgnoreCase("unlinkhealth")) {
            unlinkPlayers();
            return true;
        }
        return false;
    }
}
