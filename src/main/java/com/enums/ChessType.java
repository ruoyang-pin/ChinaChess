package com.enums;

public enum ChessType {
    KING(1, "將"),
    CAR(2, "車"),
    HORSE(3, "馬"),
    CANNON(4, "炮"),
    PRIME_MINISTER(5, "相"),
    GUARDS(6, "仕"),
    SOLDIER(7, "卒");

    private final int value;
    private final String chineseName;

    ChessType(int value, String chineseName) {
        this.value = value;
        this.chineseName = chineseName;
    }

    public int getValue() {
        return value;
    }

    public String getChineseName() {
        return chineseName;
    }

    // ================== 超高速缓存 ================
    private static final ChessType[] CACHE = new ChessType[8];

    static {
        for (ChessType type : ChessType.values()) {
            CACHE[type.value] = type; // 数组下标从 1 开始
        }
    }

    public static ChessType getByValue(int value) {
        if (value <= 0 || value >= CACHE.length) {
            throw new IllegalArgumentException("非法棋子类型: " + value);
        }
        return CACHE[value];
    }

}
