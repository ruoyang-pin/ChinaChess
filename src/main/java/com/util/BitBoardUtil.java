package com.util;

import com.domain.chess.BitBoard;
import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.google.common.collect.Lists;
import com.ui.BoardPanel;
import com.util.move.*;

import java.util.List;

import static com.service.AiChooseV2.MAX_DEPTH;
import static com.ui.BoardPanel.CURRENT_TURN;
import static com.ui.BoardPanel.END;
import static com.util.move.MoveGenerator.*;

public class BitBoardUtil {

    public static int ALL_STEP = 0;

    public static int selectedPos = -1;

    public static int movedPos = -1;

    public static final List<Integer> validMovePositions = Lists.newArrayList();

    public static int getIndex(int row, int col) {
        return row * 9 + col; // 0 ~ 89
    }

    public static int[] getXY(int pos) {
        int row = POS_X[pos];
        int col = POS_Y[pos];
        return new int[]{row, col};
    }

    public static boolean isSet(long[] board, int index) {
        if (index < 64) return (board[0] & (1L << index)) != 0;
        else return (board[1] & (1L << (index - 64))) != 0;
    }

    public static void set(long[] board, int index) {
        if (index < 64) board[0] |= (1L << index);
        else board[1] |= (1L << (index - 64));
    }

    public static void clear(long[] board, int index) {
        if (index < 64) board[0] &= ~(1L << index);
        else board[1] &= ~(1L << (index - 64));
    }

    public static void move(long[] board, int fromIndex, int toIndex) {
        clear(board, fromIndex);
        set(board, toIndex);
    }


    /**
     * 执行走子时更新列位图
     */
    public static void updateColumnOccupy(long[] columnOccupy, int from, int to) {
        // 清除 from
        columnOccupy[POS_Y[from]] &= ~(1L << POS_X[from]);
        // 设置 to
        columnOccupy[POS_Y[to]] |= (1L << POS_X[to]);
    }

    /**
     * 落子时设置列位图
     */
    public static void setColumnView(long[] columnOccupy, int index) {
        columnOccupy[POS_Y[index]] |= (1L << POS_X[index]);
    }


    /**
     * 检查列位图是否占用
     */
    public static boolean isColumnOccupied(long[] columnOccupy, int pos) {
        return (columnOccupy[POS_Y[pos]] & (1L << POS_X[pos])) != 0;
    }

    // 设置某格在行视图中为占用
    public static void setRowView(long[] rowOccupy, int index) {
        int row = POS_X[index]; // 行号 0~9
        int col = POS_Y[index]; // 列号 0~8
        rowOccupy[row] |= (1L << col);
    }

    // 清除某格在行视图中的占用
    public static void clearRowView(long[] rowOccupy, int index) {
        int row = POS_X[index];
        int col = POS_Y[index];
        rowOccupy[row] &= ~(1L << col);
    }

    // 更新行视图：走子时清除from，设置to
    public static void updateRowOccupy(long[] rowOccupy, int from, int to) {
        int fromRow = POS_X[from], fromCol = POS_Y[from];
        int toRow = POS_X[to], toCol = POS_Y[to];
        rowOccupy[fromRow] &= ~(1L << fromCol);
        rowOccupy[toRow] |= (1L << toCol);
    }


    public static void moveChessBitBoard(BitBoard bitBoard, int row, int col) {
        int pos = row * 9 + col;
        // 1. 选中棋子阶段
        if (selectedPos == -1) {
            // 判断点击点是否有当前回合己方棋子
            if (BitBoardMoveUtil.isOwnPieceAt(bitBoard, pos, CURRENT_TURN)) {
                selectedPos = pos;
                // 计算合法走法，填充 validMovePositions
                validMovePositions.clear();
                int[] generatedMoves = MoveGenerator.generateMovesForPos(bitBoard, selectedPos, CURRENT_TURN);
                for (int move : generatedMoves) {
                    if (move == -1) {
                        break;
                    }
                    int toPos = BitMoveUtil.getTo(move);
                    validMovePositions.add(toPos);
                }
                SoundPlayer.playClickSound("select.wav");
            }
        }
        // 2. 移动阶段
        else {
            if (validMovePositions.contains(pos)) {
                // 执行走子
                int selectedTypeValue = ChessTypeUtil.getChessTypeAt(bitBoard, selectedPos);
                ChessType selectedType = ChessType.getByValue(selectedTypeValue);
                int posType = ChessTypeUtil.getChessTypeAt(bitBoard, pos);
                BitBoardMoveUtil.applyMove(bitBoard, selectedPos, pos, selectedType, CURRENT_TURN, posType != -1, posType);
                SoundPlayer.playClickSound("select.wav");
                //记录走法
                MoveRecordUtil.record(selectedPos, pos, selectedType, CURRENT_TURN, posType != -1, posType);
                // 切换回合
                int chessTypeValue = ChessTypeUtil.getChessTypeAt(bitBoard, pos);
                BoardStateBuilder.getInstance().applyMove(selectedPos, pos, chessTypeValue, CURRENT_TURN);
                CURRENT_TURN = (CURRENT_TURN == ChessColor.R) ? ChessColor.B : ChessColor.R;
                ALL_STEP++;
                // 判断将军
                if (BitBoardUtil.isInCheck(bitBoard, CURRENT_TURN)) {
                    SoundPlayer.playClickSound("jiangjun.wav");
                }
                movedPos = pos;
                BoardPanel.LAST_MOVE_POINT = selectedPos;
                // 判断游戏结束（吃掉对方将）
                long[] enemyKing = getPieceBits(bitBoard, ChessType.KING, CURRENT_TURN);
                if (BitBoardUtil.isOccupied(enemyKing, pos)) {
                    END = true;
                    SoundPlayer.playClickSound("winner.wav");
                }
            }

            selectedPos = -1;
            validMovePositions.clear();
        }
    }

