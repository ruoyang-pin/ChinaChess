package com.domain.location;

import com.domain.chess.Chess;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author rich
 * @date 2025/6/22
 * @description
 */
@Getter
@Setter
@AllArgsConstructor
public class Move {

    private Chess chess;

    private Point moveTo;

    private Integer score;

    public Move(Chess chess, Point moveTo) {
        this.chess = chess;
        this.moveTo = moveTo;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Move)) {
            return false;
        }
        return this.getChess().equals(((Move) o).getChess()) && this.moveTo.equals(((Move) o).getMoveTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(chess, moveTo);
    }


}
