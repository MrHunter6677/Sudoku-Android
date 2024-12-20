package com.my.sudoku;

import java.util.List;

public class SudokuSolver {

    public boolean isValidPlacement(byte[][] board, byte row, byte column, byte number) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == number || board[i][column] == number) {
                return false;
            }
        }

        byte startRow = (byte) (row - row % 3);
        byte startCol = (byte) (column - column % 3);
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == number) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean hasSolution(byte[][] board) {
        return getSolution(copyBoard(board));
    }

    public boolean getSolution(byte[][] board) {
        for (byte row = 0; row < 9; row++) {
            for (byte col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (byte num = 1; num <= 9; num++) {
                        if (isValidPlacement(board, row, col, num)) {
                            board[row][col] = num;
                            if (getSolution(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean getAllSolutions(byte[][] board, List<byte[][]> solutions) {
        for (byte row = 0; row < 9; row++) {
            for (byte col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (byte num = 1; num <= 9; num++) {
                        if (isValidPlacement(board, row, col, num)) {
                            board[row][col] = num;
                            if (getAllSolutions(board, solutions)) {
                                solutions.add(copyBoard(board));
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private byte[][] copyBoard(byte[][] board) {
        byte[][] copy = new byte[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

    public void printBoard(byte[][] board) {
        for (byte[] row : board) {
            for (byte cell : row) {
                System.out.print((cell == 0 ? ". " : cell + " "));
            }
            System.out.println();
        }
    }

    public boolean isValidSolution(byte[][] board) {
        for (byte row = 0; row < 9; row++) {
            for (byte col = 0; col < 9; col++) {
                byte number = board[row][col];
                if (number == 0 || !isValidPlacementIgnoringCell(board, row, col, number)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidPlacementIgnoringCell(byte[][] board, byte row, byte col, byte number) {
        for (int i = 0; i < 9; i++) {
            if ((i != col && board[row][i] == number) || (i != row && board[i][col] == number)) {
                return false;
            }
        }

        byte startRow = (byte) (row - row % 3);
        byte startCol = (byte) (col - col % 3);
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if ((i != row || j != col) && board[i][j] == number) {
                    return false;
                }
            }
        }

        return true;
    }
}
