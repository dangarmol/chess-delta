package chess.connection.connectionFiles;

import chess.game.mvc.model.genericGameFiles.GameObserver;

public class FWErrorResponse implements Response {

	private static final long serialVersionUID = 1L;
	private String msg;
	
	public FWErrorResponse(String msg){
		this.msg = msg;
	}
	@Override
	public void run(GameObserver o) {
		o.onError(msg);
	}

}
