package com.tuservidor.commands;

import com.tuservidor.database.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CambiarCommand implements CommandExecutor {
    private final Database database;

    public CambiarCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tuservidor.ip")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        // Verificar que tengamos suficientes argumentos (jugador e IP)
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso correcto: /" + label + " <nombre_jugador> <ip>");
            return true;
        }

        String playerName = args[0];
        String playerIP = args[1];

        // Usar el label del comando para determinar la acción
        if ("change".equalsIgnoreCase(label)) {
            if (database.updatePlayerIP(playerName, playerIP)) {
                sender.sendMessage(ChatColor.GREEN + "La IP del jugador " + playerName + " se actualizó correctamente.");
            } else {
                sender.sendMessage(ChatColor.RED + "No se encontró al jugador " + playerName + " en la base de datos.");
            }
        } else if ("add".equalsIgnoreCase(label)) {
            if (database.addPlayer(playerName, playerIP)) {
                sender.sendMessage(ChatColor.GREEN + "El jugador " + playerName + " se añadió correctamente.");
            } else {
                sender.sendMessage(ChatColor.RED + "Error al añadir al jugador " + playerName + ".");
            }
        }

        return true;
    }
}
