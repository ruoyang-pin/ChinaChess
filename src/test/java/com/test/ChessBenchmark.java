package com.test;

import com.domain.chess.*;
import com.domain.location.SearchResult;
import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.service.AiChooseV2;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.ui.BoardPanel.*;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 7, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class ChessBenchmark {

    // 测试数据
    private List<Chess> testPieces;
    private int index;
    private Random random;

    public static Chess[][] board = new Chess[10][9]; // [行][列]

    public static void initBoard() {
        // 初始化棋子：R 表红方，B 表黑方，后面是棋子名
        // 黑方棋子（上方）
        board[0][0] = new Car(0, 0, ChessColor.B);
        board[0][1] = new Horse(0, 1, ChessColor.B);
        board[0][2] = new PrimeMinister(0, 2, ChessColor.B);
        board[0][3] = new Guards(0, 3, ChessColor.B);
        board[0][4] = new King(0, 4, ChessColor.B);
        B_KING = board[0][4];
        board[0][5] = new Guards(0, 5, ChessColor.B);
        board[0][6] = new PrimeMinister(0, 6, ChessColor.B);
        board[0][7] = new Horse(0, 7, ChessColor.B);
        board[0][8] = new Car(0, 8, ChessColor.B);
        board[2][1] = new Cannon(2, 1, ChessColor.B);
        board[2][7] = new Cannon(2, 7, ChessColor.B);
        board[3][0] = new Soldier(3, 0, ChessColor.B);
        board[3][2] = new Soldier(3, 2, ChessColor.B);
        board[3][4] = new Soldier(3, 4, ChessColor.B);
        board[3][6] = new Soldier(3, 6, ChessColor.B);
        board[3][8] = new Soldier(3, 8, ChessColor.B);

        // 红方棋子（下方）
        board[9][0] = new Car(9, 0, ChessColor.R);
        board[9][1] = new Horse(9, 1, ChessColor.R);
        board[9][2] = new PrimeMinister(9, 2, ChessColor.R);
        board[9][3] = new Guards(9, 3, ChessColor.R);
        board[9][4] = new King(9, 4, ChessColor.R);
        R_KING = board[9][4];
        board[9][5] = new Guards(9, 5, ChessColor.R);
        board[9][6] = new PrimeMinister(9, 6, ChessColor.R);
        board[9][7] = new Horse(9, 7, ChessColor.R);
        board[9][8] = new Car(9, 8, ChessColor.R);
        board[7][1] = new Cannon(7, 1, ChessColor.R);
        board[7][7] = new Cannon(7, 7, ChessColor.R);
        board[6][0] = new Soldier(6, 0, ChessColor.R);
        board[6][2] = new Soldier(6, 2, ChessColor.R);
        board[6][4] = new Soldier(6, 4, ChessColor.R);
        board[6][6] = new Soldier(6, 6, ChessColor.R);
        board[6][8] = new Soldier(6, 8, ChessColor.R);
    }


    // 测试原始评估方法
//    @Benchmark
//    public int testPieceValue(Blackhole bh) {
//        Chess piece = testPieces.get(index++ % 100);
//        int result = ComputeUtil.getPieceValue(piece, random.nextInt(200));
//        validateResult(result);
//        bh.consume(result);
//        return result;
//    }

    // 测试优化后方法
//    @Benchmark
//    public int testValueUltimate(Blackhole bh) {
//        Chess piece = testPieces.get(index++ % 100);
//        int result = ComputeUtil.getValueUltimate(piece, random.nextInt(200));
//        validateResult(result);
//        bh.consume(result);
//        return result;
//    }

//    @Benchmark
//    public int testValue(Blackhole bh) {
//        Chess piece = testPieces.get(index++ % 100);
//        int result = ComputeUtil.getValue(piece, random.nextInt(200));
//        validateResult(result);
//        bh.consume(result);
//        return result;
//    }

//    @Benchmark
//    public int testGetPositionBonus(Blackhole bh) {
//        Chess piece = testPieces.get(index++ % 100);
//        int result = ComputeUtil.getPositionBonus(piece);
//        validateResult(result);
//        bh.consume(result);
//        return result;
//    }

//    @Benchmark
//    public TreeSet<Move> generateAllMoves(Blackhole bh) {
//        TreeSet<Move> moves = AiChooseV2.generateAllMoves(board, ChessColor.R, 3, 100);
//        bh.consume(moves);
//        return moves;
//    }

//    @Benchmark
//    public TreeSet<Move> getValidMoves(Blackhole bh) {
//        TreeSet<Move> result = TreeSetUtil.getInstance();
//        for (int i = 0; i < ROWS; i++) {
//            for (int j = 0; j < COLS; j++) {
//                Chess c = board[i][j];
//                if (c != null && c.getColor() ==  ChessColor.R) {
//                    c.getValidMoves(board, result, 3, 100);
//                }
//            }
//        }
//        bh.consume(result);
//        return result;
//    }


//    @Benchmark
//    public int getPositionBonus(Blackhole bh) {
//        Chess piece = testPieces.get(index++ % 100);
//        piece.setType(ComputeUtil.ChessType.HORSE);
//        int positionBonus = ComputeUtil.getPositionBonus(piece);
//        Chess piece1 = testPieces.get(index++ % 100);
//        piece1.setType(ComputeUtil.ChessType.CAR);
//        int positionBonus2 = ComputeUtil.getPositionBonus(piece1);
//        bh.consume(positionBonus);
//        bh.consume(positionBonus2);
//        return positionBonus;
//    }
//
//    @Benchmark
//    public int getPositionBonusV2(Blackhole bh) {
//        Chess piece = testPieces.get(index++ % 100);
//        piece.setType(ComputeUtil.ChessType.HORSE);
//        int positionBonus = ComputeUtil.getPositionBonusV2(piece);
//        Chess piece1 = testPieces.get(index++ % 100);
//        piece1.setType(ComputeUtil.ChessType.CAR);
//        int positionBonus2 = ComputeUtil.getPositionBonusV2(piece1);
//        bh.consume(positionBonus);
//        bh.consume(positionBonus2);
//        return positionBonus;
//    }


//    @Benchmark
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.NANOSECONDS)
//    @Fork(value = 1, warmups = 1)
//    @Warmup(iterations = 3, time = 1)
//    @Measurement(iterations = 5, time = 1)
//    public SearchResult minimax(Blackhole bh) {
//        int[] count = new int[]{0};
//        SearchResult result = AiChooseV2.minimax(board, 6, Integer.MIN_VALUE, Integer.MAX_VALUE,
//                false, ChessColor.R, count, Lists.newArrayList(), null, ALL_STEP + 1);
//        bh.consume(result);
//        return result;
//    }


    @Setup
    public void setup() {
        random = new Random(42); // 固定种子保证可重复性
        testPieces = new ArrayList<>(100);
        initBoard();
        // 生成测试棋子（x:0-9, y:0-8）
        for (int i = 0; i < 100; i++) {
            int x = i % 10;
            int y = i % 9;
            Chess piece = createRandomPiece(x, y);
            testPieces.add(piece);
        }
    }

    private Chess createRandomPiece(int x, int y) {
        int type = random.nextInt(5);
        switch (type) {
            case 0:
                Car car = new Car(x, y, ChessColor.B);
                car.setType(ChessType.CAR);
                return car;
            case 1:
                Horse horse = new Horse(x, y, ChessColor.B);
                horse.setType(ChessType.HORSE);
                return horse;
            case 2:
                Soldier soldier = new Soldier(x, y, ChessColor.B);
                soldier.setType(ChessType.SOLDIER);
                return soldier;
            default:
                Horse defaultHorse = new Horse(x, y, ChessColor.B);
                defaultHorse.setType(ChessType.HORSE);
                return defaultHorse;
        }
    }

    private void validateResult(int value) {
        if (value < -100000 || value > 20_00000) {
            throw new IllegalStateException("Invalid evaluation result: " + value);
        }
    }
}