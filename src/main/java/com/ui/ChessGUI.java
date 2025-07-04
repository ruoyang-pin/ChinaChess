package com.ui;

import com.enumerates.ChessColor;
import com.util.move.MoveRecordUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChessGUI extends JFrame {

    private void choosePlayerColor() {
        Object[] options = {"红方", "黑方"};
        int result = JOptionPane.showOptionDialog(
                this,
                "请选择你的颜色：",
                "选择阵营",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (result == 0) {
            BoardPanel.PLAYER_COLOR = ChessColor.R;
            BoardPanel.AI_COLOR = ChessColor.B;
        } else if (result == 1) {
            BoardPanel.PLAYER_COLOR = ChessColor.B;
            BoardPanel.AI_COLOR = ChessColor.R;
        } else {
            System.exit(0);
        }
    }


    public ChessGUI() {
        setTitle("Chinese Chess - 仿真棋盘");
        choosePlayerColor(); // 选择颜色
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 获取屏幕大小，设置窗口大小为 60% 宽 × 80% 高
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.6);
        int height = (int) (screenSize.height * 0.8);
        setSize(width, height);
        setLocationRelativeTo(null); // 居中窗口

        // 用 JLayeredPane 叠加，不用 BorderLayout
        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);

        BoardPanel boardPanel = new BoardPanel();
        boardPanel.setBounds(0, 0, width, height);
        layeredPane.add(boardPanel, Integer.valueOf(0)); // 棋盘在底层

        JButton undoButton = new JButton("悔棋");
        undoButton.setFont(new Font("微软雅黑", Font.BOLD, (int)(screenSize.width*0.015)));
        undoButton.addActionListener(e -> {
            MoveRecordUtil.Repentance();
            boardPanel.repaint();
        });

        layeredPane.add(undoButton, Integer.valueOf(1)); // 按钮在顶层

        // 窗口大小变化时，自动调整棋盘和按钮
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                boardPanel.setBounds(0, 0, getContentPane().getWidth(), getContentPane().getHeight());
                int buttonWidth = (int) (screenSize.width * 0.05);
                int buttonHeight = (int) (screenSize.height * 0.05);
                int buttonX =(int)((w - buttonWidth) - screenSize.width * 0.05);
                int buttonY = (h - buttonHeight) / 2;
                undoButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
            }
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI();
            gui.setVisible(true);
        });
    }

}