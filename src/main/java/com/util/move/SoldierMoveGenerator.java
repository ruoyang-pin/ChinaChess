package com.util.move;

import com.enumerates.ChessColor;

import static com.util.move.MoveGenerator.POS_X;
import static com.util.move.MoveGenerator.POS_Y;

/**
 * @author rich
 * @date 2025/6/28
 * @description
 */
public class SoldierMoveGenerator {

    private static final boolean[] SOLDIER_CROSSED_RIVER_RED = new boolean[10];
    private static final boolean[] SOLDIER_CROSSED_RIVER_BLACK = new boolean[10];

    static {
        // 红方 0 ~ 4 行为过河
        for (int i = 0; i <= 4; i++) SOLDIER_CROSSED_RIVER_RED[i] = true;
        // 黑方 5 ~ 9 行为过河
        for (int i = 5; i <= 9; i++) SOLDIER_CROSSED_RIVER_BLACK[i] = true;
    }

    public static void generateSoldierMoves(int pos, ChessColor color, int chessType, MoveBuffer moves,
                                            final BoardStateBuilder boardStateBuilder, boolean onlyKill, ChessColor enemyColor, int ownKingPos, int enemyKingPos) {
        int row = POS_X[pos];
        int col = POS_Y[pos];
        int ordinal = color.ordinal();
        int enemyOrdinal = enemyColor.ordinal();
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;
        boolean[] crossedRiverTable = (color == ChessColor.R) ? SOLDIER_CROSSED_RIVER_RED : SOLDIER_CROSSED_RIVER_BLACK;

        int forward = (color == ChessColor.R) ? -1 : 1;

        // 前进一步
        int fRow = row + forward;
        if (fRow >= 0 && fRow <= 9) {
            int fIndex = fRow * 9 + col;
            if (occupied[fIndex]) {
                if (colorBoard[fIndex] == enemyOrdinal && MoveGeneratorCheck.isLineExposedAfterMove(pos, fIndex, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                    byte type = typeBoard[fIndex];
                    int score = MoveBuffer.querySortScore(type);
                    moves.addCaptureMove(BitMoveUtil.buildCaptureMove(pos, fIndex, chessType, type, ordinal, score));
                }
            } else if (!onlyKill && MoveGeneratorCheck.isLineExposedAfterMove(pos, fIndex, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                moves.addQuietMove(BitMoveUtil.buildMove(pos, fIndex, chessType, ordinal, 0));
            }
        }

        // 过河后可以左右走
        if (crossedRiverTable[row]) {
            int[] sideCols = {col - 1, col + 1};
            for (int c : sideCols) {
                if (c < 0 || c > 8) continue;
                int idx = row * 9 + c;

                if (occupied[idx]) {
                    if (colorBoard[idx] == enemyOrdinal && MoveGeneratorCheck.isLineExposedAfterMove(pos, idx, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
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


}
