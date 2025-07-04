package com.util.move;

import com.domain.chess.BitBoard;
import com.enumerates.ChessColor;

import static com.util.move.MoveGenerator.POS_X;
import static com.util.move.MoveGenerator.POS_Y;

public class KingMoveGenerator {


    private static final int[][] KING_MOVES = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};


    public static void generateKingMoves(BitBoard bitBoard, int pos, ChessColor color, int chessType, MoveBuffer moves,
                                         int ownKingPos, int enemyKingPos, final BoardStateBuilder boardStateBuilder, boolean onlyKill, ChessColor enemyColor) {

        int row = POS_X[pos];
        int col = POS_Y[pos];
        int ordinal = color.ordinal();
        int enemyOrdinal = enemyColor.ordinal();
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;

        int minRow = (color == ChessColor.R) ? 7 : 0;
        int maxRow = (color == ChessColor.R) ? 9 : 2;
        int minCol = 3;
        int maxCol = 5;
        long[] ownPieces = (color == ChessColor.R) ? bitBoard.redPieces : bitBoard.blackPieces;
        long[] columnOccupy = bitBoard.columnOccupy;
        for (int[] d : KING_MOVES) {
            int r = row + d[0], c = col + d[1];
            if (r < minRow || r > maxRow || c < minCol || c > maxCol) continue;

            int idx = r * 9 + c;

            if (isOwnPiece(ownPieces, idx)) continue;

            // 直接虚拟判断，不修改 occupied
            if (wouldCauseKingFaceToFace(enemyKingPos, pos, idx, columnOccupy)) {
                continue;
            }

            if (occupied[idx]) {
                if (colorBoard[idx] == enemyOrdinal) {
                    byte type = typeBoard[idx];
                    int score = MoveBuffer.querySortScore(type);
                    moves.addCaptureMove(BitMoveUtil.buildCaptureMove(pos, idx, chessType, type, ordinal, score));
                }
            } else if (!onlyKill) {
                moves.addQuietMove(BitMoveUtil.buildMove(pos, idx, chessType, ordinal, 0));
            }
        }
    }

    /**
     * 虚拟移动判断是否会导致双王相对，不修改 occupied
     */
    public static boolean wouldCauseKingFaceToFace(int enemyKingPos, int from, int to, long[] columnOccupy) {
        // 王移动后的位置
        int newKingCol = POS_Y[to];
        int enemyKingCol = POS_Y[enemyKingPos];

        // 如果移动后两王不在同一列，直接返回 false
        if (newKingCol != enemyKingCol) return false;

        int newKingRow = POS_X[to];
        int enemyKingRow = POS_X[enemyKingPos];

        int minRow = Math.min(newKingRow, enemyKingRow) + 1;
        int maxRow = Math.max(newKingRow, enemyKingRow);

        // 构造掩码：两王之间的格子
        long mask = ((1L << maxRow) - 1) ^ ((1L << minRow) - 1);

        // 当前列占位图
        long occ = columnOccupy[newKingCol];

        // 虚拟状态处理：from 视为空，to 视为占
        occ &= ~(1L << POS_X[from]); // 清除 from 行
        occ |= (1L << POS_X[to]);    // 设置 to 行

        // 如果两王之间的格子全部为空，成立飞将
        return (occ & mask) == 0;
    }

    private static boolean isOwnPiece(long[] ownPieces, int idx) {
        int part = idx >>> 6;
        int offset = idx & 63;
        long mask = 1L << offset;
        return (ownPieces[part] & mask) != 0;
    }


}
