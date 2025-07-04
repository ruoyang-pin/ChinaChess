package com.util.move;

import com.enumerates.ChessColor;
import com.enums.ChessType;
import com.util.BitBoardUtil;

public class PathStack {



    public static void print(int[] currentPath, int plv) {
        for (int i = 0; i < plv; i++) {
            int move = currentPath[i];
            if (move <= 0) {
                continue;
            }
            int from = BitMoveUtil.getFrom(move);
            int[] fromXY = BitBoardUtil.getXY(from);
            int to = BitMoveUtil.getTo(move);
            int[] toXY = BitBoardUtil.getXY(to);
            int chessTypeValue = BitMoveUtil.getChessType(move);
            int color = BitMoveUtil.getColor(move);
            ChessType chessType = ChessType.getByValue(chessTypeValue);
            ChessColor chessColor = ChessColor.getByValue(color);
            System.out.printf("%s走法为,%s,%s到 %s,%s %n", chessColor.name() + chessType.getChineseName(), fromXY[0], fromXY[1], toXY[0], toXY[1]);
        }
    }

}
