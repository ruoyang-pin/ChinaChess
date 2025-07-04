package com.util.move;

import com.enumerates.ChessColor;

import static com.util.move.MoveGenerator.POS_X;
import static com.util.move.MoveGenerator.POS_Y;

/**
 * @author rich
 * @date 2025/6/28
 * @description
 */
public class PrimeMinisterMoveGenerator {


    private static final boolean[] PRIME_MINISTER_VALID_ROWS_RED = new boolean[10];

    private static final boolean[] PRIME_MINISTER_VALID_ROWS_BLACK = new boolean[10];

    // Prime Minister 相（象）走法，走斜两个格子
    private static final int[][] PRIME_MINISTER_MOVES = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};

    // 象眼偏移量（相必须跳过的点）
    private static final int[][] PRIME_MINISTER_LEGS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    static {
        // 红方象区：5 ~ 9
        for (int i = 5; i <= 9; i++) PRIME_MINISTER_VALID_ROWS_RED[i] = true;

        // 黑方象区：0 ~ 4
        for (int i = 0; i <= 4; i++) PRIME_MINISTER_VALID_ROWS_BLACK[i] = true;

    }

    public static void generatePrimeMinisterMoves(int pos, ChessColor color,
                                                  int chessType, MoveBuffer moves,
                                                  final BoardStateBuilder boardStateBuilder, boolean onlyKill, ChessColor enemyColor, int ownKingPos, int enemyKingPos) {
        int row = POS_X[pos];
        int col = POS_Y[pos];
        int enemyOrdinal = enemyColor.ordinal();
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;
        int ordinal = color.ordinal();

        boolean[] validRows = (color == ChessColor.R) ? PRIME_MINISTER_VALID_ROWS_RED : PRIME_MINISTER_VALID_ROWS_BLACK;

        for (int i = 0; i < 4; i++) {
            int r = row + PRIME_MINISTER_MOVES[i][0];
            int c = col + PRIME_MINISTER_MOVES[i][1];

            // 越界 or 过河
            if (r < 0 || r > 9 || c < 0 || c > 8 || !validRows[r]) continue;

            // 象眼是否被挡
            if (occupied[(row + PRIME_MINISTER_LEGS[i][0]) * 9 + (col + PRIME_MINISTER_LEGS[i][1])]) continue;

            int idx = r * 9 + c;

            if (occupied[idx]) {
                if (colorBoard[idx] == enemyOrdinal && MoveGeneratorCheck.isLineExposedAfterMove(pos, idx, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                    // 敌方棋子
                    byte type = typeBoard[idx];
                    int score = MoveBuffer.querySortScore(type);
                    moves.addCaptureMove(BitMoveUtil.buildCaptureMove(pos, idx, chessType, type, ordinal, score));
                }
            } else if (!onlyKill && MoveGeneratorCheck.isLineExposedAfterMove(pos, idx, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                moves.addQuietMove(BitMoveUtil.buildMove(pos, idx, chessType, ordinal, 0));
            }
        }
    }

}
