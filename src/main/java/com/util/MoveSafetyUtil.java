package com.util;

import com.domain.chess.*;
import com.domain.location.Point;
import com.enumerates.ChessColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveSafetyUtil {


    // Map定义：蹩马脚相对位置 -> 对应马的相对位置列表
    private static final Map<Point, List<Point>> LEG_TO_HORSE_MAP = new HashMap<>();

    static {
        LEG_TO_HORSE_MAP.put(new Point(1, 1), Arrays.asList(new Point(2, 1), new Point(1, 2)));
        LEG_TO_HORSE_MAP.put(new Point(1, -1), Arrays.asList(new Point(2, -1), new Point(1, -2)));
        LEG_TO_HORSE_MAP.put(new Point(-1, -1), Arrays.asList(new Point(-2, -1), new Point(-1, -2)));
        LEG_TO_HORSE_MAP.put(new Point(-1, 1), Arrays.asList(new Point(-2, 1), new Point(-1, 2)));
    }


    /**
     * 判断移动后是否会暴露自己将的安全（包含：炮架、挡车、将帅、马腿）
     */
//    public static boolean willExposeOwnKing(Chess[][] board, ChessColor selfColor, Point from, Point to, boolean king) {
//        return king ? KingWillExposeOwnKing(board, selfColor, from, to) : ExceptKingWillExposeOwnKing(board, selfColor, from, to);
//    }

    private static boolean KingWillExposeOwnKing(Chess[][] board, ChessColor selfColor, Point from, Point to) {
        ChessColor enemyColor = (selfColor == ChessColor.R ? ChessColor.B : ChessColor.R);

        // 四个方向检测 车、炮、将
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int dir = 0; dir < 4; dir++) {
            int nx = to.getX() + dx[dir];
            int ny = to.getY() + dy[dir];
            int blockCount = 0;
            while (nx >= 0 && nx < 10 && ny >= 0 && ny < 9) {
                Chess chess = board[nx][ny];
                if (chess != null) {
                    if (blockCount > 1) {
                        break;
                    }
                    if (!chess.getLocation().equals(from)) {
                        if (chess.getColor() == enemyColor) {
                            if (chess.getLocation().equals(new Point(to.getX() + dx[dir], to.getY() + dy[dir])) && chess instanceof Soldier) {
                                return false; //过河兵
                            }
                            if (blockCount == 0 && (chess instanceof Car || chess instanceof King)) {
                                return false; // 车或将可以直接攻击将
                            }
                            if (blockCount == 1 && chess instanceof Cannon) {
                                return false; // 炮必须隔一个子才可以攻击
                            }
                        }
                        blockCount++;
                    }
                }
                nx += dx[dir];
                ny += dy[dir];
            }
        }

        // 敌方马攻击检测

        // 蹩马脚相对将的位置
        for (Map.Entry<Point, List<Point>> entry : LEG_TO_HORSE_MAP.entrySet()) {
            Point legPoint = entry.getKey();
            int legX = legPoint.getX() + to.getX();
            int legY = legPoint.getY() + to.getY();
            List<Point> possibleHorses = entry.getValue();
            if (isValid(legX, legY) && board[legX][legY] != null) {
                continue;
            }
            for (Point hors : possibleHorses) {
                int horseX = to.getX() + hors.getX();
                int horseY = to.getY() + hors.getY();
                if (isValid(horseX, horseY)) {
                    Chess c = board[horseX][horseY];
                    if (c != null && c.getColor() == enemyColor && c instanceof Horse) {
                        return false;
                    }
                }
            }
        }

        return true; // 移动合法
    }


