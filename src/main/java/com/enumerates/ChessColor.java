package com.enumerates;

/**
 * @author rich
 * @date 2025/6/21
 * @description
 */
public enum ChessColor {
    B,
    R;

    public static ChessColor getByValue(int value) {
        return ChessColor.values()[value]; // 或者抛异常
    }
}
