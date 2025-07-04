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
public class PrimeMinister extends Chess {


    public PrimeMinister(int x, int y, ChessColor color) {
        super(x, y, color);
        setName("相");
        this.type= ChessType.PRIME_MINISTER;

    }




    @Override
    public void getValidMoves(Chess[][] board, TreeSet<Move> moves, Integer depth, Integer step) {
        int x = this.x;
        int y = this.y;

        int[][] deltas = {
                {-2, -2}, {-2, 2},
                {2, -2}, {2, 2}
        };

        for (int[] d : deltas) {
            int nx = x + d[0];
            int ny = y + d[1];

            if (nx < 0 || nx >= 10 || ny < 0 || ny >= 9) continue;

            // 象眼
            int ex = x + d[0] / 2;
            int ey = y + d[1] / 2;
            if (board[ex][ey] != null) continue;

            // 不可过河
            ChessColor color = super.getColor();
            if ((color == ChessColor.R && nx < 5) || (color == ChessColor.B && nx > 4)) continue;
            Chess target = board[nx][ny];
            if (target == null || target.getColor() != color) {
                addMoveCheckWithMove(moves, nx, ny, x, y, board, this.getColor(), depth, step);
            }
        }

    }


}
