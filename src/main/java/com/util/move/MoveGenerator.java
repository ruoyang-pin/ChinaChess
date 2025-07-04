package com.util.move;

import com.domain.chess.BitBoard;
import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.util.BitBoardUtil;


import java.util.Arrays;

public class MoveGenerator {

    public static final int ROWS = 10;
    public static final int COLS = 9;

    // 方向顺序：上，下，左，右
    public static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };


    // 每个点预存 row 和 col
    public static final int[] POS_X = new int[90];
    public static final int[] POS_Y = new int[90];


    // 对应每个pos，4个方向的路径点列表（每个路径是一条直线上的格子索引数组）
    // 格式：int[90][4][] 
    // 预处理时构建，便于快速遍历
    public static int[][][] cannonPathPoints = new int[ROWS * COLS][4][];

    static {
        initCannonPathPoints();
        initPath();
    }

    public static void initPath() {
        for (int pos = 0; pos < 90; pos++) {
            POS_X[pos] = pos / 9; // 列
            POS_Y[pos] = pos % 9; // 行
        }
    }

    // 初始化所有格子的4个方向路径点数组
    private static void initCannonPathPoints() {
        for (int pos = 0; pos < ROWS * COLS; pos++) {
            int row = pos / COLS;
            int col = pos % COLS;

            for (int d = 0; d < 4; d++) {
                int dr = DIRECTIONS[d][0];
                int dc = DIRECTIONS[d][1];

                // 先算路径上点数
                int length = 0;
                int r = row + dr;
                int c = col + dc;
                while (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    length++;
                    r += dr;
                    c += dc;
                }

                // 填充路径点数组
                int[] pathPoints = new int[length];
                r = row + dr;
                c = col + dc;
                int idx = 0;
                while (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    pathPoints[idx++] = r * COLS + c;
                    r += dr;
                    c += dc;
                }

                cannonPathPoints[pos][d] = pathPoints;
            }
        }
    }

    // 假设所有路径预处理数据都放这里，比如
    // public static int[][][] cannonPathPoints = ...
    // public static int[][][] carPathPoints = ...
    // public static int[][] horseJumps = ...
    // 等等，初始化你之前写好的
    // 全棋子统一走法生成函数


    public static void generateAllMoves(BitBoard bitBoard, ChessColor color, BoardStateBuilder boardStateBuilder, boolean onlyKill, MoveBuffer moveBuffer) {
        long[] ownPieces = (color == ChessColor.R) ? bitBoard.redPieces : bitBoard.blackPieces;
        ChessColor enemyColor = (color == ChessColor.R) ? ChessColor.B : ChessColor.R;
        int ownKingPos = BitBoardUtil.findKingPos(bitBoard, color);
        int enemyKingPos = BitBoardUtil.findKingPos(bitBoard, enemyColor);
        for (ChessType type : ChessType.values()) {
            generateMovesByType(moveBuffer, bitBoard, ownPieces, type, color, ownKingPos, enemyKingPos, boardStateBuilder, onlyKill, enemyColor);
        }
    }


    /**
     * 根据位置和当前方生成该位置棋子的所有走法
     *
     * @param bitBoard 当前棋盘位棋盘
     * @param pos      目标格子位置 (0~89)
     * @param color    当前走子方
     * @return 长度为2的数组，index 0：走法数量，index 1：int[] 走法数组（打包后的走法）
     */
    public static int[] generateMovesForPos(BitBoard bitBoard, int pos, ChessColor color) {
        MoveBuffer moveBuffer = MoveBuffer.getInstance();
        BoardStateBuilder instance = BoardStateBuilder.getInstance();
        ChessColor enemyColor = (color == ChessColor.R) ? ChessColor.B : ChessColor.R;
        int ownKingPos = BitBoardUtil.findKingPos(bitBoard, color);
        int enemyKingPos = BitBoardUtil.findKingPos(bitBoard, enemyColor);
        int moveCount = 0;
        // 复制结果数组（可选，避免线程本地缓存被覆盖）
        int[] resultMoves = new int[512];
        Arrays.fill(resultMoves, -1);
        // 先判断该位置是否有己方棋子
        int part = pos >>> 6;
        int offset = pos & 63;
        long mask = 1L << offset;

        // 逐个判断每种棋子bitboard中是否有该位，优先级根据棋子类型枚举顺序
        ChessType pieceType = null;
        for (ChessType type : ChessType.values()) {
            long[] pieceBits = getPieceBits(bitBoard, type);
            long ownPieces = (color == ChessColor.R) ? bitBoard.redPieces[part] : bitBoard.blackPieces[part];
            // 该位置有己方棋子且属于该棋子类型
            if ((pieceBits[part] & mask) != 0 && (ownPieces & mask) != 0) {
                pieceType = type;
                break;
            }
        }

        if (pieceType == null) {
            // 没有己方棋子，返回空
            return resultMoves;
        }

        // 分别调用对应的走法生成函数（以你之前定义的参数顺序和方式）
        switch (pieceType) {
            case CAR:
                CarMoveGenerator.generateCarMoves(pos, color, pieceType.getValue(), moveBuffer, instance, false, enemyColor, ownKingPos, enemyKingPos);
                break;
            case CANNON:
                CannonMoveArrayGenerator.generateCannonMoves(pos, color, pieceType.getValue(), moveBuffer, instance, false, enemyColor, ownKingPos, enemyKingPos);
                break;
            case HORSE:
                HorseMoveGenerator.generateHorseMoves(pos, color, pieceType.getValue(), moveBuffer, instance, false, enemyColor, ownKingPos, enemyKingPos);
                break;
            case PRIME_MINISTER:
                PrimeMinisterMoveGenerator.generatePrimeMinisterMoves(pos, color, pieceType.getValue(), moveBuffer, instance, false, enemyColor, ownKingPos, enemyKingPos);
                break;
            case GUARDS:
                GuardsMoveGenerator.generateGuardsMoves(pos, color, pieceType.getValue(), moveBuffer, instance, false, enemyColor, ownKingPos, enemyKingPos);
                break;
            case KING:
                KingMoveGenerator.generateKingMoves(bitBoard, pos, color, pieceType.getValue(), moveBuffer, ownKingPos, enemyKingPos, instance, false, enemyColor);
                break;
            case SOLDIER:
                SoldierMoveGenerator.generateSoldierMoves(pos, color, pieceType.getValue(), moveBuffer, instance, false, enemyColor, ownKingPos, enemyKingPos);
                break;
            default:
                break;
        }
        return moveBuffer.getAllMove();
    }


    /**
     * 根据棋子类型批量生成走法
     */
    private static void generateMovesByType(MoveBuffer moveBuffer, BitBoard bitBoard,
                                            long[] ownPieces,
                                            ChessType chessType,
                                            ChessColor color,
                                            int ownKingPos,
                                            int enemyKingPos,
                                            BoardStateBuilder boardStateBuilder,
                                            boolean onlyKill,
                                            ChessColor enemyColor) {
        long[] pieceBits = getPieceBits(bitBoard, chessType);
        for (int part = 0; part < 2; part++) {
            long bits = pieceBits[part] & ownPieces[part];
            while (bits != 0) {
                int index = Long.numberOfTrailingZeros(bits) + part * 64;
                bits &= bits - 1;
                switch (chessType) {
                    case CAR:
                        CarMoveGenerator.generateCarMoves(index, color, chessType.getValue(), moveBuffer, boardStateBuilder, onlyKill, enemyColor, ownKingPos, enemyKingPos);
                        break;
                    case CANNON:
                        CannonMoveArrayGenerator.generateCannonMoves(index, color, chessType.getValue(), moveBuffer, boardStateBuilder, onlyKill, enemyColor, ownKingPos, enemyKingPos);
                        break;
                    case HORSE:
                        HorseMoveGenerator.generateHorseMoves(index, color, chessType.getValue(), moveBuffer, boardStateBuilder, onlyKill, enemyColor, ownKingPos, enemyKingPos);
                        break;
                    case PRIME_MINISTER:
                        PrimeMinisterMoveGenerator.generatePrimeMinisterMoves(index, color, chessType.getValue(), moveBuffer, boardStateBuilder, onlyKill, enemyColor, ownKingPos, enemyKingPos);
                        break;
                    case GUARDS:
                        GuardsMoveGenerator.generateGuardsMoves(index, color, chessType.getValue(), moveBuffer, boardStateBuilder, onlyKill, enemyColor, ownKingPos, enemyKingPos);
                        break;
                    case KING:
                        KingMoveGenerator.generateKingMoves(bitBoard, index, color, chessType.getValue(), moveBuffer, ownKingPos, enemyKingPos, boardStateBuilder, onlyKill, enemyColor);
                        break;
                    case SOLDIER:
                        SoldierMoveGenerator.generateSoldierMoves(index, color, chessType.getValue(), moveBuffer, boardStateBuilder, onlyKill, enemyColor, ownKingPos, enemyKingPos);
                        break;
                    default:
                        break;
                }
            }
        }

    }


    /**
     * 获取棋子对应的位棋盘
     */
    public static long[] getPieceBits(BitBoard bitBoard, ChessType chessType) {
        switch (chessType) {
            case CAR:
                return bitBoard.cars;
            case CANNON:
                return bitBoard.cannons;
            case HORSE:
                return bitBoard.horses;
            case PRIME_MINISTER:
                return bitBoard.ministers;
            case GUARDS:
                return bitBoard.guards;
            case KING:
                return bitBoard.kings;
            case SOLDIER:
                return bitBoard.soldiers;
            default:
                throw new IllegalArgumentException("未知棋子类型：" + chessType);
        }
    }

    public static long[] getPieceBits(BitBoard bitBoard, ChessType chessType, ChessColor color) {
        long[] allPieces;
        switch (chessType) {
            case CAR:
                allPieces = bitBoard.cars;
                break;
            case CANNON:
                allPieces = bitBoard.cannons;
                break;
            case HORSE:
                allPieces = bitBoard.horses;
                break;
            case PRIME_MINISTER:
                allPieces = bitBoard.ministers;
                break;
            case GUARDS:
                allPieces = bitBoard.guards;
                break;
            case KING:
                allPieces = bitBoard.kings;
                break;
            case SOLDIER:
                allPieces = bitBoard.soldiers;
                break;
            default:
                throw new IllegalArgumentException("未知棋子类型：" + chessType);
        }

        long[] colorPieces = (color == ChessColor.R) ? bitBoard.redPieces : bitBoard.blackPieces;

        // 交集：该棋子类型 & 该颜色棋子
        return new long[]{
                allPieces[0] & colorPieces[0],
                allPieces[1] & colorPieces[1]
        };
    }


}
