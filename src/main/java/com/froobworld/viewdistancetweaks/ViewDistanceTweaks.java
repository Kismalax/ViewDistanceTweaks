package com.froobworld.viewdistancetweaks;

import com.froobworld.viewdistancetweaks.command.VdtCommand;
import com.froobworld.viewdistancetweaks.config.VdtConfig;
import com.froobworld.viewdistancetweaks.util.*;
import com.froobworld.viewdistancetweaks.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ViewDistanceTweaks extends JavaPlugin {
    private VdtConfig vdtConfig;
    private HookManager hookManager;
    private TaskManager taskManager;

    @Override
    public void onEnable() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception ex) {
            getLogger().severe("ViewDistanceTweaks requires Spigot (or a fork such as Paper) in order to run.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        vdtConfig = new VdtConfig(this);
        try {
            vdtConfig.load();
        } catch (Exception e) {
            getLogger().severe("Exception while loading configuration:");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (!vdtConfig.enabled.get()) {
            getLogger().warning("ViewDistanceTweaks must be configured before it can be enabled. Edit the " +
                    "config.yml file in the plugin's data folder, setting the 'enabled' option to true when you are " +
                    "done, then reload or restart the server.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        ViewDistanceUtils.syncSpigotViewDistances();

        hookManager = new HookManager(this);
        hookManager.init();
        taskManager = new TaskManager(this);
        taskManager.init();
        registerCommands();
        initMetrics();

        getLogger().info("Finished startup.");
    }

    @Override
    public void onDisable() {}

    public void reload() throws Exception {
        vdtConfig.load();
        taskManager.reload();
    }
    
    public void apply() throws Exception {
        taskManager.reload();
    }

    private void registerCommands() {
        getCommand("vdt").setExecutor(new VdtCommand(this));
        getCommand("vdt").setPermission(VdtCommand.PERMISSON);
        getCommand("vdt").setTabCompleter(VdtCommand.tabCompleter);
    }

    private void initMetrics() {
        new Metrics(this, 6488);
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public VdtConfig getVdtConfig() {
        return vdtConfig;
    }

}
