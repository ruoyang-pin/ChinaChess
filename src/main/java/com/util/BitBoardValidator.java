package com.util;

import com.domain.chess.BitBoard;
import com.enums.ChessType;

public class BitBoardValidator {

    private static final int BOARD_SIZE = 90;

    /**
     * 校验单个 BitBoard 对象内部位图自洽性
     *
     * @param bitBoard 需要校验的棋盘
     * @return true 表示一致；false表示发现异常
     */
    public static boolean validateBitBoardConsistency(BitBoard bitBoard) {
        if (bitBoard == null) return false;

        for (int pos = 0; pos < BOARD_SIZE; pos++) {
            boolean occupied = getBit(bitBoard.allPieces, pos);

            // 如果没有棋子，红黑和类型位图都不应该占用该位置
            if (!occupied) {
                if (getBit(bitBoard.redPieces, pos) || getBit(bitBoard.blackPieces, pos)) {
                    System.out.printf("位置 %d 无棋子但红黑位图有标记%n", pos);
                    return false;
                }
                // 棋子类型位图也必须无占用
                for (ChessType type : ChessType.values()) {
                    if (getBit(bitBoard.pieceBoards[type.ordinal()], pos)) {
                        System.out.printf("位置 %d 无棋子但棋子类型 %s 位图有标记%n", pos, type.name());
                        return false;
                    }
                }
                continue;
            }

            // 有棋子位置，必须且只能属于红或黑
            boolean isRed = getBit(bitBoard.redPieces, pos);
            boolean isBlack = getBit(bitBoard.blackPieces, pos);

            if (isRed == isBlack) { // 不能同时是红黑或者都不是
                System.out.printf("位置 %d 有棋子但红黑归属错误 isRed=%s isBlack=%s%n", pos, isRed, isBlack);
                return false;
            }

            // 检查该位置到底属于哪个棋子类型，且只能唯一
            int typeCount = 0;
            ChessType foundType = null;
            for (ChessType type : ChessType.values()) {
                if (getBit(bitBoard.pieceBoards[type.ordinal()], pos)) {
                    typeCount++;
                    foundType = type;
                }
            }
            if (typeCount == 0) {
                System.out.printf("位置 %d 有棋子但未找到对应棋子类型%n", pos);
                return false;
            }
            if (typeCount > 1) {
                System.out.printf("位置 %d 有棋子但多个棋子类型重叠%n", pos);
                return false;
            }

            // 此时 foundType 不为空，且唯一
            // TODO 如果你有棋子颜色和类型的关系限制，可以在这里加校验
            // 例如：Soldier 只能属于红方或黑方等
        }

        // 全部检查通过
        return true;
    }

    private static boolean getBit(long[] bitboard, int pos) {
        int part = pos >>> 6;
        int offset = pos & 63;
        return (bitboard[part] & (1L << offset)) != 0;
    }

    public static void main(String[] args) {
        BitBoard init = BitBoardMoveUtil.init();
        boolean b = validateBitBoardConsistency(init);
        System.out.println(b);
    }
}
