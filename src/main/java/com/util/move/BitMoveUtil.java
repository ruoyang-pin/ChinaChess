package com.util.move;

public class BitMoveUtil {

    // 例如第22位表示吃子标志，第23位表示颜色标志
    public static int buildMove(int from, int to, int chessType, int color, int score) {
        return (from & 0x7F)
                | ((to & 0x7F) << 7)
                | ((chessType & 0xF) << 14)
                | ((color & 0x1) << 23)
                | ((score & 0xFF) << 24);
    }

    // 吃子走法
    public static int buildCaptureMove(int from, int to, int chessType, int capturedChessType, int color, int score) {
        return (from & 0x7F)
                | ((to & 0x7F) << 7)
                | ((chessType & 0xF) << 14)
                | ((capturedChessType & 0xF) << 18)
                | (1 << 22)
                | ((color & 0x1) << 23)
                | ((score & 0xFF) << 24);
    }

    public static int setScore(int move, int score) {
        // 清除高 8 位
        move &= 0x00FFFFFF;
        // 设置新分数
        move |= (score & 0xFF) << 24;
        return move;
    }

    public static boolean equalsIgnoreScore(int move1, int move2) {
        // 屏蔽分数字段（高8位），只比较低24位
        return (move1 & 0x00FFFFFF) == (move2 & 0x00FFFFFF);
    }


    public static int getFrom(int move) {
        return move & 0x7F;
    }

    public static int getTo(int move) {
        return (move >> 7) & 0x7F;
    }

    public static int getChessType(int move) {
        return (move >> 14) & 0xF;
    }

    public static int getCapturedType(int move) {
        return (move >> 18) & 0xF;
    }

    public static boolean isCapture(int move) {
        return (move & (1 << 22)) != 0;
    }

    // 新增：获取颜色标志位（0 或 1）
    public static int getColor(int move) {
        return (move >> 23) & 0x1;
    }

    public static int getScore(int move) {
        return (move >>> 24) & 0xFF;
    }

}
