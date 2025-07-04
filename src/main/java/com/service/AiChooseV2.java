package com.service;

/**
 * @author rich
 * @date 2025/6/23
 * @description
 */


import com.domain.chess.BitBoard;
import com.domain.location.SearchResult;
import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.ui.BoardPanel;
import com.util.BitBoardMoveUtil;
import com.util.BitBoardUtil;
import com.util.ShareAlpha;
import com.util.hash.BitBoardZobrist;
import com.util.hash.HashPathStack;
import com.util.move.*;
import com.util.pool.SearchResultPoolManager;
import com.util.score.FastEvaluate;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static com.util.BitBoardUtil.*;


public class AiChooseV2 {

    private static final int MAX_TT_SIZE = 10000000;


    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private static final CompletionService<Pair<SearchResult, Integer>> completionService = new ExecutorCompletionService<>(executor);

    public static final int CHECKMATE_SCORE = 100000;


    public static final int MAX_QUIESCENCE_DEPTH = 10;

    public static final int MAX_DEPTH = 7;

    public static void shutdown() {
        executor.shutdown();
    }

    @SneakyThrows
    public static Integer queryPoint(BitBoard bitBoard, ChessColor aiColor) {
        Integer bestMove = null;
        int pvMove = -1;
        //判断pv预测是否准确
        int lastPvMove = PV_PATH[0];
        if (lastPvMove != 0) {
            int from = BitMoveUtil.getFrom(lastPvMove);
            int to = BitMoveUtil.getTo(lastPvMove);
            if (BoardPanel.LAST_MOVE_POINT == from && movedPos == to) {
                pvMove = PV_PATH[1];
            }
        }
        long startTime = System.nanoTime();
        // 初始化
        BoardStateBuilder boardStateBuilder = BoardStateBuilder.getInstance();
        MoveBuffer moveBuffer = MoveBuffer.getInstance();
        MoveGenerator.generateAllMoves(bitBoard, aiColor, boardStateBuilder, false, moveBuffer, pvMove);
        ShareAlpha.reset();
        int[] allMove = moveBuffer.getAllMove();
        Integer[] allMoves = Arrays.stream(allMove).boxed().toArray(Integer[]::new);
        Arrays.sort(allMoves, (a, b) -> {
            int scoreA = BitMoveUtil.getScore(a);
            int scoreB = BitMoveUtil.getScore(b);
            return Integer.compare(scoreB, scoreA); // 降序
        });
        List<Future<Pair<SearchResult, Integer>>> futures = new ArrayList<>();
        for (int move : allMoves) {
            int from = BitMoveUtil.getFrom(move);
            int to = BitMoveUtil.getTo(move);
            int chessTypeValue = BitMoveUtil.getChessType(move);
            int capturedTypeValue = BitMoveUtil.getCapturedType(move);
            ChessType chessType = ChessType.getByValue(chessTypeValue);
            boolean isCaptured = BitMoveUtil.isCapture(move);
            BitBoardMoveUtil.applyMove(bitBoard, from, to, chessType, aiColor, isCaptured, capturedTypeValue);
            boardStateBuilder.applyMove(from, to, chessTypeValue, aiColor);
            BitBoard boardCopy = bitBoard.deepClone();
            int[] count = new int[]{0};
            int finalPvMove = pvMove;
            futures.add(completionService.submit(() -> {
                try {
                    BoardStateBuilder.getInstance().build(boardCopy);
                    long hash = BitBoardZobrist.getInstance().computeHash(bitBoard);
                    HashPathStack.getInstance().push(hash, 0, move);
                    int sharedAlpha = ShareAlpha.getSharedAlpha();
                    SearchResult result = minimax(boardCopy, MAX_DEPTH, sharedAlpha, Integer.MAX_VALUE,
                            false, aiColor, count, 1, null, ALL_STEP + 1, hash, BitMoveUtil.equalsIgnoreScore(move, finalPvMove));
                    result.setCount(count);
                    int[] currentPath = result.getCurrentPath();
                    currentPath[0] = move;
                    SearchResult copy = result.copy();
                    ShareAlpha.updateAlpha(result.getScore());
                    SearchResultPoolManager.release(result);
                    return Pair.of(copy, move);
                } finally {
                    BoardStateBuilder.getInstance().remove();
                    BitBoardZobrist.remove();
                    MoveBuffer.getInstance().reset();
                }
            }));
            BitBoardMoveUtil.undoMove(bitBoard, from, to, chessType, aiColor, isCaptured, capturedTypeValue);
            boardStateBuilder.undoMove(from, to, isCaptured, capturedTypeValue, chessTypeValue, aiColor);
        }
        int bestScore = Integer.MIN_VALUE;
        SearchResult result = null;
        int resultCount = 0;
        for (int i = 0; i < futures.size(); i++) {
            Future<Pair<SearchResult, Integer>> future = completionService.take();
            Pair<SearchResult, Integer> pair;
            try {
                pair = future.get();
            } catch (CancellationException e) {
                // 该任务被取消，忽略本次，继续处理其他任务
                continue;
            } catch (ExecutionException e) {
                // 这里可以打印日志或重新抛异常，根据需求处理
                e.printStackTrace();
                continue;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                break; // 可以选择退出循环
            }
            Integer move = pair.getRight();
            SearchResult searchResult = pair.getLeft();
            int score = searchResult.getScore();
            resultCount += searchResult.getCount()[0];
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
                result = searchResult;
            }
            // 杀将立即终止其他任务
            if (score >= Integer.MAX_VALUE - 1) {
                for (Future<Pair<SearchResult, Integer>> f : futures) {
                    if (!f.isDone()) {
                        f.cancel(true);
                    }
                }
                break;
            }
        }

