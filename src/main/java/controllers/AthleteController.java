package controllers;

import database.databaseDriver;
import org.apache.commons.lang.StringUtils;
import org.javatuples.Triplet;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Map;

public class AthleteController {
    private static void createAthletesTable(String connectionString) throws SQLException {
        Connection connection = DriverManager.getConnection(connectionString);
        if (!databaseDriver.doesTableExists(connection, "team")) {
            connection.close();
            throw new SQLException("Related table TEAM does not exist");
        }
        String[] columns = new String[] {
                "athlete_ID INT NOT NULL",
                "athlete_position CHAR(1) NOT NULL",
                "athlete_name CHAR(25) NOT NULL",
                "athlete_team CHAR(50) NOT NULL",
                "athlete_fantateamID INT"};
        String[] constraints = new String[] {"PRIMARY KEY (athlete_ID), FOREIGN KEY (athlete_fantateam) REFERENCES TEAM (team_ID)"};
        database.databaseDriver.createTable(connectionString, "athlete", columns, constraints);
    }

    public static void addAthletesToDb(String connectionString, @NotNull Map<Integer, Triplet<String, String, String>> extractedPlayers) {
        StringBuilder query = new StringBuilder("INSERT INTO ATHLETE (athlete_id, athlete_position, athlete_name, athlete_team) VALUES (");

        try (Connection connection = DriverManager.getConnection(connectionString);
            Statement statement = connection.createStatement()) {
            createAthletesTable(connectionString);
            if (!databaseDriver.isTableEmpty(statement, "athlete")) throw new SQLException("ATHLETE table isn't empty");
            for (Integer id : extractedPlayers.keySet()) {
                query.append(id).append(", ");
                Triplet<String, String, String> value = extractedPlayers.getOrDefault(id, null);
                query.append(value.getValue(0)).append(", ").append(value.getValue(1)).append(", ").append(value.getValue(2)).append("),");
            }
            final String sql = StringUtils.chop(query.toString()) + ";";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
