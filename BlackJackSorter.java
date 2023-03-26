import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This is a program for sorting BlackJack data

public class BlackJackSorter {
    public static void main(String[] args) {
        try {
            List<String> lines = readLinesFromFile("game_data_2.txt");
            Map<String, List<GameEvent>> gameSessions = parseGameSessions(lines);
            printGameSessions(gameSessions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // reads lines from file and returns them as a list of strings

    public static List<String> readLinesFromFile(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    // This parses game sessions from a list of lines and returns them as a map of session IDs to lists of GameEvents
    public static Map<String, List<GameEvent>> parseGameSessions(List<String> lines) {
        Map<String, List<GameEvent>> gameSessions = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(",");

            // make sure there are at least two parts (timestamp and session ID) and session ID is not empty
            if (parts.length >= 2 && !parts[1].trim().isEmpty()) {
                // create new GameEvent object from line and add it to the appropriate session ID's list
                GameEvent event = new GameEvent(line, parts[1].trim(), Integer.parseInt(parts[0].trim()));
                String gameSessionId = parts[1].trim();
                gameSessions.computeIfAbsent(gameSessionId, k -> new ArrayList<>()).add(event);
            }
        }
        return gameSessions;
    }

    // This is for sorting game sessions by session ID and timestamp, then prints them to a file
    public static void printGameSessions(Map<String, List<GameEvent>> gameSessions) {
        // create a list of Map.Entry objects from the gameSessions map (for sorting)
        List<Map.Entry<String, List<GameEvent>>> sortedSessions = new ArrayList<>(gameSessions.entrySet());
        // sort the list of sessions by session ID (which is the key of each entry)
        sortedSessions.sort(Comparator.comparingInt(entry -> Integer.parseInt(entry.getKey())));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("game_data_2.txt"))) {
            // for each session, sort the GameEvents by timestamp and print them to the file
            for (Map.Entry<String, List<GameEvent>> entry : sortedSessions) {
                List<GameEvent> sessionEvents = entry.getValue();
                sessionEvents.sort(Comparator.comparingInt(GameEvent::getTimestamp));
                for (GameEvent event : sessionEvents) {
                    writer.write(event.getLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class GameEvent {
        private String line;
        private String element;
        private int timestamp;

        // constructor for GameEvent class
        public GameEvent(String line, String element, int timestamp) {
            this.line = line;
            this.element = element;
            this.timestamp = timestamp;
        }

         // getter method for line field
        public String getLine() {
            return line;
        }

        // getter method for element field
        public String getElement() {
            return element;
        }

        // getter method for timestamp field
        public int getTimestamp() {
            return timestamp;
        }
    }
}
