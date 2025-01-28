package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.tuservidor.database.Database;

public class TeamCommand implements CommandExecutor {
    private final Database database;

    public TeamCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("team.manage")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso: /team <create|delete|add|remove>");
            return true;
        }

        String action = args[0].toLowerCase();
        String teamIdInput = args[1];

        // Validar que teamId sea un número
        int teamId;
        try {
            teamId = Integer.parseInt(teamIdInput);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "El ID del equipo debe ser un número válido.");
            return true;
        }

        switch (action) {
            case "create":
                if (database.teamExists(Integer.valueOf(teamId))) {
                    sender.sendMessage(ChatColor.RED + "El equipo con ID " + teamId + " ya existe.");
                    return true;
                }
                database.createTeam(Integer.valueOf(teamId));
                sender.sendMessage(ChatColor.GREEN + "Equipo creado con ID: " + teamId);
                break;

            case "delete":
                if (!database.teamExists(Integer.valueOf(teamId))) {
                    sender.sendMessage(ChatColor.RED + "El equipo con ID " + teamId + " no existe.");
                    return true;
                }
                database.deleteTeamById(Integer.valueOf(teamId));
                sender.sendMessage(ChatColor.GREEN + "Equipo eliminado: " + teamId);
                break;

            case "add":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Uso: /team add <numero_equipo> <nombre_jugador>");
                    return true;
                }
                String playerNameToAdd = args[2];
                if (!database.teamExists(Integer.valueOf(teamId))) {
                    sender.sendMessage(ChatColor.RED + "El equipo con ID " + teamId + " no existe.");
                    return true;
                }
                // Verificar si el jugador ya pertenece a algún equipo
                if (database.isPlayerInAnyTeam(playerNameToAdd)) {
                    sender.sendMessage(ChatColor.RED + "El jugador " + playerNameToAdd + " ya pertenece a un equipo.");
                    return true;
                }
                // Agregar jugador al equipo
                database.addPlayerToTeam(Integer.valueOf(teamId), playerNameToAdd);
                sender.sendMessage(ChatColor.GREEN + "Jugador " + playerNameToAdd + " añadido al equipo " + teamId);
                break;

            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Uso: /team remove <numero_equipo> <nombre_jugador>");
                    return true;
                }
                String playerNameToRemove = args[2];
                if (!database.teamExists(Integer.valueOf(teamId))) {
                    sender.sendMessage(ChatColor.RED + "El equipo con ID " + teamId + " no existe.");
                    return true;
                }
                if (!database.isPlayerInTeam(Integer.valueOf(teamId), playerNameToRemove)) {
                    sender.sendMessage(ChatColor.RED + "El jugador " + playerNameToRemove + " no está en el equipo " + teamId);
                    return true;
                }
                database.removePlayerFromTeam(Integer.valueOf(teamId), playerNameToRemove);
                sender.sendMessage(ChatColor.GREEN + "Jugador " + playerNameToRemove + " eliminado del equipo " + teamId);
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Comando no reconocido. Uso: /team <create|delete|add|remove> <numero_equipo> [nombre_jugador]");
                break;
        }

        return true;
    }
}
