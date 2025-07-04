package com.util.move;

import com.enumerates.ChessColor;

import static com.util.move.MoveGenerator.POS_X;
import static com.util.move.MoveGenerator.POS_Y;

public final class HorseMoveGenerator {

    private static final int[] HORSE_TARGETS_FLAT = new int[90 * 16 * 8];
    private static final int[] HORSE_COUNTS_FLAT = new int[90 * 16];
    private static final int[][] HORSE_LEG_POS = new int[90][4];

    static {
        initHorsePrecompute();
    }

    private static void initHorsePrecompute() {
        for (int pos = 0; pos < 90; pos++) {
            int row = POS_X[pos];
            int col = POS_Y[pos];

            HORSE_LEG_POS[pos][0] = (row - 1 >= 0) ? (row - 1) * 9 + col : -1; // 上
            HORSE_LEG_POS[pos][1] = (row + 1 <= 9) ? (row + 1) * 9 + col : -1; // 下
            HORSE_LEG_POS[pos][2] = (col - 1 >= 0) ? row * 9 + (col - 1) : -1; // 左
            HORSE_LEG_POS[pos][3] = (col + 1 <= 8) ? row * 9 + (col + 1) : -1; // 右

            for (int state = 0; state < 16; state++) {
                int base = (pos * 16 + state) * 8;
                int count = 0;

                if ((state & 1) == 0 && row - 2 >= 0) {
                    if (col - 1 >= 0) HORSE_TARGETS_FLAT[base + count++] = (row - 2) * 9 + (col - 1);
                    if (col + 1 <= 8) HORSE_TARGETS_FLAT[base + count++] = (row - 2) * 9 + (col + 1);
                }
                if ((state & 2) == 0 && row + 2 <= 9) {
                    if (col - 1 >= 0) HORSE_TARGETS_FLAT[base + count++] = (row + 2) * 9 + (col - 1);
                    if (col + 1 <= 8) HORSE_TARGETS_FLAT[base + count++] = (row + 2) * 9 + (col + 1);
                }
                if ((state & 4) == 0 && col - 2 >= 0) {
                    if (row - 1 >= 0) HORSE_TARGETS_FLAT[base + count++] = (row - 1) * 9 + (col - 2);
                    if (row + 1 <= 9) HORSE_TARGETS_FLAT[base + count++] = (row + 1) * 9 + (col - 2);
                }
                if ((state & 8) == 0 && col + 2 <= 8) {
                    if (row - 1 >= 0) HORSE_TARGETS_FLAT[base + count++] = (row - 1) * 9 + (col + 2);
                    if (row + 1 <= 9) HORSE_TARGETS_FLAT[base + count++] = (row + 1) * 9 + (col + 2);
                }

                HORSE_COUNTS_FLAT[pos * 16 + state] = count;
            }
        }
    }

    public static void generateHorseMoves(final int pos,
                                          final ChessColor color, final int chessType,
                                          final MoveBuffer moves,
                                          final BoardStateBuilder boardStateBuilder, boolean onlyKill, ChessColor enemyColor, int ownKingPos, int enemyKingPos, int pvMove) {

        int state = 0;
        int ordinal = color.ordinal();
        int enemyOrdinal = enemyColor.ordinal();
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;

        for (int i = 0; i < 4; i++) {
            int legPos = HORSE_LEG_POS[pos][i];
            if (legPos != -1 && occupied[legPos]) {
                state |= (1 << i);
            }
        }

        final int base = (pos * 16 + state) * 8;
        final int count = HORSE_COUNTS_FLAT[pos * 16 + state];

        for (int i = 0; i < count; i++) {
            int target = HORSE_TARGETS_FLAT[base + i];
            boolean isOccupied = occupied[target];
            if (isOccupied) { // 优先吃子路径（博弈树更优先考虑）
                if (colorBoard[target] == enemyOrdinal && MoveGeneratorCheck.isLineExposedAfterMove(pos, target, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                    //评分
                    byte type = typeBoard[target];
                    int score = MoveBuffer.querySortScore(type);
                    moves.addCaptureMove(BitMoveUtil.buildCaptureMove(pos, target, chessType, type, ordinal, score), pvMove);
                }
            } else if (!onlyKill && MoveGeneratorCheck.isLineExposedAfterMove(pos, target, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) { // 走子路径
                moves.addQuietMove(BitMoveUtil.buildMove(pos, target, chessType, ordinal, 0), pvMove);
            }
        }
    }
}
