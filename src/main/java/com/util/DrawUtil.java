package com.util;

import com.domain.chess.BitBoard;
import com.domain.chess.Chess;
import com.domain.location.Point;
import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.util.move.MoveGenerator;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

import static com.constant.CommonConst.COLS;
import static com.constant.CommonConst.ROWS;
import static com.ui.BoardPanel.PANEL_HEIGHT;
import static com.ui.BoardPanel.PANEL_WIDTH;

/**
 * @author rich
 * @date 2025/6/21
 * @description
 */
public class DrawUtil {

    // 绘制棋子
    public static void drawChessByBitboard(BitBoard bitBoard, int offsetX, int offsetY, int tileSize, Graphics2D g2) {
        // 遍历所有棋子类型
        for (ChessType chessType : ChessType.values()) {
            // 获取对应类型位棋盘
            long[] redPieces = MoveGenerator.getPieceBits(bitBoard, chessType, ChessColor.R);
            long[] blackPieces = MoveGenerator.getPieceBits(bitBoard, chessType, ChessColor.B);
            // 逐个红棋子绘制
            drawPiecesByColorAndBits(redPieces, ChessColor.R, chessType, offsetX, offsetY, tileSize, g2);

            // 逐个黑棋子绘制
            drawPiecesByColorAndBits(blackPieces, ChessColor.B, chessType, offsetX, offsetY, tileSize, g2);
        }
    }

    // 绘制某颜色、某棋子类型所有棋子
    private static void drawPiecesByColorAndBits(long[] bitsArray, ChessColor color, ChessType type,
                                                 int offsetX, int offsetY, int tileSize, Graphics2D g2) {
        for (int part = 0; part < bitsArray.length; part++) {
            long bits = bitsArray[part];
            while (bits != 0) {
                int index = Long.numberOfTrailingZeros(bits) + part * 64;
                bits &= bits - 1;

                int row = index / COLS;
                int col = index % COLS;

                Image img = getImage(color, type);
                if (img != null) {
                    int x = offsetX + col * tileSize;
                    int y = offsetY + row * tileSize;
                    int r = tileSize / 2 - 8;
                    int imgX = x - r;
                    int imgY = y - r;
                    int imgSize = r * 2;
                    g2.drawImage(img, imgX, imgY, imgSize, imgSize, null);
                }
            }
        }
    }

    // 辅助函数：根据颜色和棋子类型获取图片
    private static Image getImage(ChessColor color, ChessType type) {
        String pieceName = color.name() + type.getChineseName();  // 比如 "R_CAR"
        String resourcePath = "chess/" + pieceName + ".png";
        URL imgUrl = DrawUtil.class.getClassLoader().getResource(resourcePath);
        if (imgUrl != null) {
            return new ImageIcon(imgUrl).getImage();
        } else {
            System.err.println("图片未找到: " + resourcePath);
            return null;
        }
    }


    public static void drawStars(Graphics2D g, int offsetX, int offsetY, int tile) {
        int r = tile / 12;
        int[][] points = {
                // 黑方炮兵点
                {1, 2}, {7, 2}, {0, 3}, {2, 3}, {4, 3}, {6, 3}, {8, 3},
                // 红方炮兵点
                {1, 7}, {7, 7}, {0, 6}, {2, 6}, {4, 6}, {6, 6}, {8, 6}
        };
        for (int[] p : points) {
            int x = offsetX + p[0] * tile;
            int y = offsetY + p[1] * tile;
            g.fillOval(x - r, y - r, r * 2, r * 2);
        }
    }


