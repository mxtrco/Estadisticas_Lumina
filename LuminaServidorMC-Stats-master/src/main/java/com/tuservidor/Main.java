package com.tuservidor;

import com.tuservidor.commands.*;
import com.tuservidor.listeners.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.tuservidor.database.Database;
import org.bukkit.ChatColor;

public class Main extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {
        // Inicializar base de datos
        database = new Database(getDataFolder() + "/stats.db");
        database.connect();
        new Stats(this, database);

        // Registrar comandos
        getCommand("team").setExecutor(new TeamCommand(database));
        getCommand("top").setExecutor(new TopCommand(database));
        getCommand("winner").setExecutor(new WinnerCommand(database));
        getCommand("ip").setExecutor(new IpCommand(database));
        getCommand("puntos").setExecutor(new PuntosCommand(database));
        getCommand("change").setExecutor(new CambiarCommand(database));
        getCommand("add").setExecutor(new CambiarCommand(database));
        getLogger().info("Plugin de estadísticas activado!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin de estadísticas desactivado!");
    }
}