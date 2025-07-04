package com.util;

import com.domain.location.Move;

import java.util.TreeSet;

/**
 * @author rich
 * @date 2025/6/26
 * @description
 */
public class TreeSetUtil {


    public static TreeSet<Move> getInstance() {
        // 定义比较器，按分数从大到小排序
        // 初始化
        return new TreeSet<>((m1, m2) -> {
            int cmp = Integer.compare(m2.getScore(), m1.getScore()); // 分数降序
            if (cmp != 0) return cmp;

            // 如果分数相同，比较棋子位置避免去重
            cmp = Integer.compare(m1.getChess().getLocation().getX(), m2.getChess().getLocation().getX());
            if (cmp != 0) return cmp;

            cmp = Integer.compare(m1.getChess().getLocation().getY(), m2.getChess().getLocation().getY());
            if (cmp != 0) return cmp;

            // 比较目标位置
            cmp = Integer.compare(m1.getMoveTo().getX(), m2.getMoveTo().getX());
            if (cmp != 0) return cmp;

            return Integer.compare(m1.getMoveTo().getY(), m2.getMoveTo().getY());
        });
    }
}
