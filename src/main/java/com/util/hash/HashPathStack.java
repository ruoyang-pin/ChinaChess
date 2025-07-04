package com.util.hash;

import static com.service.AiChooseV2.MAX_DEPTH;

public class HashPathStack {

    private final long[] stack;  // 存储当前路径的哈希值

    private static final ThreadLocal<HashPathStack> threadLocalPool = ThreadLocal.withInitial(HashPathStack::new);
    private int[] currentPath;

    public HashPathStack() {
        // 预留容量大于maxDepth的4倍，降低冲突
        this.stack = new long[MAX_DEPTH + 1];
        this.currentPath = new int[MAX_DEPTH + 1];
    }

    public int[] getCurrentPath() {
        return currentPath;
    }

    public static HashPathStack getInstance() {
        return threadLocalPool.get();
    }

    public void push(long hash, int plv,int move) {
        stack[plv] = hash;
        currentPath[plv]=move;
    }

    public boolean contains(long hash, int length) {
        int count = 0;
        for (int i = 0; i < length; i++) {
            if (stack[i] == hash) {
                count++;
            }
            if(count >= 2){
                return true;
            }
        }
        return false;
    }

}
