package chess.practicaConnection.connectionFiles;

import chess.practicaConnection.MainConnection;

public class TestServer {
	public static void main(String[] args) {
		
		switch(3){
			case 1:{
				String[] arguments = { "-am", "normal", "-g", "ataxx", "-p","X:m,O:m,C:m", "-m", "-o", "2" };
				MainConnection.main(arguments);
				break;
			}
			case 2:{
				String[] arguments = { "-am", "server", "-g", "ataxx", "-sp", "4001", "-p","X:m,Y:m,C:m", "-v", "window", "-o", "2" };
				MainConnection.main(arguments);
			}
			case 3:{
				String[] arguments = { "-am", "client", "-sp", "4001", "-sh", "localhost"};
				MainConnection.main(arguments);
			}
		}
	}
}