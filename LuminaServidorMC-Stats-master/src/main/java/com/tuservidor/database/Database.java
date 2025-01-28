package com.tuservidor.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.tuservidor.stats.ScoreType;

public class Database {
    private final String path;
    public Connection connection;

    public Database(String path) {
        this.path = path;
    }


    public void connect() {
        try {
            // Crear directorio si no existe
            File dataFolder = new File(path).getParentFile();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            createTables();
            System.out.println("Base de datos conectada en: " + path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener la IP de un jugador
    public String getPlayerIp(String playerName) {
        String playerIp = null;
        String query = "SELECT player_ip FROM players WHERE player_name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    playerIp = rs.getString("player_ip");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerIp;
    }

    private void createTables() {
        String players = """
            CREATE TABLE IF NOT EXISTS players (
                player_id TEXT PRIMARY KEY,
                player_name TEXT NOT NULL,
                player_ip TEXT NOT NULL,
                points INTEGER DEFAULT 0,
                animals_bred INTEGER DEFAULT 0,
                trades_with_villagers INTEGER DEFAULT 0,
                damage_absorbed INTEGER DEFAULT 0,
                damage_blocked_by_shield INTEGER DEFAULT 0,
                damage_dealt INTEGER DEFAULT 0,
                damage_dealt_absorbed INTEGER DEFAULT 0,
                damage_dealt_resisted INTEGER DEFAULT 0,
                damage_received INTEGER DEFAULT 0,
                damage_resisted INTEGER DEFAULT 0,
                horse_distance INTEGER DEFAULT 0,
                crouch_distance INTEGER DEFAULT 0,
                distance_walked INTEGER DEFAULT 0,
                distance_fallen INTEGER DEFAULT 0,
                distance_sprinting INTEGER DEFAULT 0,
                boat_distance INTEGER DEFAULT 0,
                climbing_distance INTEGER DEFAULT 0,
                swimming_distance INTEGER DEFAULT 0,
                raids_started INTEGER DEFAULT 0,
                raids_won INTEGER DEFAULT 0,
                mobs_killed INTEGER DEFAULT 0,
                items_enchanted INTEGER DEFAULT 0,
                fish_caught INTEGER DEFAULT 0,
                underwater_distance INTEGER DEFAULT 0,
                surface_distance INTEGER DEFAULT 0,
                jumps INTEGER DEFAULT 0,
                crouch_time INTEGER DEFAULT 0,
                time_since_last_sleep INTEGER DEFAULT 0,
                time_played INTEGER DEFAULT 0,
                times_slept INTEGER DEFAULT 0
            );
        """;

        String teams = """
            CREATE TABLE IF NOT EXISTS teams (
                team_id INTEGER PRIMARY KEY,
                points INTEGER DEFAULT 0
            );
            """;

        String playerTeams = """
            CREATE TABLE IF NOT EXISTS player_teams (
                player_name TEXT NOT NULL,
                team_id INTEGER NOT NULL,
                FOREIGN KEY(player_name) REFERENCES players(player_name),
                FOREIGN KEY(team_id) REFERENCES teams(team_id),
                UNIQUE(player_name)
            );
            """;

        try (var stmt = connection.createStatement()) {
            stmt.execute(players);
            stmt.execute(teams);
            stmt.execute(playerTeams);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public void updatePlayerStats(String playerId, String playerName, int kills, int deaths) {
//        String sql = """
//                INSERT OR REPLACE INTO players (player_id, player_name, kills, deaths)
//                VALUES (?, ?, ?, ?);
//                """;
//
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setString(1, playerId);
//            pstmt.setString(2, playerName);
//            pstmt.setInt(3, kills);
//            pstmt.setInt(4, deaths);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public int getPlayerKills(String playerId) {
        String sql = "SELECT kills FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("kills");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPlayerDeaths(String playerId) {
        String sql = "SELECT deaths FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("deaths");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getPlayerTeam(String playerId) {
        String sql = """
        SELECT t.team_name 
        FROM teams t 
        JOIN player_teams pt ON t.team_id = pt.team_id 
        WHERE pt.player_id = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("team_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Para bloques minados
    public int getPlayerBlocksMined(String playerId) {
        String sql = "SELECT blocks_mined FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("blocks_mined");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Para tiempo de juego
    public long getPlayerPlaytime(String playerId) {
        String sql = "SELECT playtime FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("playtime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Para crear equipo
    public void createTeam(Integer teamId) {
        String sql1 = "INSERT INTO teams (team_id) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql1)) {
            pstmt.setString(1, String.valueOf(teamId));
            pstmt.executeUpdate();

            System.out.println("Equipo creado exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Para unirse a un equipo
    public void addPlayerToTeam(Integer teamId, String playerNameToAdd) {
        String sqlCheckTeam = "SELECT team_id FROM teams WHERE team_id = ?";
        String sqlAddPlayer = "INSERT INTO player_teams (player_name, team_id) VALUES (?, ?)";

        try (PreparedStatement pstmtCheck = connection.prepareStatement(sqlCheckTeam)) {
            pstmtCheck.setInt(1, teamId);
            var rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                try (PreparedStatement pstmtAdd = connection.prepareStatement(sqlAddPlayer)) {
                    pstmtAdd.setString(1, playerNameToAdd);
                    pstmtAdd.setInt(2, teamId);
                    pstmtAdd.executeUpdate();
                    System.out.println("Jugador agregado exitosamente al equipo.");
                }
            } else {
                System.out.println("El equipo con el ID especificado no existe.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Para obtener info del equipo
    public String getTeamInfo(String playerId) {
        String sql = """
        SELECT t.team_name, t.score,
               (SELECT GROUP_CONCAT(p2.player_name)
                FROM players p2
                JOIN player_teams pt2 ON p2.player_id = pt2.player_id
                WHERE pt2.team_id = t.team_id) as member_names
        FROM teams t
        JOIN player_teams pt ON t.team_id = pt.team_id
        WHERE pt.player_id = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return String.format("""
                %sEquipo: %s
                %sPuntos: %d
                %sMiembros: %s""",
                        ChatColor.GOLD,
                        rs.getString("team_name"),
                        ChatColor.YELLOW,
                        rs.getInt("score"),
                        ChatColor.YELLOW,
                        rs.getString("member_names").replace(",", ", "));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ChatColor.RED + "No estás en ningún equipo";
    }

    public void updatePlaytime(String playerId) {
        String sql = "UPDATE players SET playtime = playtime + 1 WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public void setTeamRole(String playerId, String teamName, TeamRole role) {
//        String sql = "UPDATE player_teams SET role = ? WHERE player_id = ? AND team_id = (SELECT team_id FROM teams WHERE team_name = ?)";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setString(1, role.name());
//            pstmt.setString(2, playerId);
//            pstmt.setString(3, teamName);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public void invitePlayer(String teamName, String playerId) {
        String sql = "INSERT INTO team_invites (team_id, player_id) VALUES ((SELECT team_id FROM teams WHERE team_name = ?), ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            pstmt.setString(2, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTeamMembers(String teamName) {
        List<String> members = new ArrayList<>();
        String sql = """
        SELECT p.player_id
        FROM players p
        JOIN player_teams pt ON p.player_id = pt.player_id
        JOIN teams t ON pt.team_id = t.team_id
        WHERE t.team_name = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(rs.getString("player_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public int getPlayerPoints(String playerId) {
        String sql = "SELECT points FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void logScore(String playerId, ScoreType type, double amount, double points) {
        System.out.println(String.format("[EstadisticasServer] Puntos actualizados - Jugador: %s, Tipo: %s, Cantidad: %.2f, Puntos: %.2f",
                playerId, type, amount, points));
    }

    public void updateScore(String playerId, ScoreType type, double amount) {
        // Para debugging
        System.out.println("[DEBUG] Actualizando puntos: tipo=" + type + ", cantidad=" + amount);

        try {
            String sql = "UPDATE players SET blocks_mined = blocks_mined + ?, points = points + ? WHERE player_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                double points = amount * type.getWeight();
                pstmt.setDouble(1, amount);
                pstmt.setDouble(2, points);
                pstmt.setString(3, playerId);
                pstmt.executeUpdate();
                System.out.println("[DEBUG] Puntos actualizados: jugador=" + playerId + ", puntos=" + points);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getColumnName(ScoreType type) {
        return switch (type) {
            case KILL -> "kills";
            case DEATH -> "deaths";
            case BLOCK_MINED -> "blocks_mined";
            case DISTANCE_WALKED -> "distance_walked";
            case DISTANCE_SPRINTED -> "distance_sprinted";
            case DISTANCE_SWUM -> "distance_swum";
            case DISTANCE_BOAT -> "distance_boat";
            case DISTANCE_HORSE -> "distance_horse";
            case RAIDS_WON -> "raids_won";
            case DAMAGE_DEALT -> "damage_dealt";
            case VILLAGER_TRADES -> "villager_trades";
            case ITEMS_ENCHANTED -> "items_enchanted";
            case BLOCKS_PLACED -> "blocks_placed";
            case WEAPONS_CRAFTED -> "weapons_crafted";
            case TOOLS_CRAFTED -> "tools_crafted";
            case HOSTILE_KILLS -> "hostile_kills";
            case ANIMALS_BRED -> "animals_bred";
            case PLAYTIME -> "playtime";
            default -> type.name().toLowerCase();
        };
    }

    // Modificar el método updateTeamScore para que sume todos los tipos de puntos
    private void updateTeamScore(String playerId, double points) {
        String sql = """
        UPDATE teams 
        SET score = score + ? 
        WHERE team_id IN (SELECT team_id FROM player_teams WHERE player_id = ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, points);
            pstmt.setString(2, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void leaveTeam(String playerId) {
        String sql = "DELETE FROM player_teams WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isTeamOwner(String playerId) {
        String sql = """
            SELECT t.team_id 
            FROM teams t 
            JOIN player_teams pt ON t.team_id = pt.team_id 
            WHERE pt.player_id = ? AND pt.role = 'LEADER'
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteTeam(String playerId) {
        String teamId = getPlayerTeamId(playerId);
        if (teamId != null) {
            String sql1 = "DELETE FROM player_teams WHERE team_id = ?";
            String sql2 = "DELETE FROM teams WHERE team_id = ?";
            try (PreparedStatement pstmt1 = connection.prepareStatement(sql1);
                 PreparedStatement pstmt2 = connection.prepareStatement(sql2)) {

                pstmt1.setString(1, teamId);
                pstmt1.executeUpdate();

                pstmt2.setString(1, teamId);
                pstmt2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPlayerTeamId(String playerId) {
        String sql = "SELECT team_id FROM player_teams WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("team_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getWinningTeam() {
        String sql = """
        SELECT t.team_id,
               t.points,
               GROUP_CONCAT(pt.player_name) as players  -- Lista de jugadores
        FROM teams t
        JOIN player_teams pt ON t.team_id = pt.team_id
        JOIN players p ON pt.player_name = p.player_name
        GROUP BY t.team_id
        ORDER BY t.points DESC
        LIMIT 1
    """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Map<String, Object> winner = new HashMap<>();
                winner.put("team_id", rs.getInt("team_id"));
                winner.put("points", rs.getInt("points"));
                winner.put("players", rs.getString("players"));  // Lista de jugadores
                return winner;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasTeam(String playerId) {
        String sql = "SELECT 1 FROM player_teams WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasTeams() {
        String sql = "SELECT 1 FROM teams LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean teamExists(Integer teamId) {
        String sql = "SELECT 1 FROM teams WHERE team_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(teamId));
            var rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTeamMemberCount(String teamName) {
        String sql = """
            SELECT COUNT(*) as count 
            FROM player_teams pt 
            JOIN teams t ON pt.team_id = t.team_id 
            WHERE t.team_name = ?
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteTeamById(Integer teamId) {
        String sql1 = "DELETE FROM player_teams WHERE team_id IN (SELECT team_id FROM teams WHERE team_id = ?)";
        String sql2 = "DELETE FROM teams WHERE team_id = ?";
        try (PreparedStatement pstmt1 = connection.prepareStatement(sql1);
             PreparedStatement pstmt2 = connection.prepareStatement(sql2)) {

            pstmt1.setString(1, String.valueOf(teamId));
            pstmt1.executeUpdate();

            pstmt2.setString(1, String.valueOf(teamId));
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerStats(String playerName, boolean isAdmin) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM players WHERE player_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                sb.append("📊 **Estadísticas de ").append(playerName).append("**\n")
                        .append("Puntos: ").append(rs.getInt("points")).append("\n")
                        .append("Kills: ").append(rs.getInt("kills")).append("\n")
                        .append("Muertes: ").append(rs.getInt("deaths")).append("\n")
                        .append("Bloques minados: ").append(rs.getInt("blocks_mined")).append("\n")
                        .append("Tiempo jugado: ").append(rs.getInt("playtime")).append(" minutos\n")
                        .append("Distancia caminada: ").append(String.format("%.2f", rs.getDouble("distance_walked"))).append(" bloques\n")
                        .append("Distancia corriendo: ").append(String.format("%.2f", rs.getDouble("distance_sprinted"))).append(" bloques\n")
                        .append("Distancia nadando: ").append(String.format("%.2f", rs.getDouble("distance_swum"))).append(" bloques\n")
                        .append("Distancia en bote: ").append(String.format("%.2f", rs.getDouble("distance_boat"))).append(" bloques\n")
                        .append("Distancia a caballo: ").append(String.format("%.2f", rs.getDouble("distance_horse"))).append(" bloques\n")
                        .append("Saltos: ").append(rs.getInt("jumps")).append("\n")
                        .append("Invasiones ganadas: ").append(rs.getInt("raids_won")).append("\n")
                        .append("Daño causado: ").append(String.format("%.1f", rs.getDouble("damage_dealt"))).append("\n")
                        .append("Daño recibido: ").append(String.format("%.1f", rs.getDouble("damage_taken"))).append("\n")
                        .append("Comercios con aldeanos: ").append(rs.getInt("villager_trades")).append("\n")
                        .append("Objetos encantados: ").append(rs.getInt("items_enchanted")).append("\n")
                        .append("Usos de mesa de crafteo: ").append(rs.getInt("crafting_used")).append("\n")
                        .append("Bloques colocados: ").append(rs.getInt("blocks_placed")).append("\n")
                        .append("Armas crafteadas: ").append(rs.getInt("weapons_crafted")).append("\n")
                        .append("Herramientas crafteadas: ").append(rs.getInt("tools_crafted")).append("\n")
                        .append("Comida consumida: ").append(rs.getInt("food_eaten")).append("\n")
                        .append("Pociones usadas: ").append(rs.getInt("potions_used")).append("\n")
                        .append("Mobs hostiles eliminados: ").append(rs.getInt("hostile_kills")).append("\n")
                        .append("Entidades montadas: ").append(rs.getInt("entities_mounted")).append("\n")
                        .append("Bloques cherry usados: ").append(rs.getInt("cherry_blocks_used")).append("\n")
                        .append("Distancia con armadura trimmed: ").append(String.format("%.2f", rs.getDouble("trimmed_armor_distance"))).append(" bloques\n");

                // Solo mostrar IP si el que consulta es admin
                if (isAdmin) {
                    sb.append("\n🔒 **Info Admin**\n")
                            .append("IP: ").append(rs.getString("last_ip")).append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public int getPointsGainedLastMinute(String playerId) {
        String sql = "SELECT points FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Verificar si un jugador está en un equipo
    public boolean isPlayerInTeam(Integer teamId, String playerNameToCheck) {
        String sqlCheckPlayer = "SELECT 1 FROM player_teams WHERE team_id = ? AND player_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCheckPlayer)) {
            pstmt.setInt(1, teamId);
            pstmt.setString(2, playerNameToCheck);
            var rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Eliminar un jugador de un equipo
    public void removePlayerFromTeam(Integer teamId, String playerNameToRemove) {
        if (isPlayerInTeam(teamId, playerNameToRemove)) {
            String sqlRemovePlayer = "DELETE FROM player_teams WHERE team_id = ? AND player_name = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sqlRemovePlayer)) {
                pstmt.setInt(1, teamId);
                pstmt.setString(2, playerNameToRemove);
                pstmt.executeUpdate();
                System.out.println("Jugador eliminado exitosamente del equipo.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El jugador especificado no está en el equipo.");
        }
    }


    public boolean isPlayerInAnyTeam(String playerNameToAdd) {
        String sql = "SELECT 1 FROM player_teams WHERE player_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerNameToAdd);
            var rs = pstmt.executeQuery();
            return rs.next(); // Si hay resultados, significa que el jugador ya pertenece a un equipo
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Si ocurre un error, asumimos que el jugador no está en ningún equipo
        }
    }

    public void updatePlayerStats(String playerId, String playerName, String playerIp, int points, int animalsBred, int tradesWithVillagers, int damageAbsorbed, int damageBlockedByShield, int damageDealt, int damageDealtAbsorbed, int damageDealtResisted, int damageReceived, int damageResisted, int horseDistance, int crouchDistance, int distanceWalked, int distanceFallen, int distanceSprinting, int boatDistance, int climbingDistance, int swimmingDistance, int raidsStarted, int raidsWon, int mobsKilled, int itemsEnchanted, int fishCaught, int underwaterDistance, int surfaceDistance, int jumps, int crouchTime, int timeSinceLastSleep, int timePlayed, int timesSlept, int puntos_extra) {

        String sql = """
            INSERT OR REPLACE INTO players (
                player_id, player_name, player_ip, points, animals_bred, trades_with_villagers, 
                damage_absorbed, damage_blocked_by_shield, damage_dealt, damage_dealt_absorbed, 
                damage_dealt_resisted, damage_received, damage_resisted, horse_distance, crouch_distance, 
                distance_walked, distance_fallen, distance_sprinting, boat_distance, climbing_distance, 
                swimming_distance, raids_started, raids_won, mobs_killed, items_enchanted, fish_caught, 
                underwater_distance, surface_distance, jumps, crouch_time, time_since_last_sleep, 
                time_played, times_slept, puntos_extra)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            pstmt.setString(2, playerName);
            pstmt.setString(3, playerIp);
            pstmt.setInt(4, points);
            pstmt.setInt(5, animalsBred);
            pstmt.setInt(6, tradesWithVillagers);
            pstmt.setInt(7, damageAbsorbed);
            pstmt.setInt(8, damageBlockedByShield);
            pstmt.setInt(9, damageDealt);
            pstmt.setInt(10, damageDealtAbsorbed);
            pstmt.setInt(11, damageDealtResisted);
            pstmt.setInt(12, damageReceived);
            pstmt.setInt(13, damageResisted);
            pstmt.setInt(14, horseDistance);
            pstmt.setInt(15, crouchDistance);
            pstmt.setInt(16, distanceWalked);
            pstmt.setInt(17, distanceFallen);
            pstmt.setInt(18, distanceSprinting);
            pstmt.setInt(19, boatDistance);
            pstmt.setInt(20, climbingDistance);
            pstmt.setInt(21, swimmingDistance);
            pstmt.setInt(22, raidsStarted);
            pstmt.setInt(23, raidsWon);
            pstmt.setInt(24, mobsKilled);
            pstmt.setInt(25, itemsEnchanted);
            pstmt.setInt(26, fishCaught);
            pstmt.setInt(27, underwaterDistance);
            pstmt.setInt(28, surfaceDistance);
            pstmt.setInt(29, jumps);
            pstmt.setInt(30, crouchTime);
            pstmt.setInt(31, timeSinceLastSleep);
            pstmt.setInt(32, timePlayed);
            pstmt.setInt(33, timesSlept);
            pstmt.setInt(34, puntos_extra);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updatePlayerTeamStats(String playerName) {
        if (connection == null) {
            Bukkit.getLogger().severe("La conexión a la base de datos no está inicializada.");
            return;
        }

        try {
            // 1. Obtener el ID del equipo del jugador
            final String GET_TEAM_ID_QUERY = "SELECT team_id FROM player_teams WHERE player_name = ?";
            int teamId = -1;

            try (PreparedStatement getTeamIdStmt = connection.prepareStatement(GET_TEAM_ID_QUERY)) {
                getTeamIdStmt.setString(1, playerName);
                try (ResultSet rs = getTeamIdStmt.executeQuery()) {
                    if (rs.next()) {
                        teamId = rs.getInt("team_id");
                    }
                }
            }

            if (teamId == -1) {
                Bukkit.getLogger().warning("El jugador " + playerName + " no pertenece a ningún equipo.");
                return;
            }

            // 2. Calcular los puntos totales del equipo
            final String CALCULATE_TEAM_POINTS_QUERY = """
            SELECT SUM(points) AS total_points
            FROM players
            INNER JOIN player_teams ON players.player_name = player_teams.player_name
            WHERE player_teams.team_id = ?
        """;
            int totalPoints = 0;

            try (PreparedStatement calculateTeamPointsStmt = connection.prepareStatement(CALCULATE_TEAM_POINTS_QUERY)) {
                calculateTeamPointsStmt.setInt(1, teamId);
                try (ResultSet rs = calculateTeamPointsStmt.executeQuery()) {
                    if (rs.next()) {
                        totalPoints = rs.getInt("total_points");
                    }
                }
            }

            // 3. Actualizar los puntos del equipo en la tabla "teams"
            final String UPDATE_TEAM_POINTS_QUERY = "UPDATE teams SET points = ? WHERE team_id = ?";

            try (PreparedStatement updateTeamPointsStmt = connection.prepareStatement(UPDATE_TEAM_POINTS_QUERY)) {
                updateTeamPointsStmt.setInt(1, totalPoints);
                updateTeamPointsStmt.setInt(2, teamId);
                updateTeamPointsStmt.executeUpdate();
            }

            Bukkit.getLogger().info("Actualizados los puntos del equipo " + teamId + " a " + totalPoints + " puntos.");

        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Error al actualizar los puntos del equipo para el jugador " + playerName);
        }
    }

    // Función para obtener los mejores jugadores
    public List<Map<String, Object>> getTopPlayers(int limit) throws SQLException {
        String query = """
        SELECT player_name, points
        FROM players
        ORDER BY points DESC
        LIMIT ?;
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> topPlayers = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> player = new HashMap<>();
                    player.put("player_name", rs.getString("player_name"));
                    player.put("points", rs.getInt("points"));
                    topPlayers.add(player);
                }
                return topPlayers;
            }
        }
    }

    // Función para obtener los mejores equipos
    public List<Map<String, Object>> getTopTeams(int limit) throws SQLException {
        String query = """
        SELECT t.team_id, t.points
        FROM teams t
        ORDER BY t.points DESC
        LIMIT ?;
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> topTeams = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> team = new HashMap<>();
                    team.put("team_id", rs.getInt("team_id"));
                    team.put("points", rs.getInt("points"));
                    topTeams.add(team);
                }
                return topTeams;
            }
        }
    }

    public void addPlayerPoints(String playerName, int points) {
        String sql = "UPDATE players SET puntos_extra = puntos_extra + ? WHERE player_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, points);
            pstmt.setString(2, playerName);
            pstmt.executeUpdate();
            System.out.println("Se han añadido " + points + " puntos a " + playerName); // Log en base de datos
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al añadir puntos a " + playerName); // Log de error en base de datos
        }
    }

    public void removePlayerPoints(String playerName, int points) {
        String sql = "UPDATE players SET puntos_extra = puntos_extra - ? WHERE player_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, points);
            pstmt.setString(2, playerName);
            pstmt.executeUpdate();
            System.out.println("Se han quitado " + points + " puntos de " + playerName); // Log en base de datos
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al quitar puntos a " + playerName); // Log de error en base de datos
        }
    }

    public int getExtraPoints(String playerName) {
        int extraPoints = 0; // Valor por defecto si no se encuentra ningún registro

        String sql = "SELECT puntos_extra FROM players WHERE player_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName); // Establece el nombre del jugador en la consulta SQL

            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta

            if (rs.next()) { // Si existe al menos un resultado
                extraPoints = rs.getInt("puntos_extra"); // Obtiene el valor de la columna puntos_extra
            } else {
                System.out.println("Jugador no encontrado: " + playerName); // Log si no se encuentra el jugador
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener puntos extra para el jugador: " + playerName); // Log de error
        }

        return extraPoints;
    }

    public boolean updatePlayerIP(String playerName, String playerIP) {
        String query = "UPDATE players SET player_ip = ? WHERE player_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerIP);
            statement.setString(2, playerName);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la IP del jugador: " + playerName);
            e.printStackTrace(); // Reemplazar con un logger en producción
            return false;
        }
    }

    public boolean addPlayer(String playerName, String playerIP) {
        String query = "INSERT INTO players (player_name, player_ip) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerName);
            statement.setString(2, playerIP);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al añadir al jugador: " + playerName);
            e.printStackTrace(); // Reemplazar con un logger en producción
            return false;
        }
    }

}