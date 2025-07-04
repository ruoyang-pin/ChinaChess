package com.util.move;

import com.enumerates.ChessColor;
import com.enums.ChessType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rich
 * @date 2025/7/1
 * @description
 */
public class MoveGeneratorCheck {

    // 预处理：将位置 -> 四个方向的扫描路径
    public static final int[][][] KING_SCAN_PATHS = new int[90][4][];
    /*
     * KING_SCAN_PATHS[kingPos][0]：向上路径
     * KING_SCAN_PATHS[kingPos][1]：向下路径
     * KING_SCAN_PATHS[kingPos][2]：向左路径
     * KING_SCAN_PATHS[kingPos][3]：向右路径
     */

    public static final int[][][] HORSE_ATTACK = new int[90][4][];

    // 对应马腿方向
    public static final int[] LEG_DX = {-1, -1, 1, 1};
    public static final int[] LEG_DY = {-1, 1, -1, 1};
    public static final int ROWS = 10;
    public static final int COLS = 9;


    static {
        for (int kingPos = 0; kingPos < 90; kingPos++) {
            int kingX = kingPos / 9;
            int kingY = kingPos % 9;

            // 向上
            List<Integer> up = new ArrayList<>();
            for (int x = kingX - 1; x >= 0; x--) up.add(x * 9 + kingY);
            KING_SCAN_PATHS[kingPos][0] = up.stream().mapToInt(Integer::intValue).toArray();

            // 向下
            List<Integer> down = new ArrayList<>();
            for (int x = kingX + 1; x <= 9; x++) down.add(x * 9 + kingY);
            KING_SCAN_PATHS[kingPos][1] = down.stream().mapToInt(Integer::intValue).toArray();

            // 向左
            List<Integer> left = new ArrayList<>();
            for (int y = kingY - 1; y >= 0; y--) left.add(kingX * 9 + y);
            KING_SCAN_PATHS[kingPos][2] = left.stream().mapToInt(Integer::intValue).toArray();

            // 向右
            List<Integer> right = new ArrayList<>();
            for (int y = kingY + 1; y <= 8; y++) right.add(kingX * 9 + y);
            KING_SCAN_PATHS[kingPos][3] = right.stream().mapToInt(Integer::intValue).toArray();

            for (int d = 0; d < 4; d++) {
                List<Integer> attackPositions = new ArrayList<>();

                int legX = kingX + LEG_DX[d];
                int legY = kingY + LEG_DY[d];

                if (legX < 0 || legX > 9 || legY < 0 || legY > 8) continue;

                // 上马腿，对应的攻击位
                if (d == 0) {
                    addIfValid(attackPositions, legX, legY - 1);
                    addIfValid(attackPositions, legX - 1, legY);
                }
                // 下马腿
                if (d == 1) {
                    addIfValid(attackPositions, legX - 1, legY);
                    addIfValid(attackPositions, legX, legY + 1);
                }
                // 左马腿
                if (d == 2) {
                    addIfValid(attackPositions, legX, legY - 1);
                    addIfValid(attackPositions, legX + 1, legY);
                }
                // 右马腿
                if (d == 3) {
                    addIfValid(attackPositions, legX, legY + 1);
                    addIfValid(attackPositions, legX + 1, legY);
                }

                HORSE_ATTACK[kingPos][d] = attackPositions.stream().mapToInt(Integer::intValue).toArray();
            }
        }

    }


    private static void addIfValid(List<Integer> list, int x, int y) {
        if (x >= 0 && x <= 9 && y >= 0 && y <= 8) {
            list.add(x * 9 + y);
        }
    }


