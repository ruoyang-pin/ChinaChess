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
public class Car extends Chess {


    public Car(int x, int y, ChessColor color) {
        super(x, y, color);
        setName("車");
        this.type= ChessType.CAR;

    }



    @Override
    public void getValidMoves(Chess[][] board, TreeSet<Move> moves, Integer depth, Integer step) {
        int x = this.x;
        int y = this.y;
        // 上下左右
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d], ny = y + dy[d];
            while (nx >= 0 && nx < 10 && ny >= 0 && ny < 9) {
                Chess target = board[nx][ny];
                if (target == null) {
                    addMoveCheckWithMove(moves, nx, ny, x, y, board, this.getColor(), depth, step);
                } else {
                    if (target.getColor() != this.getColor()) {
                        addMoveCheckWithMove(moves, nx, ny, x, y, board, this.getColor(), depth, step);// 可吃子
                    }
                    break;
                }

                nx += dx[d];
                ny += dy[d];
            }
        }
    }


}
