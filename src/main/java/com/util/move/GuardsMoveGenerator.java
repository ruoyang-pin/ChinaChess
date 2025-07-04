package com.util.move;

import com.enumerates.ChessColor;

import static com.util.move.MoveGenerator.POS_X;
import static com.util.move.MoveGenerator.POS_Y;

/**
 * @author rich
 * @date 2025/6/28
 * @description
 */
public class GuardsMoveGenerator {

    private static final boolean[] GUARDS_VALID_POSITIONS_RED = new boolean[90];

    private static final boolean[] GUARDS_VALID_POSITIONS_BLACK = new boolean[90];

    // 仕(Guards) 只能在3x3宫内动，允许方向
    private static final int[][] GUARDS_MOVES = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    static {
        // 红方
        for (int r = 7; r <= 9; r++) {
            for (int c = 3; c <= 5; c++) {
                GUARDS_VALID_POSITIONS_RED[r * 9 + c] = true;
            }
        }
        // 黑方
        for (int r = 0; r <= 2; r++) {
            for (int c = 3; c <= 5; c++) {
                GUARDS_VALID_POSITIONS_BLACK[r * 9 + c] = true;
            }
        }
    }

    public static void generateGuardsMoves(int pos, ChessColor color, int chessType, MoveBuffer moves,
                                           final BoardStateBuilder boardStateBuilder, boolean onlyKill, ChessColor enemyColor, int ownKingPos, int enemyKingPos) {
        int row = POS_X[pos];
        int col = POS_Y[pos];
        int ordinal = color.ordinal();
        int enemyOrdinal = enemyColor.ordinal();
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;
        boolean[] validPositions = (color == ChessColor.R) ? GUARDS_VALID_POSITIONS_RED : GUARDS_VALID_POSITIONS_BLACK;

        for (int[] d : GUARDS_MOVES) {
            int r = row + d[0], c = col + d[1];
            if (r < 0 || r > 9 || c < 0 || c > 8) continue;

            int idx = r * 9 + c;

            // 宫外位置，直接跳过
            if (!validPositions[idx]) continue;

            // 吃子
            if (occupied[idx]) {
                if (colorBoard[idx] == enemyOrdinal && MoveGeneratorCheck.isLineExposedAfterMove(pos, idx, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                    byte type = typeBoard[idx];
                    int score = MoveBuffer.querySortScore(type);
                    moves.addCaptureMove(BitMoveUtil.buildCaptureMove(pos, idx, chessType, type, ordinal, score));
                }
            } else if (!onlyKill && MoveGeneratorCheck.isLineExposedAfterMove(pos, idx, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                // 普通走法
                moves.addQuietMove(BitMoveUtil.buildMove(pos, idx, chessType, ordinal, 0));

            }
        }
    }


}
