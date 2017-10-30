package chess.connection.connectionFiles;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Connection {

	private Socket s;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public Connection(Socket s) throws IOException {
		this.s = s;
		this.out = new ObjectOutputStream(s.getOutputStream());
		this.in = new ObjectInputStream(s.getInputStream());
	}
	public void sendObject(Object r) throws IOException {
		out.reset();
		out.writeObject(r);
		out.flush();
		
	}
	public Object getObject() throws IOException,ClassNotFoundException {
		return in.readObject();
	}
	public void stop() throws IOException {
		s.close();
	}
}