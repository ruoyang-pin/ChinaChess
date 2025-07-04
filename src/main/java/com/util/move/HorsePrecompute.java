package com.util.move;

import java.util.ArrayList;

public class HorsePrecompute {

    public static final int ROWS = 10;
    public static final int COLS = 9;

    // 马跳跃的8个方向相对坐标（马腿的相对坐标在跳跃方向上）
    // 格式：{跳跃终点行偏移, 跳跃终点列偏移, 马腿所在格子相对坐标（行偏移，列偏移）}
    private static final int[][] HORSE_MOVE_OFFSETS = {
            {-2, -1, -1, -1},  // 跳2行-1列，马腿在跳出方向前方1格
            {-2, 1, -1, 1},
            {-1, -2, -1, -1},
            {-1, 2, -1, 1},
            {1, -2, 1, -1},
            {1, 2, 1, 1},
            {2, -1, 1, -1},
            {2, 1, 1, 1}
    };

    // 预计算数组，预设为长度8的动态数组（实际跳跃数可能少于8）
    public static int[][] precomputedHorseJumps = new int[ROWS * COLS][];
    public static int[][] precomputedHorseLegs = new int[ROWS * COLS][];

    static {
        for (int pos = 0; pos < ROWS * COLS; pos++) {
            int row = pos / COLS;
            int col = pos % COLS;

            // 动态临时列表
            ArrayList<Integer> jumpsList = new ArrayList<>();
            ArrayList<Integer> legsList = new ArrayList<>();

            for (int[] offset : HORSE_MOVE_OFFSETS) {
                int jumpRow = row + offset[0];
                int jumpCol = col + offset[1];
                int legRow = row + offset[2];
                int legCol = col + offset[3];

                // 判断跳跃位置和马腿位置是否在棋盘范围内
                if (jumpRow >= 0 && jumpRow < ROWS && jumpCol >= 0 && jumpCol < COLS &&
                        legRow >= 0 && legRow < ROWS && legCol >= 0 && legCol < COLS) {
                    int jumpPos = jumpRow * COLS + jumpCol;
                    int legPos = legRow * COLS + legCol;

                    jumpsList.add(jumpPos);
                    legsList.add(legPos);
                }
            }

            // 转换成数组赋值
            precomputedHorseJumps[pos] = jumpsList.stream().mapToInt(i -> i).toArray();
            precomputedHorseLegs[pos] = legsList.stream().mapToInt(i -> i).toArray();
        }
    }
}