    public static boolean isLineExposedAfterMove(int moveFrom, int moveTo, int ownKingPos, ChessColor enemyColor, BoardStateBuilder boardStateBuilder, int enemyKingPos) {

        if (moveTo == enemyKingPos) {
            //吃将 免检
            return true;
        }

        int kingX = ownKingPos / 9;
        int kingY = ownKingPos % 9;

        int fromX = moveFrom / 9;
        int fromY = moveFrom % 9;

        int toX = moveTo / 9;
        int toY = moveTo % 9;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;

        int enemyColorOrdinal = enemyColor.ordinal();

        // 横向扫描
        if (kingX == fromX) {
            if (fromY < kingY) {
                if (scanPath(KING_SCAN_PATHS[ownKingPos][2], enemyColor, boardStateBuilder, moveFrom, moveTo)) {
                    return false;
                }
            } else {
                if (scanPath(KING_SCAN_PATHS[ownKingPos][3], enemyColor, boardStateBuilder, moveFrom, moveTo)) {
                    return false;
                }
            }
        }

        // 纵向扫描
        if (kingY == fromY) {
            if (fromX < kingX) {
                if (scanPath(KING_SCAN_PATHS[ownKingPos][0], enemyColor, boardStateBuilder, moveFrom, moveTo)) {
                    return false;
                }
            } else {
                if (scanPath(KING_SCAN_PATHS[ownKingPos][1], enemyColor, boardStateBuilder, moveFrom, moveTo)) {
                    return false;
                }
            }
        }

        // 横向扫描
        if (kingX == toX) {
            if (toY < kingY) {
                if (scanPathV2(KING_SCAN_PATHS[ownKingPos][2], enemyColor, boardStateBuilder, moveFrom, moveTo)) {
                    return false;
                }
            } else {
                if ((scanPathV2(KING_SCAN_PATHS[ownKingPos][3], enemyColor, boardStateBuilder, moveFrom, moveTo))) {
                    return false;
                }
            }
        }

        // 纵向扫描
        if (kingY == toY) {
            if (toX < kingX) {
                if (scanPathV2(KING_SCAN_PATHS[ownKingPos][0], enemyColor, boardStateBuilder, moveFrom, moveTo)) {
                    return false;
                }
            } else {
                if (scanPathV2(KING_SCAN_PATHS[ownKingPos][1], enemyColor, boardStateBuilder, moveFrom, moveTo)) {
                    return false;
                }
            }
        }


        int dir = -1;
        if (fromX == kingX - 1 && fromY == kingY - 1) dir = 0; // 上马腿
        else if (fromX == kingX - 1 && fromY == kingY + 1) dir = 1; // 下马腿
        else if (fromX == kingX + 1 && fromY == kingY - 1) dir = 2; // 左马腿
        else if (fromX == kingX + 1 && fromY == kingY + 1) dir = 3; // 右马腿
        if (dir != -1) {
            int[] attackPositions = HORSE_ATTACK[ownKingPos][dir];
            for (int idx : attackPositions) {
                if (moveTo != idx && typeBoard[idx] == ChessType.HORSE.getValue() && colorBoard[idx] == enemyColorOrdinal) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 直接扫描预处理路径
     */
    private static boolean scanPath(int[] path, ChessColor enemyColor, BoardStateBuilder boardStateBuilder, int moveFrom, int moveTo) {
        boolean screenFound = false;
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;
        int ordinal = enemyColor.ordinal();

        for (int idx : path) {
            if (idx == moveFrom) continue;

            if (idx == moveTo) {
                if (screenFound) break; // 已找到挡子，moveTo 之后继续找炮
                screenFound = true;     // 标记 moveTo 作为屏子
                continue;
            }
            if (!occupied[idx]) continue;

            int type = typeBoard[idx];

            if (!screenFound) {
                // 直接攻击目标
                if (type == ChessType.KING.getValue()) return true; // 发现敌将
                if (type == ChessType.CAR.getValue() && ordinal == colorBoard[idx]) return true; // 发现敌车

                // 未找到挡子，当前是挡子
                screenFound = true;
            } else {
                // 已找到挡子，检查炮攻击
                if (type == ChessType.CANNON.getValue() && ordinal == colorBoard[idx]) return true;

                // 已找到挡子且不是炮，直接结束
                break;
            }
        }

        return false;
    }


    private static boolean scanPathV2(int[] path, ChessColor enemyColor, BoardStateBuilder boardStateBuilder, int moveFrom, int moveTo) {
        boolean screenFound = false;
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;
        int ordinal = enemyColor.ordinal();

        for (int idx : path) {
            if (idx == moveFrom) continue;

            if (idx == moveTo) {
                if (screenFound) break; // 已经找到挡子，moveTo 之后继续找是否是炮
                screenFound = true;
                continue;
            }
            if (!occupied[idx]) continue;
            int type = typeBoard[idx];
            if (screenFound) {
                // 已经找到挡子，当前是敌方炮
                if (type == ChessType.CANNON.getValue() && ordinal == colorBoard[idx]) {
                    return true;
                }
                break; // 有挡子后遇到的不是炮，直接结束
            } else {
                // 还没找到挡子，当前是一个棋子，标记为挡子
                screenFound = true;
            }
        }
        return false;
    }


}
