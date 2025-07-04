package com.util.move;

import com.domain.location.MoveRecord;
import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.ui.BoardPanel;
import com.util.BitBoardMoveUtil;
import com.util.BitBoardUtil;

import javax.swing.*;
import java.util.Stack;

import static com.ui.BoardPanel.*;

/**
 * @author rich
 * @date 2025/7/2
 * @description
 */
public class MoveRecordUtil {

    private static final Stack<MoveRecord> moveHistory = new Stack<>();

    public static void Repentance() {
        if (moveHistory.empty() || moveHistory.size() < 2) {
            JOptionPane.showMessageDialog(null, "无子可悔", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (END || PLAYER_COLOR != CURRENT_TURN) {
            JOptionPane.showMessageDialog(null, "游戏已经结束或者ai方执行中", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (int i = 0; i < 2; i++) {
            MoveRecord pop = moveHistory.pop();
            BitBoardMoveUtil.undoMove(bitBoard, pop.fromIndex, pop.toIndex, pop.pieceType, pop.color, pop.isCaptured, pop.capturedType);
            BoardStateBuilder.getInstance().undoMove(pop.fromIndex, pop.toIndex, pop.isCaptured, pop.capturedType, pop.pieceType.getValue(), pop.color);
        }
        BitBoardUtil.movedPos = -1;
        BoardPanel.LAST_MOVE_POINT = -1;

    }

    public static void record(int fromIndex, int toIndex, ChessType pieceType, ChessColor color, boolean isCaptured, int capturedType) {
        MoveRecord record = new MoveRecord(fromIndex, toIndex, pieceType, color, isCaptured, capturedType);
        moveHistory.push(record);
    }
}
