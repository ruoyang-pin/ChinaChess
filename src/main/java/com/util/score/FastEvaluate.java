package com.util.score;

import com.domain.chess.BitBoard;
import com.enumerates.ChessColor;
import com.enums.ChessType;

public class FastEvaluate {

    // 颜色(1bit) | 阶段(1bit) | 棋子类型(3bit) => 2*2*7=28项
    private static final int[] BASE_VALUE_TABLE = new int[16];

    // 位置加成表，pos(7bit) | type(3bit) | color(1bit) => 90 * 7 * 2 = 1260项
    private static final int[] POSITION_BONUS_TABLE = new int[90 * 7 * 2];

    static {
        initBaseValueTable();
        initPositionBonusTable();
    }

    private static void initBaseValueTable() {
        for (int phase = 0; phase < 2; phase++) {
            for (int type = 0; type < 7; type++) {
                int key = (phase << 3) | type;
                int value;
                switch (type) {
                    case 0:
                        value = 10_000;
                        break;  // KING
                    case 1:
                        value = 550 - (phase * 30);
                        break; // CAR
                    case 2:
                        value = 300 + (phase * 20);
                        break; // HORSE
                    case 3:
                        value = 350 - (phase * 30);
                        break; // CANNON
                    case 6:
                        value = 30 + (phase * 50);
                        break; // SOLDIER 简化版
                    default:
                        value = 100;
                        break; // GUARDS, PRIME_MINISTER
                }
                BASE_VALUE_TABLE[key] = value;
            }
        }
    }

    private static void initPositionBonusTable() {
        for (int pos = 0; pos < 90; pos++) {
            int row = pos / 9;
            int col = pos % 9;
            for (int type = 0; type < 7; type++) {
                for (int color = 0; color < 2; color++) {
                    int key = pos + type * 90 + color * 90 * 7;
                    POSITION_BONUS_TABLE[key] = PositionBonus.getPositionBonusFast(row, col, type, color);
                }
            }
        }
    }


    /**
     * 评估函数
     *
     * @param bitBoard 当前位棋盘
     * @param aiColor  AI方颜色
     * @param step     当前步数，用于阶段计算
     * @return 评分，正数对AI有利，负数对AI不利
     */
    public static int evaluate(BitBoard bitBoard, ChessColor aiColor, int step) {
        int phase = (step & 0x40) >>> 6; // 取局面阶段0或1
        int score = 0;

        long[] redAll = bitBoard.redPieces;
        long[] blackAll = bitBoard.blackPieces;

        for (ChessType chessType : ChessType.values()) {
            int ordinal = chessType.ordinal();
            long[] pieces = bitBoard.pieceBoards[ordinal];

            // 直接处理红方棋子
            long bits = pieces[0] & redAll[0];
            while (bits != 0) {
                int pos = Long.numberOfTrailingZeros(bits);
                bits &= bits - 1;

                int baseKey = (phase << 3) | ordinal;// 0 表示红方
                int value = BASE_VALUE_TABLE[baseKey];

                int bonusKey = pos + ordinal * 90;
                int bonus = POSITION_BONUS_TABLE[bonusKey];

                int sign = (ChessColor.R == aiColor) ? 1 : -1;
                score += sign * (value + bonus);
            }

            bits = pieces[1] & redAll[1];
            while (bits != 0) {
                int pos = Long.numberOfTrailingZeros(bits) + 64;
                bits &= bits - 1;

                int baseKey = (phase << 3) | ordinal;// 0 表示红方
                int value = BASE_VALUE_TABLE[baseKey];

                int bonusKey = pos + ordinal * 90;
                int bonus = POSITION_BONUS_TABLE[bonusKey];

                int sign = (ChessColor.R == aiColor) ? 1 : -1;
                score += sign * (value + bonus);
            }

            // 直接处理黑方棋子
            bits = pieces[0] & blackAll[0];
            while (bits != 0) {
                int pos = Long.numberOfTrailingZeros(bits);
                bits &= bits - 1;

                int baseKey = (phase << 3) | ordinal; // 1 表示黑方
                int value = BASE_VALUE_TABLE[baseKey];

                int bonusKey = pos + ordinal * 90 + 90 * 7;
                int bonus = POSITION_BONUS_TABLE[bonusKey];

                int sign = (ChessColor.B == aiColor) ? 1 : -1;
                score += sign * (value + bonus);
            }

            bits = pieces[1] & blackAll[1];
            while (bits != 0) {
                int pos = Long.numberOfTrailingZeros(bits) + 64;
                bits &= bits - 1;

                int baseKey = (phase << 3) | ordinal;
                ; // 1 表示黑方
                int value = BASE_VALUE_TABLE[baseKey];

                int bonusKey = pos + ordinal * 90 + 90 * 7;
                int bonus = POSITION_BONUS_TABLE[bonusKey];

                int sign = (ChessColor.B == aiColor) ? 1 : -1;
                score += sign * (value + bonus);
            }
        }
        return score - (step << 1);
    }


}
