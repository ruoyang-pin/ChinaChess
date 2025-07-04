package com.domain.location;

import com.domain.chess.Chess;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author rich
 * @date 2025/6/22
 * @description
 */
@Data
@AllArgsConstructor
public class MoveBackup {

    private Chess movedPiece;

    private Chess capturedPiece;

    private Point originalLocation;
}
