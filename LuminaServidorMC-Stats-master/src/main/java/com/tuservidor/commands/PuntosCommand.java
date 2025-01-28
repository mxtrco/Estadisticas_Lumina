package com.tuservidor.commands;

import com.tuservidor.database.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PuntosCommand implements CommandExecutor {
    private final Database database;

    public PuntosCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println("Comando ejecutado por: " + sender.getName()); // Log del ejecutante del comando

        if (!sender.hasPermission("tuservidor.ip")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            System.out.println("Permiso denegado para el usuario: " + sender.getName()); // Log de permiso denegado
            return true;
        }

        // Verificar que se haya ingresado el tipo de operación (add o remove) y los demás argumentos
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Uso correcto: /puntos <add|remove> <nombre_jugador> <cantidad>");
            System.out.println("Error en argumentos, se esperaban 3 pero se recibieron " + args.length); // Log de error en los argumentos
            return true;
        }

        String action = args[0].toLowerCase();
        String playerName = args[1];
        int points;

        // Intentar convertir la cantidad de puntos a entero
        try {
            points = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "La cantidad de puntos debe ser un número.");
            System.out.println("Error al convertir puntos a entero: " + args[2]); // Log de error al convertir puntos
            return true;
        }

        // Obtener la IP del jugador desde la base de datos usando la función de Database
        String playerIp = database.getPlayerIp(playerName);
        System.out.println("IP del jugador " + playerName + ": " + playerIp); // Log de IP del jugador

        if (playerIp != null) {
            // Usar switch para realizar la acción según el tipo (añadir o quitar puntos)
            switch (action) {
                case "add":
                    System.out.println("Añadiendo " + points + " puntos a " + playerName); // Log de acción
                    database.addPlayerPoints(playerName, points);
                    sender.sendMessage(ChatColor.GREEN + "A " + playerName + " se le han sumado " + points + " puntos.");
                    break;

                case "remove":
                    System.out.println("Quitando " + points + " puntos a " + playerName); // Log de acción
                    database.removePlayerPoints(playerName, points); // Necesitas implementar este método en Database
                    sender.sendMessage(ChatColor.GREEN + "A " + playerName + " se le han quitado " + points + " puntos.");
                    break;

                default:
                    sender.sendMessage(ChatColor.RED + "Acción desconocida. Usa 'add' para añadir puntos o 'remove' para quitar puntos.");
                    System.out.println("Acción desconocida: " + action); // Log de acción desconocida
                    return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "No se encontró al jugador: " + playerName);
            System.out.println("Jugador no encontrado: " + playerName); // Log de jugador no encontrado
        }

        return true;
    }
}
