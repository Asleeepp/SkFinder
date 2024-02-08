package me.asleepp.skfinder;


import me.asleepp.skfinder.commands.SkFinderCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

public final class SkFinder extends JavaPlugin {

    public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "SkFinder" + ChatColor.GRAY + "] " + ChatColor.RESET;

    private static int resultsPerPage;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig(); // Save the default config.yml if it doesn't exist
        loadConfiguration(); // Load configuration values

        getCommand("skfind").setExecutor(new SkFinderCommand());
        Plugin Skript = Bukkit.getPluginManager().getPlugin("Skript");
        if (Skript != null && Skript.isEnabled()) {
            getLogger().info("Enabled!");
        } else {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().warning("Skript wasn't found, disabling.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfiguration() {
        // Retrieve results per page from configuration
        FileConfiguration config = getConfig();
        resultsPerPage = config.getInt("results_per_page", 5);
    }

    public static int getResultsPerPage() {
        return resultsPerPage;
    }

    public void reloadConfiguration() {
        reloadConfig();
        loadConfiguration();
    }


    public static SkFinder getInstance() {
        return SkFinder.getPlugin(SkFinder.class);
    }
}
