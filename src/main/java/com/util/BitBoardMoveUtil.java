package com.util;

import com.domain.chess.BitBoard;
import com.enumerates.ChessColor;
import com.enums.ChessType;

public class BitBoardMoveUtil {

    public static void applyMove(BitBoard bitBoard, int fromIndex, int toIndex, ChessType pieceType, ChessColor color, boolean isCaptured, int capturedType) {
        //吃子删除原来棋子的位置
        if (isCaptured) {
            BitBoardUtil.clear(bitBoard.allPieces, toIndex);
            if (color == ChessColor.R) BitBoardUtil.clear(bitBoard.blackPieces, toIndex);
            else BitBoardUtil.clear(bitBoard.redPieces, toIndex);
            long[] capturedBoard = getPieceBoard(bitBoard, ChessType.getByValue(capturedType));
            BitBoardUtil.clear(capturedBoard, toIndex);
        }
        // 清除原位置
        BitBoardUtil.clear(bitBoard.allPieces, fromIndex);
        if (color == ChessColor.R) BitBoardUtil.clear(bitBoard.redPieces, fromIndex);
        else BitBoardUtil.clear(bitBoard.blackPieces, fromIndex);

        // 移动棋子
        BitBoardUtil.set(bitBoard.allPieces, toIndex);
        if (color == ChessColor.R) BitBoardUtil.set(bitBoard.redPieces, toIndex);
        else BitBoardUtil.set(bitBoard.blackPieces, toIndex);
        // 移动棋子类型表
        long[] targetBoard = getPieceBoard(bitBoard, pieceType);
        BitBoardUtil.move(targetBoard, fromIndex, toIndex);
        //移动列视图
        BitBoardUtil.updateColumnOccupy(bitBoard.columnOccupy, fromIndex, toIndex);
        //移动行视图
        BitBoardUtil.updateRowOccupy(bitBoard.rowOccupy, fromIndex, toIndex);

    }

    public static void undoMove(BitBoard bitBoard, int fromIndex, int toIndex, ChessType pieceType, ChessColor color, boolean isCaptured, int capturedType) {
        // 移回原位
        BitBoardUtil.clear(bitBoard.allPieces, toIndex);
        BitBoardUtil.set(bitBoard.allPieces, fromIndex);
        BitBoardUtil.updateColumnOccupy(bitBoard.columnOccupy, toIndex, fromIndex);
        BitBoardUtil.updateRowOccupy(bitBoard.rowOccupy, toIndex, fromIndex);
        if (color == ChessColor.R) {
            BitBoardUtil.clear(bitBoard.redPieces, toIndex);
            BitBoardUtil.set(bitBoard.redPieces, fromIndex);
        } else {
            BitBoardUtil.clear(bitBoard.blackPieces, toIndex);
            BitBoardUtil.set(bitBoard.blackPieces, fromIndex);
        }

        long[] targetBoard = getPieceBoard(bitBoard, pieceType);
        BitBoardUtil.move(targetBoard, toIndex, fromIndex);
        // 恢复被吃的棋子
        if (isCaptured) {
            BitBoardUtil.set(bitBoard.allPieces, toIndex);
            BitBoardUtil.setColumnView(bitBoard.columnOccupy, toIndex);
            BitBoardUtil.setRowView(bitBoard.rowOccupy, toIndex);
            if (color == ChessColor.R) BitBoardUtil.set(bitBoard.blackPieces, toIndex);
            else BitBoardUtil.set(bitBoard.redPieces, toIndex);
            long[] capturedBoard = getPieceBoard(bitBoard, ChessType.getByValue(capturedType));
            BitBoardUtil.set(capturedBoard, toIndex);
        }
    }

    private static long[] getPieceBoard(BitBoard bitBoard, ChessType chessType) {
        long[] board = bitBoard.pieceBoards[chessType.ordinal()];
        if (board == null) {
            throw new IllegalArgumentException("未知棋子类型: " + chessType);
        }
        return board;
    }


    // 判断当前位置是否有己方棋子（基于位运算）
    public static boolean isOwnPieceAt(BitBoard bitBoard, int pos, ChessColor color) {
        long[] ownPieces = (color == ChessColor.R) ? bitBoard.redPieces : bitBoard.blackPieces;
        int part = pos >>> 6;
        int offset = pos & 63;
        return ((ownPieces[part] >>> offset) & 1L) != 0;
    }


