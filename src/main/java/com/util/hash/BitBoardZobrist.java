package com.util.hash;

import com.domain.chess.BitBoard;
import com.enumerates.ChessColor;
import com.enums.ChessType;

public class BitBoardZobrist {

    private static final int BOARD_SIZE = 90;
    private static final int PIECE_TYPE_COUNT = 7; // 7种棋子类型
    private final static long[][] zobristTable = new long[PIECE_TYPE_COUNT * 2][BOARD_SIZE];

    private static final ThreadLocal<BitBoardZobrist> threadLocalPool = ThreadLocal.withInitial(BitBoardZobrist::new);

    static {
        initZobristTable();
    }


    private static void initZobristTable() {
        SplitMix64 rng = new SplitMix64(0xdeadbeef); // 固定种子保证复现
        for (int pt = 0; pt < PIECE_TYPE_COUNT * 2; pt++) {
            for (int pos = 0; pos < BOARD_SIZE; pos++) {
                zobristTable[pt][pos] = rng.nextLong();
            }
        }
    }

    public static BitBoardZobrist getInstance() {
        return threadLocalPool.get();
    }


    private int getZobristIndex(ChessColor color, ChessType pieceType) {
        return pieceType.ordinal() + color.ordinal() * PIECE_TYPE_COUNT;
    }

    /**
     * 计算整个棋盘的哈希值，适合开局或重置时调用
     *
     * @param bitBoard 你的BitBoard实例，含各棋子位图和颜色位图
     * @return 计算得到的哈希值
     */
    public long computeHash(BitBoard bitBoard) {
        long hash = 0L;
        for (ChessColor color : ChessColor.values()) {
            for (ChessType type : ChessType.values()) {
                int ptIdx = getZobristIndex(color, type);
                // 取出该类型棋子对应位图（红黑分开）
                // 假设你bitBoard有红棋、黑棋和棋子类型分开的位图，
                // 这里需要组合出对应颜色+类型的bitboard：
                long[] colorBits = (color == ChessColor.R) ? bitBoard.redPieces : bitBoard.blackPieces;
                long[] pieceBits = bitBoard.pieceBoards[type.ordinal()];

                // 计算交集 (仅该颜色该类型的棋子)
                long[] bits = new long[]{
                        colorBits[0] & pieceBits[0],
                        colorBits[1] & pieceBits[1]
                };

                hash ^= hashPiece(bits, ptIdx);
            }
        }
        return hash;
    }

    // 遍历bitboard上的所有1位，异或对应随机数
    private long hashPiece(long[] bits, int zobristIndex) {
        long h = 0L;
        long part = bits[0];
        while (part != 0) {
            int idx = Long.numberOfTrailingZeros(part);
            h ^= zobristTable[zobristIndex][idx];
            part &= part - 1;
        }
        part = bits[1];
        while (part != 0) {
            int idx = Long.numberOfTrailingZeros(part);
            h ^= zobristTable[zobristIndex][idx + 64];
            part &= part - 1;
        }
        return h;
    }

    /**
     * 增量更新哈希，包含走子和吃子
     *
     * @param fromPos      起始格(0~89)
     * @param toPos        目标格(0~89)
     * @param color        走子棋子颜色
     * @param pieceType    走子棋子类型
     * @param isCapture    是否吃子
     * @param capturedType 被吃棋子类型（无吃子可传null）
     */
    public long updateHash(long hash, int fromPos, int toPos, ChessColor color, ChessType pieceType,
                           boolean isCapture, int capturedType) {
        int idx = getZobristIndex(color, pieceType);
        hash ^= zobristTable[idx][fromPos]; // 移除旧位置
        hash ^= zobristTable[idx][toPos];   // 增加新位置
        ChessColor capturedColor = color == ChessColor.R ? ChessColor.B : ChessColor.R;
        if (isCapture) {
            ChessType chessType = ChessType.getByValue(capturedType);
            int capIdx = getZobristIndex(capturedColor, chessType);
            hash ^= zobristTable[capIdx][toPos]; // 目标格就是被吃子的位置
        }
        return hash;
    }


    public static void remove() {
        threadLocalPool.remove();
    }

}
