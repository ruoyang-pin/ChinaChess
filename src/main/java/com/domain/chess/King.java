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
public class King extends Chess {


    public King(int x, int y, ChessColor color) {
        super(x, y, color);
        setName("將");
        this.type= ChessType.KING;

    }




    @Override
    public void getValidMoves(Chess[][] board, TreeSet<Move> moves, Integer depth, Integer step) {
        int x = this.x;
        int y = this.y;

        int[][] deltas = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        for (int[] d : deltas) {
            int nx = x + d[0], ny = y + d[1];
            // 越界保护
            if (nx < 0 || nx >= 10 || ny < 0 || ny >= 9) continue;
            if (ny < 3 || ny > 5) continue;
            if ((super.getColor() == ChessColor.R && nx < 7) || (super.getColor() == ChessColor.B && nx > 2)) continue;
            Chess target = board[nx][ny];
            if (target == null || target.getColor() != this.getColor()) {
                addMoveCheckWithMove(moves, nx, ny, x, y, board, this.getColor(), depth, step);
            }

        }
    }


}
