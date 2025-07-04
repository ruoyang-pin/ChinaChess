package com.util;

public class SplitMix64 {
    private long seed;

    public SplitMix64(long seed) {
        this.seed = seed;
    }

    public long nextLong() {
        long z = (seed += 0x9E3779B97F4A7C15L);
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
