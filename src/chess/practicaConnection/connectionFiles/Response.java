package chess.practicaConnection.connectionFiles;

import chess.basecode.bgame.model.GameObserver;

public interface Response extends java.io.Serializable {
	public void run(GameObserver o);
}