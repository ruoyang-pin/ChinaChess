package com.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rich
 * @date 2025/7/3
 * @description
 */
public class ShareAlpha {

    private static final AtomicInteger sharedAlpha = new AtomicInteger(Integer.MIN_VALUE);


    public static void updateAlpha(int eval) {
        int prevAlpha;
        do {
            prevAlpha = sharedAlpha.get();
            if (eval <= prevAlpha) break;
        } while (!sharedAlpha.compareAndSet(prevAlpha, eval));
    }

    public static int getSharedAlpha() {
        return sharedAlpha.intValue();
    }

    public static void reset() {
        sharedAlpha.set(Integer.MIN_VALUE);
    }

}
