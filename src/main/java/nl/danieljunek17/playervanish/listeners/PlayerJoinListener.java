package nl.danieljunek17.playervanish.listeners;

// alle dependecies die nodig zijn voor de PlayerJoinListener
import nl.danieljunek17.playervanish.PlayerVanish;
import nl.danieljunek17.playervanish.commands.VanishCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private PlayerVanish plugin;
    VanishCommand vanish = PlayerVanish.instance.vanishCommand;

    public PlayerJoinListener(PlayerVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (vanish.isVanished(player)) { // unvanish player als player vanished is
            vanish.showPlayer(player);
        }
        if (player.hasPermission("vanish.seeall")) return; // als player permissies heeft vanished players te zien return
        for (Player player1 : plugin.getServer().getOnlinePlayers()) { // maak dat alle spelers die joinen niemand kunnen zien in vanish
            if (vanish.isVanished(player1) && player != player1) {
                player.hidePlayer(plugin, player1);
            }
        }
    }
}