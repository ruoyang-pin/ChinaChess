package com.util.move;

import com.enumerates.ChessColor;

public final class CarMoveGenerator {

    public static void generateCarMoves(int pos, ChessColor color, int chessType, MoveBuffer moves,
                                        BoardStateBuilder boardStateBuilder, boolean onlyKill, ChessColor enemyColor, int ownKingPos, int enemyKingPos, int pvMove) {
        int ordinal = color.ordinal();
        int enemyOrdinal = enemyColor.ordinal();
        boolean[] occupied = boardStateBuilder.occupied;
        byte[] typeBoard = boardStateBuilder.typeBoard;
        byte[] colorBoard = boardStateBuilder.colorBoard;
        for (int d = 0; d < 4; d++) {
            int[] pathPoints = MoveGenerator.cannonPathPoints[pos][d];
            for (int p : pathPoints) {
                if (occupied[p]) { // 有子，己方或敌方都挡路
                    if (colorBoard[p] == enemyOrdinal && MoveGeneratorCheck.isLineExposedAfterMove(pos, p, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) { // 敌方棋子
                        //评分
                        byte type = typeBoard[p];
                        int score = MoveBuffer.querySortScore(type);
                        moves.addCaptureMove(BitMoveUtil.buildCaptureMove(pos, p, chessType, type, ordinal, score), pvMove);
                    }
                    break; // 己方或敌方都会阻挡
                } else if (!onlyKill && MoveGeneratorCheck.isLineExposedAfterMove(pos, p, ownKingPos, enemyColor, boardStateBuilder, enemyKingPos)) { // 空格
                    moves.addQuietMove(BitMoveUtil.buildMove(pos, p, chessType, ordinal, 0), pvMove);
                }
            }
        }

    }


}