    public static void drawOutlineFrame(Graphics2D g2, Point point) {
        if (point != null) {
            int tileSize = Math.min(PANEL_WIDTH / COLS, PANEL_HEIGHT / ROWS);
            int offsetX = (PANEL_WIDTH - tileSize * (COLS - 1)) / 2;
            int offsetY = (PANEL_HEIGHT - tileSize * (ROWS - 1)) / 2;

            int x = offsetX + point.getY() * tileSize;
            int y = offsetY + point.getX() * tileSize;

            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3)); // 粗线条
            g2.drawRect(x - tileSize / 2, y - tileSize / 2, tileSize, tileSize); // 画红色轮廓
        }

    }


    public static void drawChessBoard(int offsetX, int offsetY, int tileSize, Graphics2D g2) {
        // 画横线
        for (int i = 0; i < ROWS; i++) {
            int y = offsetY + i * tileSize;
            g2.drawLine(offsetX, y, offsetX + (COLS - 1) * tileSize, y);
        }

        // 画竖线（楚河汉界断开）
        for (int j = 0; j < COLS; j++) {
            int x = offsetX + j * tileSize;
            if (j == 0 || j == COLS - 1) {
                // 边缘线贯通
                g2.drawLine(x, offsetY, x, offsetY + (ROWS - 1) * tileSize);
            } else {
                g2.drawLine(x, offsetY, x, offsetY + 4 * tileSize);
                g2.drawLine(x, offsetY + 5 * tileSize, x, offsetY + 9 * tileSize);
            }
        }

        // 外框
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(offsetX, offsetY, tileSize * (COLS - 1), tileSize * (ROWS - 1));

        // 宫的斜线（上）
        g2.drawLine(offsetX + 3 * tileSize, offsetY,
                offsetX + 5 * tileSize, offsetY + 2 * tileSize);
        g2.drawLine(offsetX + 5 * tileSize, offsetY,
                offsetX + 3 * tileSize, offsetY + 2 * tileSize);

        // 宫的斜线（下）
        g2.drawLine(offsetX + 3 * tileSize, offsetY + 7 * tileSize,
                offsetX + 5 * tileSize, offsetY + 9 * tileSize);
        g2.drawLine(offsetX + 5 * tileSize, offsetY + 7 * tileSize,
                offsetX + 3 * tileSize, offsetY + 9 * tileSize);

        // “楚河汉界”文字
        g2.setFont(new Font("Serif", Font.BOLD, tileSize / 2));
        int size = offsetY + 5 * tileSize - tileSize / 4;
        g2.drawString("楚河", offsetX + 2 * tileSize, size);
        g2.drawString("汉界", offsetX + 6 * tileSize, size);
    }

    public static void drawSelectChess(int selectedChess, Graphics2D g2, int offsetX, int offsetY, int tileSize, List<Integer> validMoves) {
        // 高亮选中和可走点
        if (selectedChess != -1) {
            int row = selectedChess / 9;
            int col = selectedChess % 9;

            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
            int x = offsetX + col * tileSize;
            int y = offsetY + row * tileSize;
            // 🔦 发光高亮选中棋子
            int glowRadius = tileSize / 2; // 控制发光圈大小
            for (int r = glowRadius; r > 0; r -= 5) {
                float alpha = (float) r / glowRadius * 0.3f; // 越外层越透明
                g2.setColor(new Color(255, 215, 0, (int) (alpha * 255))); // 金黄色 Glow
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x - r, y - r, r * 2, r * 2);
            }
            g2.setColor(Color.BLUE);
            for (Integer p : validMoves) {
                int validX = p / 9;
                int validY = p % 9;
                int cx = offsetX + validY * tileSize;
                int cy = offsetY + validX * tileSize;

                // 绘制外部半透明圆环（气泡光晕）
                g2.setColor(new Color(30, 144, 255, 80)); // DodgerBlue, 半透明
                g2.fillOval(cx - 10, cy - 10, 20, 20);

                // 绘制内部实心圆（气泡核心）
                g2.setColor(new Color(30, 144, 255)); // 实心亮色
                g2.fillOval(cx - 4, cy - 4, 8, 8);
            }
        }
    }

    public static void drawMoveIndicator(Graphics2D g2, int point, int offsetX, int offsetY, int tileSize) {
        if (point != -1) {
            int[] xy = BitBoardUtil.getXY(point);

            int x = offsetX + xy[1] * tileSize;
            int y = offsetY + xy[0] * tileSize;

            int glowRadius = tileSize / 2;
            for (int r = glowRadius; r > 0; r -= 5) {
                g2.setColor(new Color(255, 248, 231)); // 奶白色 #FFF8E7，带透明度
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x - r, y - r, r * 2, r * 2);
            }
        }
    }


}
