import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameDataAnalyzer {
    public static void main(String[] args) {
        // Read game data from file and parse into array
        List<String[]> games = new ArrayList<>();
        try {
            File file = new File("game_data_1.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] gameSession = null;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String sessionId = fields[1];
                if (gameSession == null || gameSession.length < 2 || !gameSession[1].equals(sessionId)) {
                    gameSession = new String[] { line };
                    games.add(gameSession);
                } else {
                    gameSession = Arrays.copyOf(gameSession, gameSession.length + 1);
                    gameSession[gameSession.length - 1] = line;
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }
        
        
        String saved_playerHand = null;
        String saved_dealerHand = null;
        Boolean p_hit = false;
        Boolean d_hit = false;
        Boolean p_stand = false;
        Boolean d_show = false;
        String previous_turn = null;
        List<String> errors = new ArrayList<>();      
        
        // Analyze game data for rule violations
        // Rule violations are check by analyzing every line from the input file

        for (String[] gameSession: games){
            int playerHandValue = 0;
            int dealerHandValue = 0;
           
            for (int i = 0; i < gameSession.length; i++) {
                String turn = gameSession[i];
                String[] fields = turn.split(",");
                String action = fields[3];
                String playerHand = fields[5];
                String dealerHand = fields[4]; 
                String[] player_cards = null; 
                int prev_dealerHandValue = 0;   

                // If p_hit, d_hit, p_stand or d_show actions are detected, they are changed into True and the variables from the line they were detected on are saved.
                // The rule violation will be done on the reading of the next line, previously saved variables are then compared to the new line ones.
                // If a violation is detected, it is saved and p_hit, d_hit, p_stand or d_show is changed back into False.

                if (p_hit) {
                    if ((playerHand.length() - saved_playerHand.length()) != 3) {
                        errors.add(previous_turn);
                        p_hit = false;    
                    }      
                    p_hit = false;  
                }
                if (d_hit ) {    
                    for (String card : saved_dealerHand.split("-")) {
                        prev_dealerHandValue += getCardValue(card);
                    }
                    if ((dealerHand.length() - saved_dealerHand.length()) != 3 || prev_dealerHandValue > 17) {
                        errors.add(previous_turn);
                        d_hit = false;
                        prev_dealerHandValue = 0;
                    }
                    d_hit = false;
                }
                if (p_stand) {
                    for (String card : playerHand.split("-")) {
                        playerHandValue += getCardValue(card);
                    }if (playerHandValue > 21) {
                        errors.add(previous_turn);
                    } else if ((playerHand.length() == saved_playerHand.length()) && (dealerHand.length() == saved_dealerHand.length())) {
                        p_stand = false;
                    } else {
                        errors.add(previous_turn);
                        p_stand = false;   
                    }
                }
                if (d_show) {
                    if ((dealerHand.length() - saved_dealerHand.length()) != 1) {
                        errors.add(previous_turn);
                        d_show = false;   
                    }
                    d_show = false;
                }

                // Below is the creation of the p_hit, d_hit, p_stand and d_show variables.
                // There are also rule violation detectors for P Joined, D Redeal, P Win and P Lose. 
                // They do not need to be compared to the next line because you can see the action result on the current line.

                if (action.equals("P Joined") || action.equals("D Redeal")) {
                    player_cards = playerHand.trim().split("-");
                    if (player_cards[0].length() == 2 && player_cards[1].length() == 2) {
                        String[] dealerCards = dealerHand.trim().split("-");
                        if (dealerCards[0].length() == 2 && dealerCards[1].equals("?")) {
                            continue;
                        }
                    }
                    errors.add(turn);

                } else if (action.equals("P Hit")) {
                    p_hit = true;
                    saved_playerHand = playerHand;
                    previous_turn = turn;
                    break;
                    
                } else if (action.equals("D Hit")) {   
                    d_hit = true;
                    saved_dealerHand = dealerHand;
                    previous_turn = turn;
                    break;
                    
                } else if (action.equals("P Stand")) {
                    p_stand = true;
                    previous_turn = turn;
                    saved_playerHand = playerHand;
                    saved_dealerHand = dealerHand;
                    break;

                } else if (action.equals("D Show")) {
                    d_show = true;
                    previous_turn = turn;
                    saved_dealerHand = dealerHand;
                    break;

                } else if (action.equals("P Win") || action.equals("P Lose")) {
                    for (String card : playerHand.split("-")) {
                        playerHandValue += getCardValue(card);
                    }
                    for (String card : dealerHand.split("-")) {
                        dealerHandValue += getCardValue(card);
                    }
                    if (playerHandValue > 21) {
                        if (action.equals("P Win")) {
                            errors.add(turn);
                        }
                    } else if (dealerHandValue > 21) {
                        if (action.equals("P Lose")) {
                            errors.add(turn);
                        }
                    } else if (dealerHandValue < 18) { 
                        errors.add(turn);

                    } else if (playerHandValue >= dealerHandValue) {
                        if (action.equals("P Lose")) {
                            errors.add(turn);
                        }
                    } else {
                        if (action.equals("P Win")) {
                            errors.add(turn);
                        }
                    }
                    break;
                }
            }
        }
        System.out.println(errors);

        // This is the part for writing the found errors into ana analyzer file.

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("analyzer_results.txt"));
            String gameSession = null;
            for (String error : errors) {
                String[] parts = error.split(",");
                if (!parts[1].equals(gameSession)) {
                    gameSession = parts[1];
                    writer.write(error);
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException e) {
            // Handle the exception here
            e.printStackTrace();
        }  
        
    }
    
    // This is for getting the card values

    private static int getCardValue(String card) {
        try {
            if (card == null || card.length() < 2) {
                throw new IllegalArgumentException("Invalid card: " + card);
            }
            String value = card.substring(0, card.length() - 1);
            char rank = value.charAt(0);
            if (value.equals("?")) {
                return 0;
            }
            if (Character.isDigit(rank)) {
                return Integer.parseInt(value);
            } else {
                switch (rank) {
                    case 'A':
                    case 'a':
                        return 11;
                    case 'K':
                    case 'Q':
                    case 'J':
                    case 'k':
                    case 'q':
                    case 'j':
                        return 10;
                    default:
                        throw new IllegalArgumentException("Invalid card rank: " + rank);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid card: " + card + ", setting value to 0");
            return 0;
        }
    } 
}
