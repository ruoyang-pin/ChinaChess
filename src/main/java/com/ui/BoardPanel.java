package com.ui;

import com.adapter.ChessMouseAdapter;
import com.domain.chess.BitBoard;
import com.domain.chess.Chess;
import com.domain.location.Point;
import com.enumerates.ChessColor;
import com.util.BitBoardMoveUtil;
import com.util.BitBoardUtil;
import com.util.BoardUtil;
import com.util.DrawUtil;
import com.util.move.BoardStateBuilder;

import javax.swing.*;
import java.awt.*;

import static com.constant.CommonConst.COLS;
import static com.constant.CommonConst.ROWS;
import static com.util.move.MoveGenerator.POS_X;
import static com.util.move.MoveGenerator.POS_Y;

/**
 * @author rich
 * @date 2025/6/21
 * @description
 */
public class BoardPanel extends JPanel {


    public static int LAST_MOVE_POINT = -1;

    public static ChessColor CURRENT_TURN = ChessColor.R;

    public static ChessColor PLAYER_COLOR;

    public static ChessColor AI_COLOR;

    public static boolean END;

    public static Chess B_KING;

    public static Chess R_KING;

    public static int PANEL_WIDTH;

    public static int PANEL_HEIGHT;

    public static Chess[][] board = new Chess[10][9]; // [行][列]

    public static BitBoard bitBoard;


    static {
        // 初始化棋子：R 表红方，B 表黑方，后面是棋子名
        bitBoard = BitBoardMoveUtil.init();
        BoardStateBuilder.getInstance().build(bitBoard);
    }

    public BoardPanel() {
        addMouseListener(new ChessMouseAdapter(this::repaint));
        BoardUtil.aiMove(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        PANEL_WIDTH = getWidth();
        PANEL_HEIGHT = getHeight();
        // 背景色：米黄色/仿木色
        g.setColor(new Color(245, 222, 179));
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int tileWidth = panelWidth / COLS;
        int tileHeight = panelHeight / ROWS;
        int tileSize = Math.min(tileWidth, tileHeight);

        int offsetX = (panelWidth - tileSize * (COLS - 1)) / 2;
        int offsetY = (panelHeight - tileSize * (ROWS - 1)) / 2;

        //绘制棋盘
        DrawUtil.drawChessBoard(offsetX, offsetY, tileSize, g2);

        // 绘制星位点
        DrawUtil.drawStars(g2, offsetX, offsetY, tileSize);

        // 绘制棋子
        DrawUtil.drawChessByBitboard(bitBoard, offsetX, offsetY, tileSize, g2);

        // 高亮选中和可走点
        DrawUtil.drawSelectChess(BitBoardUtil.selectedPos, g2, offsetX, offsetY, tileSize, BitBoardUtil.validMovePositions);

        if (BitBoardUtil.movedPos != -1) {
            int row = POS_X[BitBoardUtil.movedPos];
            int col = POS_Y[BitBoardUtil.movedPos];
            DrawUtil.drawOutlineFrame(g2, new Point(row, col));
        }

        DrawUtil.drawMoveIndicator(g2, LAST_MOVE_POINT, offsetX, offsetY, tileSize);


    }


}
