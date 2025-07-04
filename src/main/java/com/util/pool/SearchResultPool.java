package com.util.pool;

import com.domain.location.SearchResult;

public class SearchResultPool {

    private final SearchResult[] pool;
    private int index = 0;

    public SearchResultPool(int size) {
        pool = new SearchResult[size];
        for (int i = 0; i < size; i++) {
            pool[i] = new SearchResult();
        }
    }

    /**
     * 申请一个未被占用的对象，线程内单线程不需加锁
     */
    public SearchResult acquire() {
        int count = 0;
        while (true) {
            if (index >= pool.length) {
                index = 0;
            }
            SearchResult sr = pool[index++];
            if (!sr.isInUse()) {
                sr.setInUse(true);
                return sr;
            }
            count++;
            if (count > pool.length) {
                System.out.println("消耗完毕");
            }
        }

    }

    /**
     * 归还对象，线程内单线程无竞争
     */
    public void release(SearchResult sr) {
        sr.reset();
    }

    /**
     * 批量重置所有对象（一般搜索开始时调用）
     */
    public void reset() {
        for (int i = 0; i < index; i++) {
            pool[i].reset();
        }
        index = 0;
    }
}
