package com.service;

import com.domain.location.Move;

public class TTEntry {

    public enum EntryType {EXACT, LOWERBOUND, UPPERBOUND}

    private final int value;
    private final int depth;
    private final EntryType type;
    private final Move bestMove;

    public TTEntry(int value, int depth, EntryType type, Move bestMove) {
        this.value = value;
        this.depth = depth;
        this.type = type;
        this.bestMove = bestMove;
    }

    public int getValue() {
        return value;
    }

    public int getDepth() {
        return depth;
    }

    public EntryType getType() {
        return type;
    }

    public Move getBestMove() {
        return bestMove;
    }
}
