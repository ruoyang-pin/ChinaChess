package com.adapter;

import com.util.BitBoardMoveUtil;
import com.util.BitBoardUtil;
import com.util.BoardUtil;
import lombok.AllArgsConstructor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.constant.CommonConst.COLS;
import static com.constant.CommonConst.ROWS;
import static com.ui.BoardPanel.*;

/**
 * @author rich
 * @date 2025/6/22
 * @description
 */
@AllArgsConstructor
public class ChessMouseAdapter extends MouseAdapter {

    private Runnable repaintCallback; // 用于回调 repaint()

    @Override
    public void mouseClicked(MouseEvent e) {
        if (END || PLAYER_COLOR != CURRENT_TURN) {
            return;
        }
        int tileSize = Math.min(PANEL_WIDTH / COLS, PANEL_HEIGHT / ROWS);
        int offsetX = (PANEL_WIDTH - tileSize * (COLS - 1)) / 2;
        int offsetY = (PANEL_HEIGHT - tileSize * (ROWS - 1)) / 2;

        int col = Math.round((e.getX() - offsetX) / (float) tileSize);
        int row = Math.round((e.getY() - offsetY) / (float) tileSize);

        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return;

        BitBoardUtil.moveChessBitBoard(bitBoard, row, col);
        repaintCallback.run();

        BoardUtil.aiMove(repaintCallback);

    }
}
