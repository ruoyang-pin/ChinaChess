
package com.util;

import com.service.TTEntry;

public class TranspositionTable {

    private final int size;  // 2的幂次方
    private final EntrySlot[] table;

    public static TranspositionTable instance = new TranspositionTable();

    public static TranspositionTable getInstance() {
        return instance;
    }

    public TranspositionTable(int sizePowerOfTwo) {
        if (Integer.bitCount(sizePowerOfTwo) != 1) {
            throw new IllegalArgumentException("size must be power of two");
        }
        this.size = sizePowerOfTwo;
        this.table = new EntrySlot[size];
        for (int i = 0; i < size; i++) {
            table[i] = new EntrySlot();
        }
    }

    public TranspositionTable() {
        // 2的幂次方
        this.size = 1 << 24;
        this.table = new EntrySlot[size];
        for (int i = 0; i < size; i++) {
            table[i] = new EntrySlot();
        }
    }

    private int index(long hash) {
        return (int) (hash) & (size - 1);
    }

    // 读时返回匹配hash且深度最大的Entry，没命中返回null
    public TTEntry get(long hash) {
        int idx = index(hash);
        return table[idx].get(hash);
    }

    // 写时根据深度替换较浅的Entry，或者覆盖空槽
    public void put(long hash, TTEntry entry) {
        int idx = index(hash);
        table[idx].put(hash, entry);
    }

    private static class EntrySlot {
        // 两个槽，volatile保证可见性
        private volatile TTEntryWithHash slot1;
        private volatile TTEntryWithHash slot2;

        TTEntry get(long hash) {
            TTEntryWithHash e1 = slot1;
            if (e1 != null && e1.hash == hash) return e1.entry;
            TTEntryWithHash e2 = slot2;
            if (e2 != null && e2.hash == hash) return e2.entry;
            return null;
        }

        void put(long hash, TTEntry entry) {
            TTEntryWithHash newEntry = new TTEntryWithHash(hash, entry);

            TTEntryWithHash current1 = slot1;
            TTEntryWithHash current2 = slot2;

            if (current1 == null) {
                slot1 = newEntry;
                return;
            }
            if (current2 == null) {
                slot2 = newEntry;
                return;
            }

            // 替换较浅的Entry，假设你TTEntry有getDepth()
            if (entry.getDepth() > current1.entry.getDepth()) {
                slot1 = newEntry;
            } else if (entry.getDepth() > current2.entry.getDepth()) {
                slot2 = newEntry;
            } else {
                // 如果都更浅，简单替换slot1（策略可改）
                slot1 = newEntry;
            }
        }

        private static class TTEntryWithHash {
            final long hash;
            final TTEntry entry;

            TTEntryWithHash(long hash, TTEntry entry) {
                this.hash = hash;
                this.entry = entry;
            }
        }
    }
}
