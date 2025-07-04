package com.test;

import com.domain.chess.BitBoard;
import com.domain.location.SearchResult;
import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.util.BitBoardMoveUtil;
import com.util.BitBoardUtil;
import com.util.TranspositionTable;
import com.util.move.*;
import com.util.pool.SearchResultPoolManager;

import java.util.concurrent.atomic.AtomicInteger;

public class MoveGeneratorTest {

    //                ints.addAndGet(CarMoveGenerator.generateCarMoves(bitBoard, 0, ChessColor.B, ChessType.CAR.ordinal(), moves, 0));  17
    //                nts.addAndGet(KingMoveGenerator.generateKingMoves(bitBoard, 4, ChessColor.B, ChessType.KING.ordinal(), moves, 0)); 34
    //                ints.addAndGet(FullMoveGenerator.generateHorseMoves(bitBoard, 1, ChessColor.B, ChessType.HORSE.ordinal(), moves, 0)); 71
    //                ints.addAndGet(CannonMoveArrayGenerator.generateCannonMoves(bitBoard, 19, ChessColor.B, ChessType.CANNON.ordinal(), moves, 0));  92
    //                ints.addAndGet(FullMoveGenerator.generatePrimeMinisterMoves(bitBoard, 2, ChessColor.B, ChessType.PRIME_MINISTER.ordinal(), moves, 0));  35
    //                ints.addAndGet(FullMoveGenerator.generateGuardsMoves(bitBoard, 3, ChessColor.B, ChessType.GUARDS.ordinal(), moves, 0));  25
    //                ints.addAndGet(FullMoveGenerator.generateSoldierMoves(bitBoard, 27, ChessColor.B, ChessType.SOLDIER.ordinal(), moves, 0)); 20


    /**
     * 计算代码执行时间，单位纳秒
     *
     * @param task 要执行的代码块
     * @return 代码执行耗时（纳秒）
     */
    public static long measureNanoTime(Runnable task) {
        long start = System.nanoTime();
        task.run();
        long end = System.nanoTime();
        return end - start;
    }

    // 测试示例
    public static void main(String[] args) {
        int[] moves = new int[256];
        BitBoard bitBoard = BitBoardMoveUtil.init();
        AtomicInteger ints = new AtomicInteger();
        int ownKingPos = BitBoardUtil.findKingPos(bitBoard, ChessColor.B);
        int enemyKingPos = BitBoardUtil.findKingPos(bitBoard, ChessColor.R);
        boolean[] occupied = new boolean[90];

        byte[] typeBoard = new byte[90];
        long[] own = bitBoard.blackPieces;
        long[] enemy = bitBoard.redPieces;

        BoardStateBuilder.getInstance().build(bitBoard);
        for (int i = 0; i < 64; i++) {
            if ((own[0] | enemy[0] & (1L << i)) != 0) occupied[i] = true;
        }
        for (int i = 0; i < 26; i++) {
            if ((own[1] | enemy[1] & (1L << i)) != 0) occupied[i + 64] = true;
        }
        ZobristHash.computeFullHash(bitBoard, ChessColor.R);
        long hash = ZobristHash.currentHash();
        TranspositionTable instance = TranspositionTable.getInstance();
        ChessType byValue = ChessType.getByValue(1);
        SearchResult acquire = SearchResultPoolManager.acquire();
        BoardStateBuilder builder = BoardStateBuilder.getInstance();
        long duration = measureNanoTime(() -> {
            // 这里放你要测量的代码
            for (int i = 0; i < 1000000; i++) {
                ints.addAndGet(CarMoveGenerator.generateCarMoves(0, ChessColor.B, ChessType.CAR.ordinal(), moves, builder, false, ChessColor.R, ownKingPos, enemyKingPos));
//                Pair<int[], Integer> pair = MoveGenerator.generateAllMoves(bitBoard, ChessColor.R, builder, 0,false);
//                ints.addAndGet(pair.getLeft()[0]);
//                SearchResult minimax = AiChooseV2.minimax(bitBoard, 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
//                        false, ChessColor.R, new int[]{1}, 1, null, 1, 0);
//                int evaluate = FastEvaluate.evaluate(bitBoard, ChessColor.R, 0);
//                boolean lineExposedAfterMove = MoveGeneratorCheck.isLineExposedAfterMove(0, 9, ownKingPos, ChessColor.R, builder);
//                ints.addAndGet(lineExposedAfterMove);
//                SearchResultPoolManager.release(minimax);
            }
        });
        System.out.println("代码执行耗时: " + duration / 1000000 + " 纳秒");
    }


}
