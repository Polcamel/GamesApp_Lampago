package com.example.gameapp_elai; // Siguroha nga sakto ang imong package name

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp_elai.R;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private int p1Score = 0, p2Score = 0, p3Score = 0;
    private int lives = 3;
    private int activePlayers = 2;
    private int currentPlayer = 1;
    private int roundCount = 0;

    private TextView scoreView, lifeView;
    private Stack<int[]> moveHistory = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreView = findViewById(R.id.scoreView);
        lifeView = findViewById(R.id.lifeView);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        RadioGroup playerGroup = findViewById(R.id.playerCountGroup);

        // Dinamiko nga paghimo sa mga buttons para mapuno ang grid
        gridLayout.post(() -> {
            int width = gridLayout.getWidth() / 3;
            int height = gridLayout.getHeight() / 3;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j] = new Button(this);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = width - 10;
                    params.height = height - 10;
                    params.setMargins(5, 5, 5, 5);
                    buttons[i][j].setLayoutParams(params);

                    buttons[i][j].setTextSize(30);
                    buttons[i][j].setBackgroundColor(Color.parseColor("#FFF1F1"));
                    buttons[i][j].setTextColor(Color.parseColor("#AD1457"));

                    final int r = i;
                    final int c = j;
                    buttons[i][j].setOnClickListener(v -> onCellClick(buttons[r][c], r, c));
                    gridLayout.addView(buttons[i][j]);
                }
            }
        });

        playerGroup.setOnCheckedChangeListener((group, checkedId) -> {
            activePlayers = (checkedId == R.id.radio3) ? 3 : 2;
            resetGame();
        });

        findViewById(R.id.undoBtn).setOnClickListener(v -> useLifeSaver());
        findViewById(R.id.resetBtn).setOnClickListener(v -> resetGame());
    }

    private void onCellClick(Button b, int r, int c) {
        if (!b.getText().toString().equals("")) return;

        moveHistory.push(new int[]{r, c});

        if (activePlayers == 2) {
            b.setText(currentPlayer == 1 ? "X" : "O");
        } else {
            if (currentPlayer == 1) b.setText("X");
            else if (currentPlayer == 2) b.setText("O");
            else b.setText("∆");
        }

        roundCount++;
        if (checkForWin()) {
            playerWins();
        } else if (roundCount == 9) {
            Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
            resetBoard();
        } else {
            currentPlayer = (currentPlayer % activePlayers) + 1;
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) field[i][j] = buttons[i][j].getText().toString();
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) return true;
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) return true;
        }
        return (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) ||
                (field[0][2].equals(field[1][1]) && field[1][1].equals(field[2][0]) && !field[0][2].equals(""));
    }

    private void playerWins() {
        if (currentPlayer == 1) p1Score++;
        else if (currentPlayer == 2) p2Score++;
        else p3Score++;

        updateScore();
        Toast.makeText(this, "Player " + currentPlayer + " Wins!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void useLifeSaver() {
        if (lives > 0 && !moveHistory.isEmpty()) {
            int[] lastMove = moveHistory.pop();
            buttons[lastMove[0]][lastMove[1]].setText("");
            roundCount--;
            lives--;
            lifeView.setText("Life Savers: " + lives);
            currentPlayer = (currentPlayer == 1) ? activePlayers : currentPlayer - 1;
        } else {
            Toast.makeText(this, "No life savers left!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateScore() {
        scoreView.setText("P1: " + p1Score + "  |  P2: " + p2Score + "  |  P3: " + p3Score);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) buttons[i][j].setText("");
        }
        roundCount = 0;
        currentPlayer = 1;
        moveHistory.clear();
    }

    private void resetGame() {
        p1Score = 0; p2Score = 0; p3Score = 0;
        lives = 3;
        lifeView.setText("Life Savers: 3");
        updateScore();
        resetBoard();
    }
}