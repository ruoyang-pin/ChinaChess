package com.util.move;

import com.domain.chess.BitBoard;
import com.enumerates.ChessColor;
import com.util.ChessTypeUtil;

public class BoardStateBuilder {

    // 棋盘是否被占用，0~89格
    public final boolean[] occupied = new boolean[90];

    // 每格棋子类型，0 表示无棋子
    public final byte[] typeBoard = new byte[90];

    // 每格棋子颜色，0 表示空，1 表示红棋，2 表示黑棋
    public final byte[] colorBoard = new byte[90];

    // ThreadLocal 支持多线程并发安全
    private static final ThreadLocal<BoardStateBuilder> THREAD_LOCAL_INSTANCE =
            ThreadLocal.withInitial(BoardStateBuilder::new);

    public static BoardStateBuilder getInstance() {
        return THREAD_LOCAL_INSTANCE.get();
    }

    /**
     * 初始化状态（根节点调用一次）
     */
    public void build(BitBoard board) {
        long[] red = board.redPieces;
        long[] black = board.blackPieces;

        for (int i = 0; i < 90; i++) {
            int part = i >>> 6;
            int offset = i & 63;
            long mask = 1L << offset;

            boolean isRed = (red[part] & mask) != 0;
            boolean isBlack = (black[part] & mask) != 0;

            occupied[i] = isRed || isBlack;

            if (isRed) {
                colorBoard[i] = (byte) ChessColor.R.ordinal(); // 红棋
            } else if (isBlack) {
                colorBoard[i] = (byte) ChessColor.B.ordinal(); // 黑棋
            } else {
                colorBoard[i] = -1; // 空
            }

            int x = i / 9;
            int y = i % 9;
            typeBoard[i] = (byte) ChessTypeUtil.getChessTypeAt(board, x, y);
        }
    }

    /**
     * 应用一步棋（快速更新）
     */
    public void applyMove(int from, int to, int moveChessType, ChessColor moveColor) {
        occupied[from] = false;
        occupied[to] = true;

        typeBoard[from] = -1;
        typeBoard[to] = (byte) moveChessType;

        colorBoard[from] = 0;
        colorBoard[to] = (byte) moveColor.ordinal();
    }

    /**
     * 撤销一步棋
     */
    public void undoMove(int from, int to, boolean isCapture, int capturedType, int moveChessType, ChessColor moveColor) {
        occupied[from] = true;
        occupied[to] = isCapture;

        typeBoard[from] = (byte) moveChessType;
        typeBoard[to] = (byte) (isCapture ? capturedType : -1);

        colorBoard[from] = (byte) moveColor.ordinal();
        colorBoard[to] = (byte) (isCapture ? (moveColor == ChessColor.R ? 0 : 1) : -1);
    }

    public void remove() {
        THREAD_LOCAL_INSTANCE.remove();
    }

    /**
     * 查询是否被占用（高性能）
     */
    public boolean isOccupied(int idx) {
        return occupied[idx];
    }

    /**
     * 查询棋子类型（高性能）
     */
    public int getChessType(int idx) {
        return typeBoard[idx];
    }

    /**
     * 查询棋子颜色（高性能）
     *
     * @return -1 = 空，0 = 红，1 = 黑
     */
    public byte getColor(int idx) {
        return colorBoard[idx];
    }


}

