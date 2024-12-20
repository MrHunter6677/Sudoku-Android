package com.my.sudoku;

import java.util.*;


public class SudokuGenerator {

    private static class RandomizedSet {

        private Map<Byte, Byte> data;
        private List<Byte> copy;
        private Random random;

        public RandomizedSet() {
            data = new HashMap<>();
            copy = new ArrayList<>();
            random = new Random();
        }

        public boolean insert(byte val) {
            if (!data.containsKey(val)) {
                copy.add(val);
                data.put(val, (byte) (copy.size() - 1));
                return true;
            }
            return false;
        }

        public boolean remove(byte val) {
            if (data.containsKey(val)) {
                byte index = data.get(val);
                byte lastElement = copy.get(copy.size() - 1);
                copy.set(index, lastElement);
                data.put(lastElement, index);
                copy.remove(copy.size() - 1);
                data.remove(val);
                return true;
            }
            return false;
        }

        public byte getRandom() {
            byte index = (byte) random.nextInt(copy.size());
            return copy.get(index);
        }

        public byte size() {
            return (byte) copy.size();
        }
    }

    private byte[][] board;
    private final int SWAPS = 1000;
    public enum DIFFICULTY {
        EASY,
        MEDIUM,
        HARD,
        EXPERT
    }
    private DIFFICULTY difficulty;
    private byte EMPTY_CELLS = 40;
    private Random random = new Random();
    private boolean emptyCellCount = false;

    public SudokuGenerator() {
        difficulty = DIFFICULTY.EASY;
    }

    public SudokuGenerator(DIFFICULTY difficulty) {
        this.difficulty = difficulty;
    }

    private byte[][] initBoard() {
        return new byte[][]{
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
    }
    public byte[][] generateBoard() {
        this.board = initBoard();
        for (int i = 0; i < SWAPS; i++) {
            swap(random);
        }
        for (byte i = 0; i < 9; i++) {
            for (byte j = (byte)(i + 1); j < 9; j++) {
                byte temp = board[i][j];
                board[i][j] = board[j][i];
                board[j][i] = temp;
            }
        }
        for (int i = 0; i < SWAPS; i++) {
            swap(random);
        }

        if (!emptyCellCount) {
            EMPTY_CELLS = switch (difficulty) {
                case EASY -> 35;
                case MEDIUM -> 45;
                case HARD -> 55;
                case EXPERT -> 65;
            };
        } else {
            emptyCellCount = false;
        }

        deleteElements();
        return board;
    }

    private void deleteElements() {
        List<Byte> cellIndices = new ArrayList<>();
        for (byte i = 0; i < 9; i++) {
            for (byte j = 0; j < 9; j++) {
                cellIndices.add((byte) (i * 9 + j));
            }
        }
        Collections.shuffle(cellIndices);
        int removedCells = 0;

        boolean oneEmptyGrid = true;
        for (byte index : cellIndices) {
            if (removedCells >= EMPTY_CELLS) {
                break;
            }
            byte row = (byte) (index / 9);
            byte col = (byte) (index % 9);
            byte temp = board[row][col];
            board[row][col] = 0;
            boolean allowOneEmptyGrid = false;
            if (difficulty == DIFFICULTY.EXPERT && oneEmptyGrid) {
                allowOneEmptyGrid = true;
            }
            if (allowOneEmptyGrid) {
                if (!isValidAfterRemoval(row, col, temp, true)) {
                    board[row][col] = temp;
                    allowOneEmptyGrid = false;
                    oneEmptyGrid = false;
                }
            } else if (!isValidAfterRemoval(row, col, temp, false)){
                    board[row][col] = temp;
            } else {
                removedCells++;
            }
        }
    }


    private boolean isValidAfterRemoval(int row, int col, int num, boolean allowOneEmptyGrid) {
        if (!containsAllNumbers()) return false;
        boolean hasNumberInRow = false;
        for (int i = 0; i < 9; i++) {
            if (board[row][i] != 0) {
                hasNumberInRow = true;
                break;
            }
        }
        if (!hasNumberInRow) return false;
        boolean hasNumberInCol = false;
        for (int i = 0; i < 9; i++) {
            if (board[i][col] != 0) {
                hasNumberInCol = true;
                break;
            }
        }
        if (!hasNumberInCol) return false;
        boolean hasNumberInGrid = false;
        int gridRowStart = (row / 3) * 3;
        int gridColStart = (col / 3) * 3;
        for (int i = gridRowStart; i < gridRowStart + 3; i++) {
            for (int j = gridColStart; j < gridColStart + 3; j++) {
                if (board[i][j] != 0) {
                    hasNumberInGrid = true;
                    break;
                }
            }
        }
        if (!allowOneEmptyGrid) {
            if (!hasNumberInGrid) return false;
        }
        return true;
    }

    private boolean containsAllNumbers() {
        boolean[] found = new boolean[9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    found[board[i][j] - 1] = true;
                }
            }
        }
        for (boolean num : found) {
            if (!num) return false;
        }
        return true;
    }

    public byte[][] generateBoard(byte emptyCells) {
        emptyCellCount = true;
        this.EMPTY_CELLS = (byte) Math.min(emptyCells, 72);
        return generateBoard();
    }

    private void swap(Random random) {
        byte block = (byte)(random.nextInt(3) * 3);
        byte row1 = (byte)(block + random.nextInt(3));
        byte row2 = (byte)(block + random.nextInt(3));
        byte[] temp = board[row1];
        board[row1] = board[row2];
        board[row2] = temp;
    }

    // Displays the board
    public void displayBoard(byte[][] board) {
        for (byte i = 0; i < 9; i++) {
            for (byte j = 0; j < 9; j++) {
                System.out.print(board[i][j] == 0 ? ". " : board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
