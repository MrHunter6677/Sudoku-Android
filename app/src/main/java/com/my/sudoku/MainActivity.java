package com.my.sudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    private static final int BORDER_COLOR = Color.parseColor("#c46817");
    private static final int BUTTON_COLOR = Color.parseColor("#ecf0f1");
    private static final int BUTTON_TEXT_COLOR = Color.parseColor("#34495e");
    private static final int BOARD_PADDING = 20; // Padding for grid
    private static final int NUMPAD_MARGIN = 12;

    Button[][] buttons;
    Button selected;
    int selectedRow;
    int selectedColumn;
    boolean game = false;
    boolean[][] keys;
    byte[][] board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        buttons = new Button[9][9];
        keys = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                keys[i][j] = false;
            }
        }
        int screenSize = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        int boardSize = screenSize - (screenSize % 3);
        initBoard(findViewById(R.id.main), boardSize);
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreBoardState();
    }
    public void initBoard(LinearLayout main, int boardSize) {
        boardSize = boardSize - (boardSize % 3);
        LinearLayout gameContainer = new LinearLayout(this);
        gameContainer.setOrientation(LinearLayout.VERTICAL);  // Vertical stacking
        gameContainer.setPadding(BOARD_PADDING, BOARD_PADDING, BOARD_PADDING, BOARD_PADDING);
        setBorder(gameContainer, BORDER_COLOR, BORDER_COLOR, BOARD_PADDING, 40);

        boardSize = boardSize - BOARD_PADDING;
        GridLayout game = getGridLayout(new LinearLayout.LayoutParams(boardSize, boardSize), boardSize, boardSize, 3, 3);
        game.setBackgroundColor(BORDER_COLOR);
        gameContainer.addView(game);
        main.addView(gameContainer);

        int gridSize = boardSize / 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                initGrids(game, gridSize, i, j);
            }
        }

        initNumPad(main);
    }

    private void initGrids(GridLayout game, int gridSize, int i, int j) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
        GridLayout grid = getGridLayout(params, gridSize, gridSize, 3, 3);
        params.setGravity(Gravity.CENTER);
        setBorder(grid, BORDER_COLOR, BORDER_COLOR, 5, 0);
        initButtons(grid, gridSize / 3 - (gridSize % 3) - 3, i, j);
        game.addView(grid);
    }

    private void initButtons(GridLayout grid, int buttonSize, int row, int col) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = getButton(
                        new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j)),
                        buttonSize, buttonSize, row * 3 + i, col * 3 + j
                );
                btn.setMinWidth(buttonSize);
                btn.setMinHeight(buttonSize);
                btn.setMaxWidth(buttonSize);
                btn.setMaxHeight(buttonSize);
                btn.setTextSize(20);
                setBorder(btn, BUTTON_COLOR, BORDER_COLOR, 5, 10);
                btn.setTextColor(BUTTON_TEXT_COLOR);
                grid.addView(btn);
                buttons[row * 3 + i][col * 3 + j] = btn;
            }
        }
    }


    private GridLayout getGridLayout(ViewGroup.LayoutParams params, int width, int height, int row, int col) {
        GridLayout grid = new GridLayout(this);
        grid.setRowCount(row);
        grid.setColumnCount(col);
        params.width = width;
        params.height = height;
        grid.setLayoutParams(params);
        return grid;
    }
    private Button getButton(ViewGroup.LayoutParams params, int width, int height, int row, int col) {
        Button btn = new Button(getApplicationContext());
        params.width = width;
        params.height = height;
        btn.setLayoutParams(params);
        btn.setSingleLine(true);
        btn.setEllipsize(null);
        btn.setPadding(0, 0, 0, 0);
        btn.setGravity(Gravity.CENTER);


        btn.setOnClickListener(e -> {
            if (selected != null) {
                setBorder(selected, BUTTON_COLOR, BORDER_COLOR, 5, 10);
            }
            if (!keys[row][col]) {
                selected = btn;
                selectedRow = row;
                selectedColumn = col;
                setBorder(btn, Color.GRAY, BORDER_COLOR, 5, 10);
            } else {
                selected = null;
            }
        });
        return btn;
    }


    private void setBorder(View v, int color, int borderColor, int borderWidth, float cornerRadius) {
        float[] radii = new float[] {
                cornerRadius, cornerRadius, // top-left corner
                cornerRadius, cornerRadius, // top-right corner
                cornerRadius, cornerRadius, // bottom-right corner
                cornerRadius, cornerRadius  // bottom-left corner
        };
        RoundRectShape roundRectShape = new RoundRectShape(radii, null, null);
        ShapeDrawable shape = new ShapeDrawable(roundRectShape) {
            @Override
            public void draw(Canvas canvas) {
                Paint paint = getPaint();
                int width = getBounds().width();
                int height = getBounds().height();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(color);
                canvas.drawRoundRect(0, 0, width, height, cornerRadius, cornerRadius, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(borderColor);
                paint.setStrokeWidth(borderWidth);
                canvas.drawRoundRect(0, 0, width, height, cornerRadius, cornerRadius, paint);
            }
        };
        v.setBackground(shape);
    }

    private void initNumPad(LinearLayout mainLayout) {
        LinearLayout numPad = new LinearLayout(this);
        numPad.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams numPadParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        numPad.setLayoutParams(numPadParams);

        // Create the numpad layout
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(createButton("1"));
        row1.addView(createButton("2"));
        row1.addView(createButton("3"));
        row1.addView(createButton("Check"));

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(createButton("4"));
        row2.addView(createButton("5"));
        row2.addView(createButton("6"));
        row2.addView(createButton("Clear"));

        LinearLayout row3 = new LinearLayout(this);
        row3.setOrientation(LinearLayout.HORIZONTAL);
        row3.addView(createButton("7"));
        row3.addView(createButton("8"));
        row3.addView(createButton("9"));
        row3.addView(createButton("Reset"));

        LinearLayout row4 = new LinearLayout(this);
        row4.setOrientation(LinearLayout.HORIZONTAL);
        row4.addView(createButton("New Board"));
        row4.addView(createButton("Solve"));

        numPad.addView(row1);
        numPad.addView(row2);
        numPad.addView(row3);
        numPad.addView(row4);
        mainLayout.addView(numPad);
    }

    private Button createButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.CYAN);
        button.setBackgroundColor(BUTTON_COLOR);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(NUMPAD_MARGIN / 2, NUMPAD_MARGIN / 2, NUMPAD_MARGIN / 2, NUMPAD_MARGIN / 2);
        button.setLayoutParams(params);
        setBorder(button, BORDER_COLOR, BUTTON_COLOR, 2, 12);
        button.setTextSize(20);
        button.setTypeface(null, Typeface.BOLD);

        button.setOnClickListener(e -> {
            if (text.equals("New Board")) {
                showDifficultyDialog();
            } else if (text.equals("Check")) {
                checkGame();
            } else if (text.equals("Clear")) {
                if (selected != null) {
                    selected.setTextColor(Color.BLACK);
                    selected.setText("");
                }
            } else if (text.equals("Reset")) {
                resetGame();
            } else if (text.equals("Solve")) {
                solveGame();
            } else {
                if (game && selected != null) {
                    selected.setText(text);
                    saveState(selectedRow, selectedColumn);
                }
            }
        });
        return button;
    }

    private void dialog(String msg, String yes, String no, DialogInterface.OnClickListener yesAction, DialogInterface.OnClickListener noAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(yes, yesAction)
                .setNegativeButton(no, noAction);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    keys[i][j] = true;
                    buttons[i][j].setTypeface(null, Typeface.BOLD_ITALIC);
                    buttons[i][j].setTextSize(22);
                    setBorder(buttons[i][j], Color.parseColor("#8ff8f8"), BORDER_COLOR, 5, 10);
                    buttons[i][j].setTextColor(Color.RED);
                    buttons[i][j].setText(String.valueOf(board[i][j]));
                } else {
                    keys[i][j] = false;
                    buttons[i][j].setTypeface(null, Typeface.NORMAL);
                    buttons[i][j].setTextSize(20);
                    setBorder(buttons[i][j], BUTTON_COLOR, BORDER_COLOR, 5, 10);
                    buttons[i][j].setTextColor(Color.BLACK);
                    buttons[i][j].setText("");
                }
            }
        }
    }


    private void checkGame() {
        if (game) {
            boolean completed = true;
            SudokuSolver sol = new SudokuSolver();
            byte[][] temp = new byte[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    temp[i][j] = Byte.parseByte((String) (buttons[i][j].getText().equals("")?"0": buttons[i][j].getText()));
                    if (temp[i][j] == 0) {
                        completed = false;
                    }
                }
            }
            if (sol.isValidSolution(temp) && completed) {
                dialog("Congratulations!, You won the game. \n\n Do you want to play again?", "Yes", "No", (a, b) -> {
                    showDifficultyDialog();
                }, (a, b) -> {
                    finish();
                });
            } else {
                dialog("Please check everything \n\nOr do you want to generate a new gaame", "Yes", "No", (a, b) ->{
                    showDifficultyDialog();
                }, (a, b) -> {

                });
            }
        }
    }

    private void solveGame() {
        if (game) {
            SudokuSolver sol = new SudokuSolver();
            byte[][] temp = new byte[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    temp[i][j] = Byte.parseByte(
                            (String) (buttons[i][j].getText().equals("") ? "0" : buttons[i][j].getText())
                    );
                }
            }
            if (!sol.isValidSolution(temp)) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        temp[i][j] = board[i][j];
                    }
                }

            }
            if (!sol.getSolution(temp)) {
                dialog("Unable to solve the board. Please check your inputs or generate a new board.", "OK", null, null, null);
                return;
            }
            updateSolution(temp);
        }
    }

    private void updateSolution(byte[][] solution) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if ((!buttons[i][j].getText().toString().equals("" + solution[i][j])) && !keys[i][j]) {
                    buttons[i][j].setTextColor(Color.parseColor("#0aa300"));
                    buttons[i][j].setText("" + solution[i][j]);
                }
            }
        }
    }

    private void generateNewGame(SudokuGenerator.DIFFICULTY difficulty) {
        SharedPreferences sharedPreferences = getSharedPreferences("SudokuGame", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        game = true;
        SudokuGenerator gen = new SudokuGenerator(difficulty);
        board = gen.generateBoard();
        resetGame();
        saveBoardState();
    }

    private void showDifficultyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Difficulty");
        String[] difficulties = {"EASY", "MEDIUM", "HARD", "EXPERT"};
        builder.setItems(difficulties, (dialog, which) -> {
            SudokuGenerator.DIFFICULTY selectedDifficulty;
            switch (which) {
                case 1:
                    selectedDifficulty = SudokuGenerator.DIFFICULTY.MEDIUM;
                    break;
                case 2:
                    selectedDifficulty = SudokuGenerator.DIFFICULTY.HARD;
                    break;
                case 3:
                    selectedDifficulty = SudokuGenerator.DIFFICULTY.EXPERT;
                    break;
                default:
                    selectedDifficulty = SudokuGenerator.DIFFICULTY.EASY;
                    break;
            }
            generateNewGame(selectedDifficulty);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void saveState(int i, int j) {
        SharedPreferences sharedPreferences = getSharedPreferences("SudokuGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("btn_text_" + i + "_" + j, buttons[i][j].getText().toString());
        editor.putInt("btn_text_color_" + i + "_" + j, buttons[i][j].getCurrentTextColor());
        editor.apply();
    }

    private void saveBoardState() {
        SharedPreferences sharedPreferences = getSharedPreferences("SudokuGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                editor.putInt("board_" + i + "_" + j, board[i][j]);
                editor.putBoolean("key_" + i + "_" + j, keys[i][j]);
                String buttonText = buttons[i][j].getText().toString();
                editor.putString("btn_text_" + i + "_" + j, buttonText.isEmpty() ? "0" : buttonText);
                editor.putInt("btn_text_color_" + i + "_" + j, buttons[i][j].getCurrentTextColor());
            }
        }
        editor.putBoolean("game_active", game);
        editor.apply();
    }

    private void restoreBoardState() {
        SharedPreferences sharedPreferences = getSharedPreferences("SudokuGame", MODE_PRIVATE);
        board = new byte[9][9];
        keys = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = (byte) sharedPreferences.getInt("board_" + i + "_" + j, 0);
                keys[i][j] = sharedPreferences.getBoolean("key_" + i + "_" + j, false);
            }
        }
        game = sharedPreferences.getBoolean("game_active", false);
        resetGame();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!keys[i][j]) {
                    String buttonText = sharedPreferences.getString("btn_text_" + i + "_" + j, "0");
                    buttons[i][j].setText(buttonText.equals("0") ? "" : buttonText);
                    int textColor = sharedPreferences.getInt("btn_text_color_" + i + "_" + j, Color.BLACK);
                    buttons[i][j].setTextColor(textColor);
                }
            }
        }
    }
}
