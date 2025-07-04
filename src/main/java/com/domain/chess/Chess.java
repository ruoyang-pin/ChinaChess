package com.domain.chess;

import com.domain.location.Move;
import com.domain.location.Point;
import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.util.score.ComputeUtil;
import com.util.MoveSafetyUtil;
import com.util.TreeSetUtil;
import lombok.Data;

import java.util.TreeSet;

import static com.ui.BoardPanel.B_KING;
import static com.ui.BoardPanel.R_KING;
import static com.util.BitBoardUtil.ALL_STEP;


/**
 * @author rich
 * @date 2025/6/21
 * @description
 */
@Data
public abstract class Chess {

    private ChessColor color;

    private String name;

    public int x, y; // 直接存储坐标（protected允许子类访问）

    public ChessType type;

    public Chess(int x, int y, ChessColor color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }


    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    public Point getLocation() {
        return new Point(this.x, this.y);
    }


    public abstract void getValidMoves(Chess[][] board, TreeSet<Move> moves, Integer depth, Integer step);


    public boolean isKing() {
        return "將".equals(getName());
    }

    public String getCombinationName() {
        return this.getColor() + "-" + this.getName();
    }


    protected void addMoveCheckWithMove(TreeSet<Move> moves, int newX, int newY, int oldX, int oldY, Chess[][] board, ChessColor selfColor, Integer depth, Integer step) {
//        Point point = new Point(newX, newY);
//        if (MoveSafetyUtil.willExposeOwnKing(board, selfColor, new Point(oldX, oldY), point, this.isKing())) {
//            Move move = new Move(this, point);
//            //启发式算法打分
//            int score = ComputeUtil.evaluateMoveScore(move, board, selfColor, depth, step);
//            move.setScore(score);
//            moves.add(move);
//        }
    }

    public static boolean checkWillKillKing(Chess[][] board, ChessColor color) {
        Chess hostileKing = color == ChessColor.B ? R_KING : B_KING;
        for (Chess[] chessArr : board) {
            for (Chess chess : chessArr) {
                if (chess != null && chess.color == color) {
                    TreeSet<Move> moves = TreeSetUtil.getInstance();
                    chess.getValidMoves(board, moves, null, ALL_STEP);
                    for (Move move : moves) {
                        if (move.getMoveTo().equals(hostileKing.getLocation())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}
