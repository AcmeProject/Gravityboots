package net.poweredbyhate.gravityboots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Lax on 5/15/2016.
 */
public class GravityBootsCommand implements CommandExecutor {

    GravityBoots plogen;

    public GravityBootsCommand(GravityBoots plogen) {
        this.plogen = plogen;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "Gravity boots coded by LaxWasHere");
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("spawn") && sender.hasPermission("gravityboots.spawn") && sender instanceof Player) {
                ((Player) sender).getInventory().addItem(plogen.boots);
                sender.sendMessage(ChatColor.GOLD + plogen.getConfig().getString("prefix") + ChatColor.GREEN + " You spawned a gravity boot!");
            }
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage("/gb - Shows you the plugin version and who made it.");
                sender.sendMessage("/gb spawn - give yourself a gravity boot");
                sender.sendMessage("/gb give <player> - Gives a gravity boot to the player.");
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("gravityboots.give")) {
                if (Bukkit.getPlayer(args[1]) != null) {
                    Player p = Bukkit.getPlayer(args[1]);
                    p.getInventory().addItem(plogen.boots);
                    sender.sendMessage(ChatColor.GOLD + plogen.getConfig().getString("prefix") + ChatColor.GREEN + " You gave " + p.getName() + " a gravity boot!");
                } else {
                    sender.sendMessage(ChatColor.GOLD + plogen.getConfig().getString("prefix") + ChatColor.GREEN + " Player offline!");
                }
            }
        }
        return false;
    }
}
