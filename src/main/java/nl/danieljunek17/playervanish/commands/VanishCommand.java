package nl.danieljunek17.playervanish.commands;

// alle dependecies die nodig zijn voor het vanish commando

import nl.danieljunek17.playervanish.PlayerVanish;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.Bukkit.getServer;


public class VanishCommand implements CommandExecutor {

    //public HashMap<UUID, Effect> stored = new HashMap<UUID, Effect>();
    public  Map<Player, BossBar> bossBarMap = new HashMap<>();
    public static List<UUID> hiddenUUID = new ArrayList<>();

    private final PlayerVanish plugin;

    public VanishCommand(PlayerVanish playervanish) {
        this.plugin = playervanish;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("list")) { // laat alle mensen in vanish zien als je de volgende permissie hebt
                if (!player.hasPermission("vanish.list")) {
                    player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "Je hebt geen permissie hiertoe");
                    return true;
                }
                if (hiddenUUID.size() > 0) { // kijkt of er spelers in vanish zijn en als het zo is stuurt het een lijst
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < hiddenUUID.size(); i++) {
                        if (player.getServer().getPlayerExact(args[0]) != null){
                            builder.append(Bukkit.getPlayer(hiddenUUID.get(i)).getDisplayName());
                        } else if (player.getServer().getPlayerExact(args[0]) == null){
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(hiddenUUID.get(i));
                            String PlayerName = offlinePlayer.getName();
                            builder.append(PlayerName);
                        }
                        if (i < hiddenUUID.size() - 1) {
                            builder.append(", ");
                        }
                    }

                    player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "Hidden players: " + builder.toString());
                } else {
                    player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "There are no hidden players");
                }
                return true;
            }
            if (player.hasPermission("vanish.vanishother")) { // als je een speler hebt opgegeven en de permissie hebt verander opgegeven speler van vanishstatus
                Player target = getServer().getPlayer(args[0]);
                if (target == null) target = getServer().getPlayerExact(args[0]);
                if (target == null) {
                    player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "Deze speler bestaat niet");
                    return true;
                }

                if (!isVanished(target)) { // als de persoon niet vanished is vanish hem
                    vanishPlayer(target);
                    target.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "Je bent vanished");
                    player.sendMessage(ChatColor.GRAY +  plugin.getMessagePrefix() + target.getName() + " is nu vanished");
                    getServer().broadcastMessage(ChatColor.YELLOW + target.getName() + " left the game.");
                } else { // als de persoon wel vanished is unvanish hem
                    showPlayer(target);
                    target.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "Je bent niet langer vanished");
                    player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + target.getName() + " is is niet langer vanished");
                    getServer().broadcastMessage(ChatColor.YELLOW + target.getName() + " joined the game.");
                }
            } else {
                player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "Je hebt geen permissie hiertoe");
            }
            return true;
        } else if (player.hasPermission("vanish.own")) { // als je toegang hebt jezelf in vanish te zetten
            if (!isVanished(player)) { // als je niet vanished bent vanish
                vanishPlayer(player);
                player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "je bent vanished");
                getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " left the game.");
            } else { // als je wel vanished bent unvanish
                showPlayer(player);
                player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "je bent niet langer vanished");
                getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " joined the game.");
            }
            return true;
        } else {
            player.sendMessage(ChatColor.GRAY + plugin.getMessagePrefix() + "Je hebt geen permissie hiertoe");
            return true;
        }
    }
    // wordt gebruikt om te kijken of de player vanished is
    public static boolean isVanished(Player player) {
        return hiddenUUID.contains(player.getUniqueId());
    }
    // wordt uitgevoerd om de player in vanish te zetten
    public void vanishPlayer(Player player) {
        hiddenUUID.add(player.getUniqueId());
        for (Player player1: getServer().getOnlinePlayers()) {
            if (player1 == player) {
                continue;
            } else if (player1.hasPermission("vanish.seeall")) {
                player1.sendMessage(ChatColor.GRAY + player.getName() + " vanished");
                continue;
            }
            player1.hidePlayer(plugin, player);
        }
        spawnParticles(player.getLocation(), Particle.FLAME, 60, 0.5); // zet particles neer op vanish
        bar(player);
    }
    //maakt een particle effect aan
    public void spawnParticles(Location location, Particle particle, int amount, double offset) {
        location.getWorld().spawnParticle(particle, location, amount, offset, offset, offset);
    }
    //maakt dat de player unvanished
    public void showPlayer(Player player) {
        hiddenUUID.remove(player.getUniqueId());
        for (Player player1 : getServer().getOnlinePlayers()) {
            spawnParticles(player.getLocation(), Particle.FLAME, 60, 0.5); // zet particles neer op unvanish
            bar(player);
            player1.showPlayer(plugin, player);
        }
    }
    // zet een bossbar boven je als je in vanish staat
    public void bar(Player player) {
        if (bossBarMap.containsKey(player)) {
            bossBarMap.get(player).removePlayer(player);
        }
        if (hiddenUUID.contains(player.getUniqueId())){
            BossBar bossBar = Bukkit.createBossBar("Vanished", BarColor.GREEN, BarStyle.SOLID);
            bossBar.addPlayer(player);
            bossBarMap.put(player, bossBar);
        }
    }
}