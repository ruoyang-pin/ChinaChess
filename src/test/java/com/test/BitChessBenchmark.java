package com.test;

import com.domain.chess.*;
import com.domain.location.SearchResult;
import com.enumerates.ChessColor;
import com.service.AiChooseV2;
import com.util.BitBoardMoveUtil;
import com.util.TranspositionTable;
import com.util.move.BoardStateBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * @author rich
 * @date 2025/6/27
 * @description
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 7, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)

public class BitChessBenchmark {


    public static BitBoard bitBoard;
    public static final ChessColor aiColor = ChessColor.R;

    @Setup
    public static void initBoard() {
//        // 初始化棋子：R 表红方，B 表黑方，后面是棋子名
//        // 黑方棋子（上方）
//        bitBoard = BitBoardMoveUtil.init();
//        ZobristHash.computeFullHash(bitBoard, aiColor);
//        TranspositionTable instance = TranspositionTable.getInstance();
//        BoardStateBuilder.getInstance().build(bitBoard);
    }


//    @Benchmark
//    public int[] testValue(Blackhole bh) {
//
//        BoardStateBuilder boardStateBuilder = new BoardStateBuilder();
//        boardStateBuilder.build(bitBoard);
//
//        int[] ints = MoveGenerator.generateAllMoves(bitBoard, ChessColor.R, boardStateBuilder);
//
//        for (int anInt : ints) {
//            //PASS
//        }
//        bh.consume(ints);
//        return ints;
//    }

//    @Benchmark
//    public int generateCarMoves(Blackhole bh) {
//
//        int[] moves = new int[256];
//        int ints =     FullMoveGenerator.generateCarMoves(bitBoard, 0, ChessColor.B, ChessType.CAR.ordinal(), moves, 0);
//
//        bh.consume(ints);
//        return ints;
//    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 3, time = 1)
    @Measurement(iterations = 5, time = 1)
    public SearchResult minimax(Blackhole bh) {
//        int[] count = new int[]{0};
//        long hash = ZobristHash.currentHash();
//        SearchResult minimax = AiChooseV2.minimax(bitBoard, 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
//                false, ChessColor.R, new int[]{1}, 1, null, 1, 0, hash);
//        bh.consume(minimax);
//        return minimax;
    }


}
