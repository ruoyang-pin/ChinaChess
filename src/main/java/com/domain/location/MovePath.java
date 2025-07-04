package com.domain.location;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author rich
 * @date 2025/6/23
 * @description
 */
@Data
@AllArgsConstructor
public class MovePath {

    private String name;

    private Point origin;

    private Point target;


    public boolean equals(String name, int x1, int y1, int x2, int y2) {

        return name.equals(this.name) && this.origin.equals(new Point(x1, y1)) && this.target.equals(new Point(x2, y2));

    }


}