        long endTime = System.nanoTime();
        double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;

        if (result != null) {
            //记录pv步数
            Arrays.fill(PV_PATH, 0);
            int[] currentPath = result.getCurrentPath();
            int pathLength = result.getPathLength();
            System.arraycopy(currentPath, 1, PV_PATH, 0, pathLength - 1);
            System.out.printf("得分为%s  共计算%s个节点 花费%s秒%n", bestScore, resultCount, elapsedTimeInSeconds);
            PathStack.print(currentPath, pathLength);
        }
        return bestMove;
    }


    public static SearchResult minimax(BitBoard bitBoard, int depth, int alpha, int beta, boolean maximizing, ChessColor aiColor, int[] count, int plv, Boolean eatChess, Integer step, long hash, boolean pvExist) {
        count[0]++;
        ChessColor currentColor = maximizing ? aiColor : (aiColor == ChessColor.R ? ChessColor.B : ChessColor.R);
        int pvMove = -1;
        if (depth == 0) {
            if (Boolean.TRUE.equals(eatChess)) {
                return quiescenceSearch(bitBoard, alpha, beta, maximizing, aiColor, count, plv, 0, step + 1, pvExist);
            } else {
                SearchResult acquire = SearchResultPoolManager.acquire();
                acquire.setScore(FastEvaluate.evaluate(bitBoard, aiColor, step));
                acquire.setPathLength(plv);
                return acquire;
            }
        }
        BoardStateBuilder boardStateBuilder = BoardStateBuilder.getInstance();
        HashPathStack hashPathStack = HashPathStack.getInstance();
        //陷入循环 剪枝
        if (plv > 3 && hashPathStack.contains(hash, plv)) {
            SearchResult acquire = SearchResultPoolManager.acquire();
            acquire.setScore(maximizing ? CHECKMATE_SCORE : -CHECKMATE_SCORE);
            acquire.setPathLength(plv);
            return acquire;
        }
        if (pvExist) {
            pvMove = PV_PATH[plv + 1];
        }
        MoveBuffer moveBuffer = MoveBuffer.getInstance();
        int oldQuietCount = moveBuffer.getQuietCount();
        int oldCaptureCount = moveBuffer.getCaptureCount();
        int oldMoveCount = oldQuietCount + oldCaptureCount;
        // 初始化
        MoveGenerator.generateAllMoves(bitBoard, currentColor, boardStateBuilder, false, moveBuffer, pvMove);
        int captureCount = moveBuffer.getCaptureCount();
        int quietCount = moveBuffer.getQuietCount();
        //排序
        moveBuffer.sortCaptureMoves(oldCaptureCount, captureCount);
        int[] captureMoves = moveBuffer.getCaptureMoves();
        int[] quietMoves = moveBuffer.getQuietMoves();
        int movesCount = captureCount + quietCount;
        int addCaptureCount = captureCount - oldCaptureCount;
        int addMovesCount = movesCount - oldMoveCount;
        //困毙
        if (addMovesCount == 0) {
            SearchResult acquire = SearchResultPoolManager.acquire();
            acquire.setScore(maximizing ? -CHECKMATE_SCORE : CHECKMATE_SCORE);
            acquire.setPathLength(plv);
            return acquire;
        }
        int bestValue = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        SearchResult bestResult = SearchResultPoolManager.acquire();
        bestResult.setPathLength(plv);
        for (int j = 0; j < addMovesCount; j++) {
            int move = j < addCaptureCount ? captureMoves[oldCaptureCount + j] : quietMoves[oldQuietCount + j - addCaptureCount];
            int from = BitMoveUtil.getFrom(move);
            int to = BitMoveUtil.getTo(move);
            int chessTypeValue = BitMoveUtil.getChessType(move);
            int capturedTypeValue = BitMoveUtil.getCapturedType(move);
            boolean isCapture = BitMoveUtil.isCapture(move);
            ChessType chessType = ChessType.getByValue(chessTypeValue);
            if (isCapture && capturedTypeValue == ChessType.KING.getValue()) {
                bestResult.setScore(maximizing ? CHECKMATE_SCORE : -CHECKMATE_SCORE);
                int[] currentPath = bestResult.getCurrentPath();
                bestResult.setPathLength(plv + 1);
                currentPath[plv] = move;
                return bestResult;
            }
            BitBoardMoveUtil.applyMove(bitBoard, from, to, chessType, currentColor, isCapture, capturedTypeValue);
            boardStateBuilder.applyMove(from, to, chessTypeValue, currentColor);
            long newHash = BitBoardZobrist.getInstance().updateHash(hash, from, to, currentColor, chessType, isCapture, capturedTypeValue);
            hashPathStack.push(newHash, plv, move);
            SearchResult result = minimax(bitBoard, depth - 1, alpha, beta, !maximizing, aiColor, count, plv + 1, isCapture, step + 1, newHash, BitMoveUtil.equalsIgnoreScore(move, pvMove));
            int eval = result.getScore();
            boardStateBuilder.undoMove(from, to, isCapture, capturedTypeValue, chessTypeValue, currentColor);
            BitBoardMoveUtil.undoMove(bitBoard, from, to, chessType, currentColor, isCapture, capturedTypeValue);
            //重置索引
            moveBuffer.setCount(captureCount, quietCount);
            if (maximizing) {
                if (eval > bestValue) {
                    bestValue = eval;
                    result.getCurrentPath()[plv] = move;
                    SearchResultPoolManager.release(bestResult);
                    bestResult = result;
                } else SearchResultPoolManager.release(result);
                alpha = Math.max(alpha, eval);
            } else {
                if (eval < bestValue) {
                    bestValue = eval;
                    result.getCurrentPath()[plv] = move;
                    SearchResultPoolManager.release(bestResult);
                    bestResult = result;
                } else SearchResultPoolManager.release(result);
                beta = Math.min(beta, eval);
            }
            if (beta <= alpha) {
                break;
            }
            //尝试获取全局alpha
            if (plv == 1) {
                int sharedAlpha = ShareAlpha.getSharedAlpha();
                alpha = Math.max(sharedAlpha, alpha);
            }
        }
        return bestResult;
    }


    private static SearchResult quiescenceSearch(BitBoard board, int alpha, int beta, boolean maximizing, ChessColor aiColor, int[] count, int plv, int qDepth, int step, boolean pvExist) {
        count[0]++;
        if (qDepth > MAX_QUIESCENCE_DEPTH) {
            SearchResult acquire = SearchResultPoolManager.acquire();
            acquire.setScore(FastEvaluate.evaluate(board, aiColor, step));
            acquire.setPathLength(plv);
            return acquire;
        }

        ChessColor currentColor = maximizing ? aiColor : (aiColor == ChessColor.R ? ChessColor.B : ChessColor.R);
        int standPat = FastEvaluate.evaluate(board, aiColor, step);
        if (maximizing) {
            if (standPat >= beta) {
                SearchResult acquire = SearchResultPoolManager.acquire();
                acquire.setScore(standPat);
                acquire.setPathLength(plv);
                return acquire;
            }
            if (standPat > alpha) alpha = standPat;
        } else {
            if (standPat <= alpha) {
                SearchResult acquire = SearchResultPoolManager.acquire();
                acquire.setScore(standPat);
                acquire.setPathLength(plv);
                return acquire;
            }
            if (standPat < beta) beta = standPat;
        }
        int pvMove = -1;
        if (pvExist) {
            pvMove = PV_PATH[plv + 1];
        }
        BoardStateBuilder boardStateBuilder = BoardStateBuilder.getInstance();
        MoveBuffer moveBuffer = MoveBuffer.getInstance();
        int oldQuietCount = moveBuffer.getQuietCount();
        int oldCaptureCount = moveBuffer.getCaptureCount();
        int oldMoveCount = oldQuietCount + oldCaptureCount;
        // 初始化
        MoveGenerator.generateAllMoves(board, currentColor, boardStateBuilder, true, moveBuffer, pvMove);
        int captureCount = moveBuffer.getCaptureCount();
        int quietCount = moveBuffer.getQuietCount();
        //排序
//        moveBuffer.sortCaptureMoves(oldCaptureCount, captureCount);
        int[] captureMoves = moveBuffer.getCaptureMoves();
        int[] quietMoves = moveBuffer.getQuietMoves();
        int movesCount = captureCount + quietCount;
        int addCaptureCount = captureCount - oldCaptureCount;
        int addMovesCount = movesCount - oldMoveCount;
        int bestValue = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if (addMovesCount == 0) {
            SearchResult acquire = SearchResultPoolManager.acquire();
            acquire.setScore(FastEvaluate.evaluate(board, aiColor, step));
            acquire.setPathLength(plv);
            return acquire;
        }
        SearchResult bestResult = SearchResultPoolManager.acquire();
        for (int j = 0; j < addMovesCount; j++) {
            int move = j < addCaptureCount ? captureMoves[oldCaptureCount + j] : quietMoves[oldQuietCount + j - addCaptureCount];
            int from = BitMoveUtil.getFrom(move);
            int to = BitMoveUtil.getTo(move);
            int chessTypeValue = BitMoveUtil.getChessType(move);
            int capturedTypeValue = BitMoveUtil.getCapturedType(move);
            boolean isCapture = BitMoveUtil.isCapture(move);
            ChessType chessType = ChessType.getByValue(chessTypeValue);
            if (isCapture && capturedTypeValue == ChessType.KING.getValue()) {
                bestResult.setScore(maximizing ? CHECKMATE_SCORE : -CHECKMATE_SCORE);
                int[] currentPath = bestResult.getCurrentPath();
                bestResult.setPathLength(plv + 1);
                currentPath[plv] = move;
                return bestResult;
            }
            BitBoardMoveUtil.applyMove(board, from, to, chessType, currentColor, isCapture, capturedTypeValue);
            boardStateBuilder.applyMove(from, to, chessTypeValue, currentColor);
            SearchResult result = quiescenceSearch(board, alpha, beta, !maximizing, aiColor, count, plv + 1, qDepth + 1, step + 1, BitMoveUtil.equalsIgnoreScore(move, pvMove));
            int eval = result.getScore();
            boardStateBuilder.undoMove(from, to, isCapture, capturedTypeValue, chessTypeValue, currentColor);
            BitBoardMoveUtil.undoMove(board, from, to, chessType, currentColor, isCapture, capturedTypeValue);
            //重置索引
            moveBuffer.setCount(captureCount, quietCount);
            if (maximizing) {
                if (eval > bestValue) {
                    bestValue = eval;
                    result.getCurrentPath()[plv] = move;
                    SearchResultPoolManager.release(bestResult);
                    bestResult = result;
                } else SearchResultPoolManager.release(result);
                alpha = Math.max(alpha, eval);
            } else {
                if (eval < bestValue) {
                    bestValue = eval;
                    result.getCurrentPath()[plv] = move;
                    SearchResultPoolManager.release(bestResult);
                    bestResult = result;
                } else SearchResultPoolManager.release(result);
                beta = Math.min(beta, eval);
            }

            if (beta <= alpha) {
                break;
            }

        }
        return bestResult;
    }


}

