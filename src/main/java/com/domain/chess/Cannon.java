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
public class Cannon extends Chess {


    public Cannon(int x, int y, ChessColor color) {
        super(x, y, color);
        setName("炮");
        this.type= ChessType.CANNON;
    }


    @Override
    public void getValidMoves(Chess[][] board, TreeSet<Move> moves, Integer depth, Integer step) {
        int x = this.x;
        int y = this.y;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d], ny = y + dy[d];
            // 1. 不隔子走
            while (nx >= 0 && nx < 10 && ny >= 0 && ny < 9 && board[nx][ny] == null) {
                addMoveCheckWithMove(moves, nx, ny, x, y, board, this.getColor(), depth, step);
                nx += dx[d];
                ny += dy[d];
            }

            // 2. 隔一个子吃敌方
            nx += dx[d];
            ny += dy[d];
            while (nx >= 0 && nx < 10 && ny >= 0 && ny < 9) {
                if (board[nx][ny] != null) {
                    if (board[nx][ny].getColor() != this.getColor()) {
                        addMoveCheckWithMove(moves, nx, ny, x, y, board, this.getColor(), depth, step);
                    }

                    break;
                }
                nx += dx[d];
                ny += dy[d];
            }
        }
    }


}