//    private static boolean ExceptKingWillExposeOwnKing(Chess[][] board, ChessColor selfColor, Point from, Point to) {
//        ChessColor enemyColor = (selfColor == ChessColor.R ? ChessColor.B : ChessColor.R);
//
//        if (from.getX() == kingPos.getX()) {
//            if (isLineExposed(board, kingPos, from, to, true, enemyColor, true)) {
//                return false;
//            }
//        }
//
//        if (from.getY() == kingPos.getY()) {
//            if (isLineExposed(board, kingPos, from, to, false, enemyColor, true)) {
//                return false;
//            }
//        }
//
//        if (to.getX() == kingPos.getX()) {
//            if (isLineExposed(board, kingPos, from, to, true, enemyColor, false)) {
//                return false;
//            }
//        }
//
//        if (to.getY() == kingPos.getY()) {
//            if (isLineExposed(board, kingPos, from, to, false, enemyColor, false)) {
//                return false;
//            }
//        }
//
//        // 3. 是否拆掉敌马蹩马脚
//        return !exposesHorseAttack(from, to, board, selfColor, enemyColor);
//    }

    /**
     * 检测将所在的行或列是否暴露给敌方车、炮、将，包含：
     * 1. 移动前是否清空己方防线导致暴露
     * 2. 移动后是否成为炮架
     *
     * @param board      棋盘
     * @param kingPos    我方将的位置
     * @param from       当前移动棋子原位置
     * @param to         当前移动棋子目标位置
     * @param isRow      true=同行检测，false=同列检测
     * @param enemyColor 敌方颜色
     * @return 是否暴露给敌方攻击
     */
    private static boolean isLineExposed(Chess[][] board, Point kingPos, Point from, Point to, boolean isRow, ChessColor enemyColor, boolean before) {
        int fixed = isRow ? kingPos.getX() : kingPos.getY(); // 固定行或列
        int kingIndex = isRow ? kingPos.getY() : kingPos.getX();
        int fromIndex = isRow ? from.getY() : from.getX();
        int toIndex = isRow ? to.getY() : to.getX();
        int start = (fromIndex < kingIndex) ? kingIndex - 1 : kingIndex + 1;
        int end = (fromIndex < kingIndex) ? -1 : (isRow ? 9 : 10);
        int step = (fromIndex < kingIndex) ? -1 : 1;
        if (before) {
            if (isRow ? from.getX() == to.getX() : from.getY() == to.getY()) {
                return false;
            }
            // 判断移动前是否暴露
            int blockCount = 0;
            for (int i = start; i != end; i += step) {
                Chess chess = isRow ? board[fixed][i] : board[i][fixed];
                if (chess != null) {
                    // 忽略当前移动棋子
                    if (chess.getLocation().equals(from)) continue;

                    if (blockCount > 1) break;

                    if (blockCount == 0 && chess.getColor() == enemyColor && (chess instanceof Car || chess instanceof King)) {
                        return true; // 车或将直接威胁
                    }
                    if (blockCount == 1 && chess.getColor() == enemyColor && chess instanceof Cannon) {
                        return true; // 炮越一个子威胁
                    }
                    blockCount++;
                }
            }
        } else {
            start = (toIndex < kingIndex) ? kingIndex - 1 : kingIndex + 1;
            end = (toIndex < kingIndex) ? -1 : (isRow ? 9 : 10);
            step = (toIndex < kingIndex) ? -1 : 1;

            // 判断移动后是否成为炮架
            for (int i = start; i != end; i += step) {
                Chess chess = isRow ? board[fixed][i] : board[i][fixed];
                if (chess != null) {
                    // 只判断炮，前面不能再有阻挡
                    if (chess.getLocation().equals(from) || chess.getLocation().equals(to)) {
                        continue;
                    }
                    int chessPoint = isRow ? chess.getLocation().getY() : chess.getLocation().getX();
                    boolean locationRight = (toIndex < kingIndex) ? chessPoint < toIndex : chessPoint > toIndex;
                    if (locationRight && chess.getColor() == enemyColor && chess instanceof Cannon) {
                        return true; // 成为炮架
                    }
                    break; // 第一个非空棋子不是炮，直接退出
                }
            }
        }

        return false;
    }

    /**
     * 判断移动前的位置是否是蹩马脚，从而暴露给敌方马攻击
     *
     * @param from
     * @param to         移动
     * @param board      当前棋盘
     * @param selfColor  我方颜色
     * @param enemyColor 敌方颜色
     * @return 是否暴露给敌马攻击
     */
//    private static boolean exposesHorseAttack(Point from, Point to, Chess[][] board, ChessColor selfColor, ChessColor enemyColor) {
//
//        Point kingPosition = KingPositionUtil.getKingPosition(selfColor);
//
//        int kingX = kingPosition.getX();
//
//        int kingY = kingPosition.getY();
//
//        int fromX = from.getX();
//
//        int fromY = from.getY();
//
//        // 蹩马脚相对将的位置
//        int dxLeg = fromX - kingX;
//        int dyLeg = fromY - kingY;
//
//        Point legPoint = new Point(dxLeg, dyLeg);
//        List<Point> possibleHorses = LEG_TO_HORSE_MAP.get(legPoint);
//
//        if (possibleHorses == null) {
//            // 当前位置不是蹩马脚，不会暴露给敌马
//            return false;
//        }
//
//        for (Point horseRel : possibleHorses) {
//            int horseX = kingX + horseRel.getX();
//            int horseY = kingY + horseRel.getY();
//
//            // 判断马位置是否有效
//            if (isValid(horseX, horseY)) {
//                Chess c = board[horseX][horseY];
//                if (c != null && !to.equals(c.getLocation()) && c.getColor() == enemyColor && c instanceof Horse) {
//                    // 有敌马，暴露给敌马攻击
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    /**
     * 判断坐标是否合法
     */
    private static boolean isValid(int x, int y) {
        return x >= 0 && x < 10 && y >= 0 && y < 9;
    }


}
