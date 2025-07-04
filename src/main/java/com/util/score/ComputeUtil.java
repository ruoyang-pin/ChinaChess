package com.util.score;

import com.domain.chess.*;
import com.domain.location.Point;
import com.enumerates.ChessColor;
import com.enums.ChessType;

import java.util.Arrays;

import static com.enums.ChessType.*;

/**
 * @author rich
 * @date 2025/6/26
 * @description
 */
public class ComputeUtil {

    // 2. 预计算价值表（开局/中局/残局）
    private static final int[][] PIECE_VALUE_TABLE = {
            {10000, 550, 300, 350, 150, 150, 35},   // 开局
            {10000, 520, 320, 320, 150, 150, 35}    // 中残局
    };



    // 如果step范围有限（如<256），改用byte数组减少缓存占用
    private static final byte[] PHASE_TABLE = new byte[256];
    static {
        Arrays.fill(PHASE_TABLE, 0, 40, (byte)0);
        Arrays.fill(PHASE_TABLE, 40, 200, (byte)1);
    }

    // 32位位域编码：color(1bit) type(3bit) x(4bit) y(4bit)
    private static final int[] BONUS_LOOKUP = new int[1 << (1+3+4+4)];



    // y坐标0-8（共9行），过河条件改为y>3
    private static final int[][] SOLDIER_VALUE = new int[9][2];


    static {
        // 初始化士兵价值
        for (int y = 0; y < 9; y++) {
            // 基础值：y=4时为100，其他为35
            SOLDIER_VALUE[y][0] = (y == 4) ? 100 : 35;  // 未过河（y<=4）
            SOLDIER_VALUE[y][1] = SOLDIER_VALUE[y][0] + 30; // 过河（y>4）加30分
        }
        // 初始化所有可能组合
        for (int color = 0; color < 2; color++) {
            for (int type = 0; type < 7; type++) {
                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 9; y++) {
                        int key = (color << 11) | (type << 8) | (x << 4) | y;
                        BONUS_LOOKUP[key] = calculateBonus(color, type, x, y);
                    }
                }
            }
        }

    }


//    public static int evaluateMoveScore(Move move, Chess[][] board, ChessColor color, Integer depth, Integer step) {
//        int score = 0;
//
//        Chess target = getChessAt(board, move.getMoveTo());
//
//        if (target != null) {
//            score += (getValueUltimate(target, step) + getPositionBonusV2(target)) * 100;
//            if (target.isKing()) score += 1_000_000;
//        }
//
//        if (depth != null && KillerMoveUtil.isKillerMove(move, depth)) {
//            score += 1_000_000; // 提升优先级，保证 killer move 最优先尝试
//        }
//
//        // 鼓励走车马
//        if (move.getChess() instanceof Car || move.getChess() instanceof Horse) {
//            score += 500;
//        }
//
//        if (mayAttackKing(move, color)) {
//            score += 50_000;
//        }
//
//        int col = move.getMoveTo().getX();
//        if (col >= 3 && col <= 5) {
//            score += 300;
//        }
//        return score;
//    }


    private static int getPhase(int step) {
        if (step < 20) return 0; // 开局
        if (step < 40) return 1; // 中局
        return 2; // 残局
    }


    public static Chess getChessAt(Chess[][] board, Point p) {
        return board[p.getX()][p.getY()];
    }


    private static boolean hasCrossedRiver(Chess c) {
        if (c.getColor() == ChessColor.R) {
            return c.getX() < 5;
        } else {
            return c.getX() > 4;
        }
    }


    public static int getPieceValue(Chess c, int step) {
        int phase = getPhase(step); // 0 = opening, 1 = midgame, 2 = endgame

        if (c.isKing()) return 10000;

        if (c.getName().equals("車")) return phase == 0 ? 550 : 520; // 提高车价值

        if (c.getName().equals("馬")) return phase == 0 ? 300 : 320; // 提高马价值

        if (c.getName().equals("炮")) return phase == 0 ? 350 : 320;

        if (c.getName().equals("仕")) return 150;

        if (c.getName().equals("相")) return 150;

        if (c.getName().equals("卒")) {
            int base = c.getY() == 4 ? 100 : 35;
            if (hasCrossedRiver(c)) base += 30; // 过河兵加分
            return base;
        }
        return 0;
    }

    public static int getPiece(Chess c, int step) {
        int phase = getPhase(step); // 0 = opening, 1 = midgame, 2 = endgame

        if (c.isKing()) return 10000;

        if (c instanceof Car) return phase == 0 ? 550 : 520; // 提高车价值

        if (c instanceof Horse) return phase == 0 ? 300 : 320; // 提高马价值

        if (c instanceof Cannon) return phase == 0 ? 350 : 320;

        if (c instanceof PrimeMinister) return 150;

        if (c instanceof Guards) return 150;

        if (c instanceof Soldier) {
            int base = c.getLocation().getY() == 4 ? 100 : 35;
            if (hasCrossedRiver(c)) base += 30; // 过河兵加分
            return base;
        }

        return 0;
    }



    public static int getPositionBonus(Chess c) {
        if (c instanceof Car) {
            // 中路优先 + 前进优先
            return (4 - Math.abs(c.getY() - 4)) * 5 + (c.getColor() == ChessColor.R ? (9 - c.getX()) : c.getX()) * 2;
        }

        if (c instanceof Horse) {
            // 靠近中心优先 + 过河优先
            return (4 - Math.abs(c.getY() - 4)) * 3 + (c.getColor() == ChessColor.R ? (4 - c.getX()) : (c.getX() - 5)) * 3;
        }

        return 0;
    }

    public static int getPositionBonusV2(Chess c) {
        // 位打包参数
        int key = (c.getColor().ordinal() << 11)
                | (c.getType().ordinal() << 8)
                | (c.getX() << 4)
                | c.getY();

        return BONUS_LOOKUP[key];
    }

    // 黑方坐标转换（避免重复计算）
    private static int transformForBlack(int baseBonus, ChessType type, int x) {
        if (type == ChessType.CAR) {
            return baseBonus - (9 - 2*x) * 2; // 数学等价转换
        }
        if (type == ChessType.HORSE) {
            return baseBonus + (2*x - 8) * 3; // 数学等价转换
        }
        return baseBonus;
    }

