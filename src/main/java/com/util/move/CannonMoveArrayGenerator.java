package com.util.move;

import com.enumerates.ChessColor;

public class CannonMoveArrayGenerator {
    // 预计算所有方向和路径
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final int[][][] PATH_POINTS = new int[90][4][];
    private static final int[][] PATH_LENGTHS = new int[90][4];

    static {
        // 类加载时初始化所有路径
        for (int pos = 0; pos < 90; pos++) {
            int row = pos / 9;
            int col = pos % 9;

            for (int d = 0; d < 4; d++) {
                int dr = DIRECTIONS[d][0];
                int dc = DIRECTIONS[d][1];

                // 计算路径长度
                int len = 0;
                int r = row + dr;
                int c = col + dc;
                while (r >= 0 && r < 10 && c >= 0 && c < 9) {
                    len++;
                    r += dr;
                    c += dc;
                }

                PATH_LENGTHS[pos][d] = len;

                // 填充路径点
                int[] path = new int[len];
                r = row + dr;
                c = col + dc;
                for (int i = 0; i < len; i++) {
                    path[i] = r * 9 + c;
                    r += dr;
                    c += dc;
                }
                PATH_POINTS[pos][d] = path;
            }
        }
    }

    public static void generateCannonMoves(int pos, ChessColor color,
                                           int type, MoveBuffer moves,
                                           BoardStateBuilder boardStateBuilder, boolean onlyKill, ChessColor enemyColor, int ownKingPos, int enemyKingPos, int pvMove) {
        int ordinal = color.ordinal();
        int enemyOrdinal = enemyColor.ordinal();
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;
        for (int d = 0; d < 4; d++) {
            final int[] path = PATH_POINTS[pos][d];
            final int len = PATH_LENGTHS[pos][d];

            int blockIdx = -1;
            for (int i = 0; i < len; i++) {
                int idx = path[i];
                if (occupied[idx]) {
                    blockIdx = i;
                    break;
                }
                if (!onlyKill && MoveGeneratorCheck.isLineExposedAfterMove(pos, idx, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                    moves.addQuietMove(BitMoveUtil.buildMove(pos, idx, type, ordinal, 0), pvMove);
                }
            }

            if (blockIdx != -1 && blockIdx + 1 < len) {
                for (int i = blockIdx + 1; i < len; i++) {
                    int idx = path[i];
                    if (occupied[idx]) {
                        if (colorBoard[idx] == enemyOrdinal && MoveGeneratorCheck.isLineExposedAfterMove(pos, idx, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) {
                            byte occupiedType = typeBoard[idx];
                            int score = MoveBuffer.querySortScore(occupiedType);
                            moves.addCaptureMove(BitMoveUtil.buildCaptureMove(pos, idx, type, occupiedType, ordinal, score), pvMove);
                        }
                        break;
                    }
                }
            }
        }
    }


}