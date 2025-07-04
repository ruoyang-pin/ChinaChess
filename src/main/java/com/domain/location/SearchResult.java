package com.domain.location;

import lombok.Data;

import static com.service.AiChooseV2.MAX_DEPTH;

@Data
public class SearchResult {

    private int score;

    private int[] count;

    private int[] currentPath;

    private int pathLength;

    private boolean inUse = false;


    public SearchResult() {
        // count 数组在构造时初始化，且固定大小
        this.currentPath = new int[20];
    }

    public void reset() {
        this.score = 0;
        this.inUse = false;
        this.pathLength = 0;
    }

    public SearchResult copy() {
        SearchResult result = new SearchResult();
        result.score = this.score;
        result.count = new int[this.count.length];
        System.arraycopy(this.count, 0, result.count, 0, this.count.length);
        result.currentPath = new int[this.currentPath.length];
        System.arraycopy(this.currentPath, 0, result.currentPath, 0, this.currentPath.length);
        result.pathLength = this.pathLength;
        result.inUse = this.inUse;
        return result;
    }

}
