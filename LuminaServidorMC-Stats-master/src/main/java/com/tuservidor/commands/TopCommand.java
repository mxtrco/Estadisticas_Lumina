package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.tuservidor.database.Database;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TopCommand implements CommandExecutor {
    private final Database database;

    public TopCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Uso: /top <players|teams> [cantidad]");
            return true;
        }

        // Determinar la cantidad (por defecto 5)
        int limit = 5;
        if (args.length >= 2) {
            try {
                limit = Integer.parseInt(args[1]);
                if (limit < 1) limit = 5;
                if (limit > 50) limit = 50; // Máximo 50 para evitar spam
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Cantidad inválida. Usando 5 por defecto.");
            }
        }

        // Manejar las subcomandos
        switch (args[0].toLowerCase()) {
            case "players":
                showTopPlayers(sender, limit);
                break;
            case "teams":
                showTopTeams(sender, limit);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Uso: /top <players|teams> [cantidad]");
        }

        return true;
    }

    // Función para mostrar los mejores jugadores
    public void showTopPlayers(CommandSender sender, int limit) {
        try {
            List<Map<String, Object>> topPlayers = database.getTopPlayers(limit);
            sender.sendMessage(ChatColor.GREEN + "Top Players:");
            for (Map<String, Object> player : topPlayers) {
                sender.sendMessage(ChatColor.YELLOW + "Player: " + player.get("player_name") + ", Points: " + player.get("points"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Error fetching top players.");
        }
    }

    // Función para mostrar los mejores equipos
    public void showTopTeams(CommandSender sender, int limit) {
        try {
            List<Map<String, Object>> topTeams = database.getTopTeams(limit);
            sender.sendMessage(ChatColor.GREEN + "Top Teams:");
            for (Map<String, Object> team : topTeams) {
                sender.sendMessage(ChatColor.YELLOW + "Team ID: " + team.get("team_id") + ", Points: " + team.get("points"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Error fetching top teams.");
        }
    }
}
