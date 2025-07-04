package com.domain.chess;

import com.domain.location.Move;
import com.enumerates.ChessColor;
import com.enums.ChessType;

import java.util.TreeSet;

/**
 * @author rich
 * @date 2025/6/21
 * @description
 */
public class Soldier extends Chess {


    public Soldier(int x, int y, ChessColor color) {
        super(x, y, color);
        setName("卒");
        this.type= ChessType.SOLDIER;

    }




    @Override
    public void getValidMoves(Chess[][] board, TreeSet<Move> moves, Integer depth, Integer step) {
        int x = this.x;
        int y = this.y;

        // 前进方向
        ChessColor color = super.getColor();
        int forward = color == ChessColor.R ? -1 : 1;
        int nx = x + forward;
        if (nx >= 0 && nx < 10) {
            Chess front = board[nx][y];
            if (front == null || front.getColor() != color) {
                addMoveCheckWithMove(moves, nx, y, x, y, board, this.getColor(), depth, step);
            }
        }

        // 过河后左右可走
        boolean crossedRiver = (color == ChessColor.R && x <= 4) || (color == ChessColor.B && x >= 5);
        if (crossedRiver) {
            for (int dy = -1; dy <= 1; dy += 2) {
                int ny = y + dy;
                if (ny >= 0 && ny < 9) {
                    Chess side = board[x][ny];
                    if (side == null || side.getColor() != color) {
                        addMoveCheckWithMove(moves, x, ny, x, y, board, this.getColor(), depth, step);
                    }
                }
            }
        }
    }


}
