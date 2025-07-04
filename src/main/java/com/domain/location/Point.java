package com.domain.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author rich
 * @date 2025/6/21
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Point {
    private int x;
    private int y;

    public Point(Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }


    public Point(int[] pos) {
        this.x = pos[0];
        this.y = pos[1];
    }

}