    public static boolean isInCheck(BitBoard bitBoard, ChessColor color) {
        // 1. 找到己方将的位置
        int kingPos = findKingPos(bitBoard, color);
        if (kingPos == -1) return false;  // 没找到将，理论上异常

        ChessColor enemyColor = (color == ChessColor.R) ? ChessColor.B : ChessColor.R;
        long[] enemyCars = getPieceBits(bitBoard, ChessType.CAR, enemyColor);
        long[] enemyCannons = getPieceBits(bitBoard, ChessType.CANNON, enemyColor);
        long[] enemyHorses = getPieceBits(bitBoard, ChessType.HORSE, enemyColor);
        long[] enemySoldiers = getPieceBits(bitBoard, ChessType.SOLDIER, enemyColor);
        long[] ownPieces = (color == ChessColor.R) ? bitBoard.redPieces : bitBoard.blackPieces;
        long[] enemyPieces = (color == ChessColor.R) ? bitBoard.blackPieces : bitBoard.redPieces;
        long[] occupied = new long[]{ownPieces[0] | enemyPieces[0], ownPieces[1] | enemyPieces[1]};

        // 2. 判断是否被车攻击（直线路径有敌车且无阻挡）
        for (int d = 0; d < 4; d++) {
            int[] path = MoveGenerator.cannonPathPoints[kingPos][d];
            for (int sq : path) {
                if (isOccupied(occupied, sq)) {
                    if (isBitSet(enemyCars, sq)) {
                        return true;  // 被车将军
                    }
                    break;
                }
            }
        }

        // 判断是否被炮攻击
        for (int d = 0; d < 4; d++) {
            int[] path = MoveGenerator.cannonPathPoints[kingPos][d];
            boolean jumped = false; // 是否已跳过一个棋子
            for (int sq : path) {
                if (isOccupied(occupied, sq)) {
                    if (!jumped) {
                        // 第一次遇到阻挡棋子，设置跳过标记
                        jumped = true;
                    } else {
                        // 第二次遇到阻挡棋子，检查是否是敌方炮
                        if (isBitSet(enemyCannons, sq)) {
                            return true;  // 被炮将军
                        }
                        break; // 无论是否是炮，第二个阻挡后停止搜索该方向
                    }
                }
                // else: 空格继续搜索
            }
        }


        // 4. 判断是否被马攻击
        int[] horseJumps = HorsePrecompute.precomputedHorseJumps[kingPos];
        int[] horseLegs = HorsePrecompute.precomputedHorseLegs[kingPos];
        for (int i = 0; i < horseJumps.length; i++) {
            int jumpPos = horseJumps[i];
            int legPos = horseLegs[i];
            if (!isOccupied(occupied, legPos) && isBitSet(enemyHorses, jumpPos)) {
                return true;  // 被马将军
            }
        }

        // 5. 判断是否被兵攻击
        // 兵攻击方向不同，红兵向上攻击，黑兵向下攻击
        int[] soldierAttackOffsets = (color == ChessColor.R) ? new int[]{-9, -1, 1} : new int[]{9, -1, 1};
        for (int offset : soldierAttackOffsets) {
            int sq = kingPos + offset;
            if (sq < 0 || sq >= 90) continue;
            if (isBitSet(enemySoldiers, sq)) {
                return true;
            }
        }

        return false; // 没被将军
    }

    public static int findKingPos(BitBoard bitBoard, ChessColor color) {
        long[] kings = bitBoard.kings;
        long[] ownPieces = (color == ChessColor.R) ? bitBoard.redPieces : bitBoard.blackPieces;
        long[] kingBits = new long[]{kings[0] & ownPieces[0], kings[1] & ownPieces[1]};

        if (kingBits[0] != 0) return Long.numberOfTrailingZeros(kingBits[0]);
        if (kingBits[1] != 0) return 64 + Long.numberOfTrailingZeros(kingBits[1]);
        return -1;
    }


    // ---- 辅助函数 ----
    private static boolean isBitSet(long[] bits, int pos) {
        int part = pos >>> 6;
        int offset = pos & 63;
        return (bits[part] & (1L << offset)) != 0;
    }

    public static boolean isOccupied(long[] occupied, int pos) {
        return isBitSet(occupied, pos);
    }


}
