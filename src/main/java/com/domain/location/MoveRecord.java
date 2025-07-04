package com.domain.location;

import com.enumerates.ChessColor;
import com.enums.ChessType;

public class MoveRecord {
    public int fromIndex;
    public int toIndex;
    public ChessType pieceType;
    public ChessColor color;
    public boolean isCaptured;
    public int capturedType;

    public MoveRecord(int fromIndex, int toIndex, ChessType pieceType, ChessColor color, boolean isCaptured, int capturedType) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.pieceType = pieceType;
        this.color = color;
        this.isCaptured = isCaptured;
        this.capturedType = capturedType;
    }
}
