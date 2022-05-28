package ro.cofi.respawnablecrystals;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ro.cofi.respawnablecrystals.command.RSCrystalsCommand;
import ro.cofi.respawnablecrystals.command.RSCrystalsTabCompleter;
import ro.cofi.respawnablecrystals.listener.CrystalSpawnListener;
import ro.cofi.respawnablecrystals.listener.DragonDeathListener;
import ro.cofi.respawnablecrystals.listener.DragonPhaseListener;
import ro.cofi.respawnablecrystals.listener.ServerTickListener;
import ro.cofi.respawnablecrystals.logic.ConfigManager;
import ro.cofi.respawnablecrystals.logic.CrystalManager;

import java.util.Objects;

public final class RespawnableCrystals extends JavaPlugin {

    private static final ChatColor PLUGIN_NAME_COLOR = ChatColor.AQUA;

    private ConfigManager configManager;
    private CrystalManager crystalManager;

    @Override
    public void onEnable() {
        // save the config file from the jar into the server folder, in case it doesn't exist yet
        saveDefaultConfig();

        // init fields
        configManager = new ConfigManager(this);
        crystalManager = new CrystalManager(this);

        // register listeners
        getServer().getPluginManager().registerEvents(new CrystalSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerTickListener(this), this);
        getServer().getPluginManager().registerEvents(new DragonPhaseListener(this), this);
        getServer().getPluginManager().registerEvents(new DragonDeathListener(this), this);

        // register commands
        Objects.requireNonNull(getServer().getPluginCommand("rscrystals")).setExecutor(new RSCrystalsCommand(this));

        // register tab completer
        Objects.requireNonNull(getServer().getPluginCommand("rscrystals")).setTabCompleter(new RSCrystalsTabCompleter(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }

    public void reload() {
        reloadConfig();
        crystalManager.reloadLocations();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CrystalManager getCrystalManager() {
        return crystalManager;
    }

    public String prefixMessage(String message) {
        message = "[%s%s%s] %s".formatted(PLUGIN_NAME_COLOR, getName(), ChatColor.RESET, message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
