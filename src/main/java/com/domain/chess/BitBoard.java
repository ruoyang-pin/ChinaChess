package com.domain.chess;

import com.enums.ChessType;

public class BitBoard {

    // 全局棋子位图
    public long[] allPieces = new long[2]; // allPieces[0]：棋盘前64格；allPieces[1]：棋盘剩余26格

    // 分颜色棋子位图
    public long[] redPieces = new long[2];
    public long[] blackPieces = new long[2];
    public long[] columnOccupy = new long[9]; // 列占位图
    public long[] rowOccupy = new long[10]; // 行占位图
    // 每个棋子类型的位图
    public long[] cars = new long[2];
    public long[] horses = new long[2];
    public long[] cannons = new long[2];
    public long[] kings = new long[2];
    public long[] soldiers = new long[2];
    public long[] guards = new long[2];
    public long[] ministers = new long[2];

    // 建议新增一个棋子数组映射
    public long[][] pieceBoards;

    public BitBoard() {
        // 初始化棋子数组
        pieceBoards = new long[ChessType.values().length][];
        pieceBoards[ChessType.CAR.ordinal()] = cars;
        pieceBoards[ChessType.HORSE.ordinal()] = horses;
        pieceBoards[ChessType.CANNON.ordinal()] = cannons;
        pieceBoards[ChessType.SOLDIER.ordinal()] = soldiers;
        pieceBoards[ChessType.PRIME_MINISTER.ordinal()] = ministers;
        pieceBoards[ChessType.GUARDS.ordinal()] = guards;
        pieceBoards[ChessType.KING.ordinal()] = kings;
    }


    public BitBoard deepClone() {
        BitBoard copy = new BitBoard();
        System.arraycopy(this.allPieces, 0, copy.allPieces, 0, 2);
        System.arraycopy(this.redPieces, 0, copy.redPieces, 0, 2);
        System.arraycopy(this.blackPieces, 0, copy.blackPieces, 0, 2);
        System.arraycopy(this.cars, 0, copy.cars, 0, 2);
        System.arraycopy(this.horses, 0, copy.horses, 0, 2);
        System.arraycopy(this.cannons, 0, copy.cannons, 0, 2);
        System.arraycopy(this.kings, 0, copy.kings, 0, 2);
        System.arraycopy(this.soldiers, 0, copy.soldiers, 0, 2);
        System.arraycopy(this.guards, 0, copy.guards, 0, 2);
        System.arraycopy(this.ministers, 0, copy.ministers, 0, 2);
        System.arraycopy(this.columnOccupy, 0, copy.columnOccupy, 0, 9);
        System.arraycopy(this.rowOccupy, 0, copy.rowOccupy, 0, 10);
        return copy;
    }
}
