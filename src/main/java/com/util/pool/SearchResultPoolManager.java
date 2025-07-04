package com.util.pool;

import com.domain.location.SearchResult;

public class SearchResultPoolManager {

    private static final int POOL_SIZE = 120;

    private static final ThreadLocal<SearchResultPool> threadLocalPool = ThreadLocal.withInitial(() -> new SearchResultPool(POOL_SIZE));


    public static SearchResult acquire() {
        return threadLocalPool.get().acquire();
    }

    public static void release(SearchResult sr) {
        threadLocalPool.get().release(sr);
    }

    public static void reset() {
        threadLocalPool.get().reset();
    }
}
