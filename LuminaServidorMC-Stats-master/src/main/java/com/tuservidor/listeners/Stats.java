package com.tuservidor.listeners;

import com.tuservidor.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Statistic;
import com.tuservidor.database.Database;

public class Stats implements Listener {
    private final JavaPlugin plugin;
    private final Database database;

    public Stats(JavaPlugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;  // Ahora se inicializa correctamente.
        // Registrar el listener de eventos
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        String playerIp = event.getAddress().getHostAddress();

        // Obtén la IP del jugador desde la base de datos
        String playerIpDatabase = database.getPlayerIp(playerName);

        // Verifica si la IP registrada coincide con la IP del jugador
        if (playerIpDatabase == null || !playerIpDatabase.equals(playerIp)) {
            // Rechaza la conexión si no coincide
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "Tu dirección IP no coincide con la registrada.");
            System.out.println("Conexión rechazada para " + playerName + ": IP no válida.");
        } else {
            System.out.println("Conexión permitida para " + playerName + ".");
        }
    }

    // Este método puede ser llamado cuando un jugador se une al servidor
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Mostrar mensaje de bienvenida
        Bukkit.getLogger().info(player.getName() + " se ha unido al servidor.");

        // Estadísticas a mostrar
        int animalsBred = player.getStatistic(Statistic.ANIMALS_BRED);
        int tradesWithVillagers = player.getStatistic(Statistic.TRADED_WITH_VILLAGER);
        int damageAbsorbed = player.getStatistic(Statistic.DAMAGE_ABSORBED);
        int damageBlockedByShield = player.getStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD);
        int damageDealt = player.getStatistic(Statistic.DAMAGE_DEALT);
        int damageDealtAbsorbed = player.getStatistic(Statistic.DAMAGE_DEALT_ABSORBED);
        int damageDealtResisted = player.getStatistic(Statistic.DAMAGE_DEALT_RESISTED);
        int damageReceived = player.getStatistic(Statistic.DAMAGE_TAKEN);
        int damageResisted = player.getStatistic(Statistic.DAMAGE_RESISTED);
        int horseDistance = player.getStatistic(Statistic.HORSE_ONE_CM);
        int crouchDistance = player.getStatistic(Statistic.CROUCH_ONE_CM);
        int distanceWalked = player.getStatistic(Statistic.WALK_ONE_CM);
        int distanceFallen = player.getStatistic(Statistic.FALL_ONE_CM);
        int distanceSprinting = player.getStatistic(Statistic.SPRINT_ONE_CM);
        int boatDistance = player.getStatistic(Statistic.BOAT_ONE_CM);
        int climbingDistance = player.getStatistic(Statistic.CLIMB_ONE_CM);
        int swimmingDistance = player.getStatistic(Statistic.SWIM_ONE_CM);
        int raidsStarted = player.getStatistic(Statistic.RAID_TRIGGER);
        int raidsWon = player.getStatistic(Statistic.RAID_WIN);
        int mobsKilled = player.getStatistic(Statistic.MOB_KILLS);
        int itemsEnchanted = player.getStatistic(Statistic.ITEM_ENCHANTED);
        int fishCaught = player.getStatistic(Statistic.FISH_CAUGHT);
        int underwaterDistance = player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM);
        int surfaceDistance = player.getStatistic(Statistic.WALK_ON_WATER_ONE_CM);
        int jumps = player.getStatistic(Statistic.JUMP);
        int crouchTime = player.getStatistic(Statistic.SNEAK_TIME);
        int timeSinceLastSleep = player.getStatistic(Statistic.TIME_SINCE_REST);
        int timePlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        int timesSlept = player.getStatistic(Statistic.SLEEP_IN_BED);

        int puntos_extra = database.getExtraPoints(player.getName());

        // Definir los pesos para cada estadística
        int animalBredWeight = 2;
        int tradesWeight = 3;
        int damageAbsorbedWeight = 4;
        int damageBlockedByShieldWeight = 4;
        int damageDealtWeight = 5;
        int damageDealtAbsorbedWeight = 3;
        int damageDealtResistedWeight = 3;
        int damageReceivedWeight = 2;
        int damageResistedWeight = 2;
        int horseDistanceWeight = 1;
        int distanceWalkedWeight = 1;
        int distanceSprintingWeight = 1;
        int distanceFallenWeight = 1;
        int mobsKilledWeight = 4;
        int itemsEnchantedWeight = 2;
        int fishCaughtWeight = 1;
        int underwaterDistanceWeight = 1;
        int surfaceDistanceWeight = 1;
        int jumpsWeight = 2;
        int crouchTimeWeight = 1;
        int timePlayedWeight = 3;
        int timesSleptWeight = 1;

        // Calcular los puntos con la combinación lineal según prioridades
        int points =
                (animalBredWeight * animalsBred) +
                        (tradesWeight * tradesWithVillagers) +
                        (damageAbsorbedWeight * damageAbsorbed) +
                        (damageBlockedByShieldWeight * damageBlockedByShield) +
                        (damageDealtWeight * damageDealt) +
                        (damageDealtAbsorbedWeight * damageDealtAbsorbed) +
                        (damageDealtResistedWeight * damageDealtResisted) +
                        (damageReceivedWeight * damageReceived) +
                        (damageResistedWeight * damageResisted) +
                        (horseDistanceWeight * horseDistance) +
                        (distanceWalkedWeight * distanceWalked) +
                        (distanceSprintingWeight * distanceSprinting) +
                        (distanceFallenWeight * distanceFallen) +
                        (mobsKilledWeight * mobsKilled) +
                        (itemsEnchantedWeight * itemsEnchanted) +
                        (fishCaughtWeight * fishCaught) +
                        (underwaterDistanceWeight * underwaterDistance) +
                        (surfaceDistanceWeight * surfaceDistance) +
                        (jumpsWeight * jumps) +
                        (crouchTimeWeight * crouchTime) +
                        (timePlayedWeight * timePlayed) +
                        (timesSleptWeight * timesSlept) +
                        puntos_extra;

        // Actualizar estadísticas en la base de datos
        database.updatePlayerStats(player.getUniqueId().toString(),
                player.getName(),
                player.getAddress().getAddress().getHostAddress(),
                points,
                animalsBred,
                tradesWithVillagers,
                damageAbsorbed,
                damageBlockedByShield,
                damageDealt,
                damageDealtAbsorbed,
                damageDealtResisted,
                damageReceived,
                damageResisted,
                horseDistance,
                crouchDistance,
                distanceWalked,
                distanceFallen,
                distanceSprinting,
                boatDistance,
                climbingDistance,
                swimmingDistance,
                raidsStarted,
                raidsWon,
                mobsKilled,
                itemsEnchanted,
                fishCaught,
                underwaterDistance,
                surfaceDistance,
                jumps,
                crouchTime,
                timeSinceLastSleep,
                timePlayed,
                timesSlept,
                puntos_extra);
        database.updatePlayerTeamStats(player.getName());
    }
}
