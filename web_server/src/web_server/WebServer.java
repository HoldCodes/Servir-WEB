package web_server;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServer {
public static void main(String[] args) throws Exception{
	int port =2222;
	ServerSocket ServSocket = new ServerSocket (port);
	// open listen socket
	while (true)
	{
		// lISTEN for a tcp conn
		Socket req= ServSocket.accept();
		HttpRequest hreq= new HttpRequest(req);
		Thread th= new Thread(hreq);
		th.start();
	}
	}
}