//    private static boolean mayAttackKing(Move move, ChessColor color) {
//        ChessColor enemyColor = (color == ChessColor.R ? ChessColor.B : ChessColor.R);
//        Point enemyKingLocation = KingPositionUtil.getKingPosition(enemyColor);
//        if (enemyKingLocation == null) {
//            // 说明对方将已经被吃掉，通常你可以返回 false
//            return false;
//        }
//        Point to = move.getMoveTo();
//        return to.equals(enemyKingLocation);
//    }

    public static int getValue(Chess c, int step) {
        int phase = getPhase(step); // 0 = opening, 1 = midgame, 2 = endgame

        if (c.isKing()) return 10000;

        if (c instanceof Car) return phase == 0 ? 550 : 520; // 提高车价值

        if (c instanceof Horse) return phase == 0 ? 300 : 320; // 提高马价值

        if (c instanceof Cannon) return phase == 0 ? 350 : 320;

        if (c instanceof PrimeMinister) return 150;

        if (c instanceof Guards) return 150;

        if (c instanceof Soldier) {
            int base = c.getLocation().getY() == 4 ? 100 : 35;
            if (hasCrossedRiver(c)) base += 30; // 过河兵加分
            return base;
        }

        return 0;
    }



    // 结合所有优化
    public static int getValueUltimate(Chess c, int step) {
        final int phase = (step & 0x40) >>> 6; // 位运算阶段判断
        switch (c.type) { // 依赖字段内存连续访问
            case KING:    return 10_000;
            case CAR:     return 550 - (phase * 30); // 数学替代三元
            case HORSE:   return 300 + (phase * 20);
            case CANNON:  return 350 - (phase * 30);
            case SOLDIER: return SOLDIER_VALUE[c.y & 0x7][(c.y > 4) ? 1 : 0];
            default:      return 150;
        }
    }

    private static int calculateBonus(int color, int type, int x, int y) {
        // 红方视角坐标转换（黑方在初始化时已做镜像处理）
        final int redX = (color == ChessColor.R.ordinal()) ? x : 9 - x;

        // 中心距离计算（0-4，越小越靠近中心）
        final int centerDist = Math.abs(y - 4);
        ChessType chessType = getByValue(type);
        switch (chessType) {
            case CAR:    // 车：中路优先 + 前进优先
                return (4 - centerDist) * 5 + (9 - redX) * 2;

            case HORSE:  // 马：靠近中心 + 过河优先
                return (4 - centerDist) * 3 + (4 - redX) * 3;

            case CANNON: // 炮：后方炮台价值高，过河后价值提升
                return (redX >= 6 ? 15 : 0) + (redX <= 4 ? (5 - redX) * 4 : 0);

            case SOLDIER:   // 兵：过河后价值剧增
                return redX <= 4 ? (5 - redX) * 10 + (4 - centerDist) * 2 : 0;

            case KING:   // 将/帅：安全位置价值高
                // 九宫中心位置价值最高
                if (redX >= 7 && redX <= 9 && y >= 3 && y <= 5) {
                    return 20 - Math.abs(redX - 8) - Math.abs(y - 4);
                }
                return 0;

            case GUARDS:  // 士：九宫中心价值高
                if (redX >= 7 && redX <= 9 && y >= 3 && y <= 5) {
                    return 15 - Math.abs(redX - 8) - Math.abs(y - 4);
                }
                return 0;

            case PRIME_MINISTER: // 象：河界防守位置
                // 象眼位置（y=2,6）价值高
                return (redX >= 5 ? (redX - 4) * 3 : 0) +
                        ((y == 2 || y == 6) ? 5 : 0);

            default:
                return 0;
        }
    }




}
