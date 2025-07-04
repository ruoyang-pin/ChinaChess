package com.util;

import com.domain.chess.BitBoard;
import com.enums.ChessType;

public class ChessTypeUtil {

    public static int getChessTypeAt(BitBoard bitBoard, int row, int col) {
        int pos = row * 9 + col;
        int part = pos >>> 6;
        int offset = pos & 63;
        long mask = 1L << offset;

        if ((bitBoard.cars[part] & mask) != 0) return ChessType.CAR.getValue();
        if ((bitBoard.horses[part] & mask) != 0) return ChessType.HORSE.getValue();
        if ((bitBoard.cannons[part] & mask) != 0) return ChessType.CANNON.getValue();
        if ((bitBoard.soldiers[part] & mask) != 0) return ChessType.SOLDIER.getValue();
        if ((bitBoard.kings[part] & mask) != 0) return ChessType.KING.getValue();
        if ((bitBoard.guards[part] & mask) != 0) return ChessType.GUARDS.getValue();
        if ((bitBoard.ministers[part] & mask) != 0) return ChessType.PRIME_MINISTER.getValue();

        return -1; // 空格，找不到棋子
    }

    public static int getChessTypeAt(BitBoard bitBoard, int pos) {
        int part = pos >>> 6;
        int offset = pos & 63;
        long mask = 1L << offset;

        if ((bitBoard.cars[part] & mask) != 0) return ChessType.CAR.getValue();
        if ((bitBoard.horses[part] & mask) != 0) return ChessType.HORSE.getValue();
        if ((bitBoard.cannons[part] & mask) != 0) return ChessType.CANNON.getValue();
        if ((bitBoard.soldiers[part] & mask) != 0) return ChessType.SOLDIER.getValue();
        if ((bitBoard.kings[part] & mask) != 0) return ChessType.KING.getValue();
        if ((bitBoard.guards[part] & mask) != 0) return ChessType.GUARDS.getValue();
        if ((bitBoard.ministers[part] & mask) != 0) return ChessType.PRIME_MINISTER.getValue();

        return -1; // 空格，找不到棋子
    }


}
