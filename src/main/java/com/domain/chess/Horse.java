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
public class Horse extends Chess {

    public Horse(int x, int y, ChessColor color) {
        super(x, y, color);
        setName("馬");
        this.type= ChessType.HORSE;

    }




    @Override
    public void getValidMoves(Chess[][] board, TreeSet<Move> moves, Integer depth, Integer step) {
        int x = this.x;
        int y = this.y;

        int[][] deltas = {
                {-2, -1}, {-2, 1},
                {2, -1}, {2, 1},
                {-1, -2}, {1, -2},
                {-1, 2}, {1, 2}
        };
        for (int[] d : deltas) {
            int nx = x + d[0], ny = y + d[1];
            if (nx < 0 || nx >= 10 || ny < 0 || ny >= 9) continue;

            if (Math.abs(d[0]) == 2 && board[x + d[0] / 2][y] != null) continue;
            if (Math.abs(d[1]) == 2 && board[x][y + d[1] / 2] != null) continue;
            Chess target = board[nx][ny];
            if (target == null || target.getColor() != this.getColor()) {
                addMoveCheckWithMove(moves, nx, ny, x, y, board, this.getColor(), depth, step);
            }
        }
    }


}
