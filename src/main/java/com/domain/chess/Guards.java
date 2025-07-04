package com.domain.chess;

import com.domain.location.Move;
import com.enumerates.ChessColor;
import com.enums.ChessType;

import java.util.TreeSet;

import static com.constant.CommonConst.COLS;
import static com.constant.CommonConst.ROWS;

/**
 * @author rich
 * @date 2025/6/21
 * @description
 */
public class Guards extends Chess {


    public Guards(int x, int y, ChessColor color) {
        super(x, y, color);
        setName("仕");
        this.type= ChessType.GUARDS;

    }




    @Override
    public void getValidMoves(Chess[][] board, TreeSet<Move> moves, Integer depth, Integer step) {
        int x = this.x;
        int y = this.y;
        int[][] deltas = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1}
        };

        for (int[] d : deltas) {
            int nx = x + d[0];
            int ny = y + d[1];
            // 检查是否在棋盘范围内
            if (nx < 0 || nx >= ROWS) continue;
            if (ny < 0 || ny >= COLS) continue;
            // 限定九宫范围
            if (ny < 3 || ny > 5) continue;
            if ((super.getColor() == ChessColor.R && nx < 7) || (super.getColor() == ChessColor.B && nx > 2)) continue;
            Chess target = board[nx][ny];
            if (target == null || target.getColor() != this.getColor()) {
                addMoveCheckWithMove(moves, nx, ny, x, y, board, this.getColor(), depth, step);
            }
        }
    }


}
