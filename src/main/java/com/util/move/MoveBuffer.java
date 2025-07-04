package com.util.move;

/**
 * @author rich
 * @date 2025/7/3
 * @description
 */
public class MoveBuffer {


    public static final ThreadLocal<MoveBuffer> threadLocalMoveBuffer = ThreadLocal.withInitial(MoveBuffer::new);


    // 临时缓冲区
    private final int[] captureMoves;
    private int captureCount = 0;


    private final int[] quietMoves;
    private int quietCount = 0;

    public static final int CAPACITY = 512;
    public static final int PV_SCORE = 250;

    public MoveBuffer() {
        this.captureMoves = new int[128];
        this.quietMoves = new int[CAPACITY];
    }


    public static MoveBuffer getInstance() {
        return threadLocalMoveBuffer.get();
    }

    public int getCaptureCount() {
        return captureCount;
    }

    public int getQuietCount() {
        return quietCount;
    }

    public int[] getCaptureMoves() {
        return captureMoves;
    }

    public int[] getQuietMoves() {
        return quietMoves;
    }

    // 重置各阶段计数，开始新一轮生成
    public void reset() {
        captureCount = 0;
        quietCount = 0;
    }

    // 添加吃子走法
    public void addCaptureMove(int move, int pvMove) {
        addMove(move, pvMove, true);
    }

    // 添加空步
    public void addQuietMove(int move, int pvMove) {
        addMove(move, pvMove, false);
    }


    private void addMove(int move, int pvMove, boolean capture) {
        if (pvMove == move) {
            captureMoves[captureCount++] = BitMoveUtil.setScore(move, PV_SCORE);
        } else {
            if (capture) captureMoves[captureCount++] = move;
            else quietMoves[quietCount++] = move;
        }
    }


    // 对吃子走法排序（用你喜欢的排序算法，这里用插入排序示例）
    public void sortCaptureMoves(int start, int end) {
        for (int i = start + 1; i < end; i++) { // 从 start + 1 开始
            int key = captureMoves[i];
            int keyScore = key >>> 24;

            int j = i - 1;
            while (j >= start && (captureMoves[j] >>> 24) < keyScore) {
                captureMoves[j + 1] = captureMoves[j];
                j--;
            }
            captureMoves[j + 1] = key;
        }
    }

    public int[] getAllMove() {
        int[] result = new int[captureCount + quietCount];
        System.arraycopy(captureMoves, 0, result, 0, captureCount);
        System.arraycopy(quietMoves, 0, result, captureCount, quietCount);
        reset();
        return result;
    }

    public void setCount(int captureCount, int quietCount) {
        this.captureCount = captureCount;
        this.quietCount = quietCount;
    }

    public static int querySortScore(byte type) {
        int value;
        switch (type) {
            case 1:
                value = 200;
                break;  // KING
            case 2:
                value = 150;
                break; // CAR
            case 3:
                value = 100;
                break; // HORSE
            case 4:
                value = 120;
                break; // CANNON
            case 7:
                value = 50;
                break; // SOLDIER 简化版
            default:
                value = 70;
                break; // GUARDS, PRIME_MINISTER
        }
        return value;
    }


}

