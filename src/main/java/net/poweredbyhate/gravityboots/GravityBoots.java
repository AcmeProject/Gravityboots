package net.poweredbyhate.gravityboots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class GravityBoots extends JavaPlugin implements Listener {

    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS, 1);
    public ArrayList<UUID> cooldown = new ArrayList<>();

    public void onEnable() {
        saveDefaultConfig();
        setBoots();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("gravityboots").setExecutor(new GravityBootsCommand(this));
        if (getConfig().getBoolean("recipe")) {
            Bukkit.getServer().addRecipe(new ShapedRecipe(boots).shape("ooo","oxo","lel").setIngredient('x', Material.DIAMOND_BOOTS).setIngredient('l', Material.ENDER_PEARL).setIngredient('e', Material.NETHER_STAR));
        }
    }

    public void setBoots() {
        ItemMeta m = boots.getItemMeta();
        m.setDisplayName(ChatColor.GREEN + "Gravity Boots");
        m.addEnchant(Enchantment.LURE, 2, true);
        boots.setItemMeta(m);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent ev) { //oh boy, if checks.
        Player p = ev.getPlayer();
        if (p.getInventory().getBoots() == null) {
            return;
        }
        if (bootCheck(p.getInventory().getBoots())) {
            if (!p.isSneaking()) {
                if (cooldown.contains(p.getUniqueId())) {
                    toggleMessage(p, getConfig().getString("toggle-message-cooldown"));
                    return;
                }
                if (p.hasPotionEffect(PotionEffectType.LEVITATION)) {
                    p.removePotionEffect(PotionEffectType.LEVITATION);
                    toggleMessage(p, getConfig().getString("toggle-message-enable"));
                    startCooldown(p.getUniqueId());
                } else {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, getUniverse(), 1));
                    toggleMessage(p, getConfig().getString("toggle-message-disable"));
                    startCooldown(p.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent ev) {
        if (!(ev.getEntity() instanceof Player)) {
            return;
        }
        if (!getConfig().getBoolean("disable-fall-damage")) {
            return;
        }
        Player p = (Player) ev.getEntity();
        if (p.getInventory().getBoots() == null) {
            return;
        }
        if (ev.getCause() == EntityDamageEvent.DamageCause.FALL && bootCheck(p.getInventory().getBoots())) {
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent ev) {
        Player p = (Player) ev.getWhoClicked();
        if (p.getInventory().getBoots() == null && p.hasPotionEffect(PotionEffectType.LEVITATION)) {
            p.removePotionEffect(PotionEffectType.LEVITATION);
        }
    }

    public void startCooldown(final UUID u) {
        if (!getConfig().getBoolean("enable-toggle-cooldown") || Bukkit.getPlayer(u).hasPermission("gravityboots.admin")) {
            return;
        }
        if (!cooldown.contains(u)) {
            cooldown.add(u);
            new BukkitRunnable() {
                @Override
                public void run() {
                    cooldown.remove(u);
                }
            }.runTaskLater(this, 20 * getConfig().getInt("toggle-cooldown-time"));
        }
    }

    public void toggleMessage(Player p, String m) {
        if (getConfig().getBoolean("send-toggle-message")) {
            p.sendMessage(ChatColor.GOLD + getConfig().getString("prefix") + ChatColor.translateAlternateColorCodes('&', m));
        }
    }

    public int getUniverse() {
        return Integer.MAX_VALUE;
    }

    public boolean bootCheck(ItemStack i) {
        return i.getEnchantmentLevel(Enchantment.LURE) == 2;
        //return i.hasItemMeta() && i.getItemMeta().getDisplayName() != null && ChatColor.stripColor(i.getItemMeta().getDisplayName()).equalsIgnoreCase("Gravity Boots");
    }

}
