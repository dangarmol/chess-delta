package chess.connection.connectionFiles;

import chess.game.mvc.model.genericGameFiles.GameObserver;

public interface Response extends java.io.Serializable {
	public void run(GameObserver o);
}