    public static BitBoard init() {
        BitBoard bitBoard = new BitBoard();
        // 红方棋子
        placePiece(bitBoard, 9, 0, ChessType.CAR, ChessColor.R);
        placePiece(bitBoard, 9, 1, ChessType.HORSE, ChessColor.R);
        placePiece(bitBoard, 9, 2, ChessType.PRIME_MINISTER, ChessColor.R);
        placePiece(bitBoard, 9, 3, ChessType.GUARDS, ChessColor.R);
        placePiece(bitBoard, 9, 4, ChessType.KING, ChessColor.R);
        placePiece(bitBoard, 9, 5, ChessType.GUARDS, ChessColor.R);
        placePiece(bitBoard, 9, 6, ChessType.PRIME_MINISTER, ChessColor.R);
        placePiece(bitBoard, 9, 7, ChessType.HORSE, ChessColor.R);
        placePiece(bitBoard, 9, 8, ChessType.CAR, ChessColor.R);
        placePiece(bitBoard, 7, 1, ChessType.CANNON, ChessColor.R);
        placePiece(bitBoard, 7, 7, ChessType.CANNON, ChessColor.R);
        placePiece(bitBoard, 6, 0, ChessType.SOLDIER, ChessColor.R);
        placePiece(bitBoard, 6, 2, ChessType.SOLDIER, ChessColor.R);
        placePiece(bitBoard, 6, 4, ChessType.SOLDIER, ChessColor.R);
        placePiece(bitBoard, 6, 6, ChessType.SOLDIER, ChessColor.R);
        placePiece(bitBoard, 6, 8, ChessType.SOLDIER, ChessColor.R);

        // 黑方棋子
        placePiece(bitBoard, 0, 0, ChessType.CAR, ChessColor.B);
        placePiece(bitBoard, 0, 1, ChessType.HORSE, ChessColor.B);
        placePiece(bitBoard, 0, 2, ChessType.PRIME_MINISTER, ChessColor.B);
        placePiece(bitBoard, 0, 3, ChessType.GUARDS, ChessColor.B);
        placePiece(bitBoard, 0, 4, ChessType.KING, ChessColor.B);
        placePiece(bitBoard, 0, 5, ChessType.GUARDS, ChessColor.B);
        placePiece(bitBoard, 0, 6, ChessType.PRIME_MINISTER, ChessColor.B);
        placePiece(bitBoard, 0, 7, ChessType.HORSE, ChessColor.B);
        placePiece(bitBoard, 0, 8, ChessType.CAR, ChessColor.B);
        placePiece(bitBoard, 2, 1, ChessType.CANNON, ChessColor.B);
        placePiece(bitBoard, 2, 7, ChessType.CANNON, ChessColor.B);
        placePiece(bitBoard, 3, 0, ChessType.SOLDIER, ChessColor.B);
        placePiece(bitBoard, 3, 2, ChessType.SOLDIER, ChessColor.B);
        placePiece(bitBoard, 3, 4, ChessType.SOLDIER, ChessColor.B);
        placePiece(bitBoard, 3, 6, ChessType.SOLDIER, ChessColor.B);
        placePiece(bitBoard, 3, 8, ChessType.SOLDIER, ChessColor.B);
        return bitBoard;
    }


    private static void placePiece(BitBoard bitBoard, int row, int col, ChessType pieceType, ChessColor color) {
        int index = BitBoardUtil.getIndex(row, col);

        // 更新全局位图
        BitBoardUtil.set(bitBoard.allPieces, index);

        // 更新颜色位图
        if (color == ChessColor.R) {
            BitBoardUtil.set(bitBoard.redPieces, index);
        } else {
            BitBoardUtil.set(bitBoard.blackPieces, index);
        }
        //设置列视图
        BitBoardUtil.setColumnView(bitBoard.columnOccupy, index);
        //设置行视图
        BitBoardUtil.setRowView(bitBoard.rowOccupy, index);

        // 更新棋子类型位图
        long[] targetBoard = getPieceBoard(bitBoard, pieceType);
        BitBoardUtil.set(targetBoard, index);
    }

}
