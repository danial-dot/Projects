import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Tictactoe {

    public static void main(String[] args) {
        new GameFrame();
    }
}

class GameFrame extends JFrame {
    private final JButton[] buttons = new JButton[9];
    private char currentPlayer = 'X';
    private boolean gameWon = false;
    private final JButton resetButton;
    private final JLabel scoreLabel;
    private Connection connection;
    private int xWins = 0;
    private int oWins = 0;
    private int round = 1;

    public GameFrame() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        initializeButtons(boardPanel);
        add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 20));
        resetButton.addActionListener(e -> resetGame());
        bottomPanel.add(resetButton);

        scoreLabel = new JLabel("Scores - X: 0, O: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        bottomPanel.add(scoreLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        connectToDatabase();
        loadScores();

        setVisible(true);
    }

    private void initializeButtons(JPanel panel) {
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 60));
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(new ButtonClickListener(i));
            panel.add(buttons[i]);
        }
    }

    private class ButtonClickListener implements ActionListener {
        private final int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameWon || !buttons[index].getText().equals("")) {
                return;
            }

            buttons[index].setText(String.valueOf(currentPlayer));

            if (checkWin()) {
                JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins round " + round + "!");
                updateScore(currentPlayer);
                round++;
                if (isBestOfThreeWinner()) {
                    JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins the game!");
                    resetBestOfThree();
                }
                resetBoard();
            } else if (isBoardFull()) {
                JOptionPane.showMessageDialog(null, "It's a draw!");
                resetBoard();
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            }
        }
    }

    private boolean checkWin() {
        int[][] winPatterns = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        for (int[] pattern : winPatterns) {
            if (buttons[pattern[0]].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[pattern[1]].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[pattern[2]].getText().equals(String.valueOf(currentPlayer))) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (JButton button : buttons) {
            if (button.getText().equals("")) {
                return false;
            }
        }
        return true;
    }

    private void resetBoard() {
        for (JButton button : buttons) {
            button.setText("");
        }
        currentPlayer = 'X';
        gameWon = false;
    }

    private void resetGame() {
        resetBoard();
        xWins = 0;
        oWins = 0;
        round = 1;
        updateScoreLabel();
    }

    private boolean isBestOfThreeWinner() {
        return xWins == 2 || oWins == 2;
    }

    private void resetBestOfThree() {
        xWins = 0;
        oWins = 0;
        round = 1;
        resetBoard();
        updateScoreLabel();
    }

    private void updateScore(char winner) {
        if (winner == 'X') {
            xWins++;
        } else {
            oWins++;
        }
        updateScoreLabel();
        saveScores();
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Scores  X: " + xWins + ", O: " + oWins);
    }

    private void connectToDatabase() {
        try {
            // Connect without specifying the database
            Connection initialConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "******");
            Statement statement = initialConnection.createStatement();
    
            // Create the database if it doesn't exist
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS tictactoe");
            statement.close();
            initialConnection.close();
    
            // Connect to the newly created database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tictactoe", "root", "12349876");
    
            // Create the scores table if it doesn't exist
            Statement createTableStatement = connection.createStatement();
            createTableStatement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS scores (xWins INTEGER DEFAULT 0, oWins INTEGER DEFAULT 0)"
            );
            createTableStatement.close();
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void loadScores() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM scores");
            if (resultSet.next()) {
                xWins = resultSet.getInt("xWins");
                oWins = resultSet.getInt("oWins");
                updateScoreLabel();
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO scores (xWins, oWins) VALUES (?, ?)");
                preparedStatement.setInt(1, 0);
                preparedStatement.setInt(2, 0);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveScores() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE scores SET xWins = ?, oWins = ?");
            preparedStatement.setInt(1, xWins);
            preparedStatement.setInt(2, oWins);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
