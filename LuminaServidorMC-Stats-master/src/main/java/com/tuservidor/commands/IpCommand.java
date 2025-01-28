package com.tuservidor.commands;

import com.tuservidor.database.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IpCommand implements CommandExecutor {
    private final Database database;

    public IpCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar si el jugador tiene el permiso adecuado
        if (!sender.hasPermission("tuservidor.ip")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return false;
        }

        // Verificar que se haya ingresado el nombre del jugador
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Uso correcto: /ip <nombre_jugador>");
            return false;
        }

        String playerName = args[0];

        // Obtener la IP del jugador desde la base de datos usando la función de Database
        String playerIp = database.getPlayerIp(playerName);

        if (playerIp != null) {
            sender.sendMessage(ChatColor.GREEN + "La IP de " + playerName + " es: " + playerIp);
        } else {
            sender.sendMessage(ChatColor.RED + "No se encontró al jugador: " + playerName);
        }

        return true;
    }
}