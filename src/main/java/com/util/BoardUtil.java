package com.util;

import com.service.AiChooseV2;
import com.util.move.BitMoveUtil;

import javax.swing.*;

import static com.ui.BoardPanel.*;

/**
 * @author rich
 * @date 2025/6/22
 * @description
 */
public class BoardUtil {


    public static void aiMove(Runnable repaintCallback) {
        SwingUtilities.invokeLater(() -> {
            if (!END && PLAYER_COLOR != CURRENT_TURN) {
                Integer move = AiChooseV2.queryPoint(bitBoard, AI_COLOR);
                int from = BitMoveUtil.getFrom(move);
                int to = BitMoveUtil.getTo(move);
                int[] fromXY = BitBoardUtil.getXY(from);
                int[] toXY = BitBoardUtil.getXY(to);
                new Thread(() -> {
                    // 1. 模拟选中
                    SwingUtilities.invokeLater(() -> {
                        BitBoardUtil.moveChessBitBoard(bitBoard, fromXY[0], fromXY[1]);
                        repaintCallback.run();
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 2. 模拟落子
                    SwingUtilities.invokeLater(() -> {
                        BitBoardUtil.moveChessBitBoard(bitBoard, toXY[0], toXY[1]);
                        repaintCallback.run();
                    });
                }).start();
            }
        });
    }



}
