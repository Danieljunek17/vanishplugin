package nl.danieljunek17.playervanish;

// alle dependecies die nodig zijn hier
import nl.danieljunek17.playervanish.commands.VanishCommand;
import nl.danieljunek17.playervanish.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class PlayerVanish extends JavaPlugin {

    public static PlayerVanish instance;
    public VanishCommand vanishCommand;
    public PlayerJoinListener playerJoinListener;

    private final String messagePrefix = "[Danieljunek17TestPlugin] ";

    @Override
    public void onEnable() { // laad alles in als je opstart
        instance = this;
        vanishCommand = new VanishCommand(instance);
        playerJoinListener = new PlayerJoinListener(instance);

        getCommand("vanish").setExecutor(vanishCommand);
        Bukkit.getPluginManager().registerEvents(playerJoinListener, this);
    }

    @Override
    public void onDisable() { // remove bij iedereen die in vanish staat de bossbar voor reload en/of stop van de server
        for (Map.Entry<Player, BossBar> bossBarEntry : vanishCommand.bossBarMap.entrySet()) {
            bossBarEntry.getValue().removePlayer(bossBarEntry.getKey());
        }

    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

}