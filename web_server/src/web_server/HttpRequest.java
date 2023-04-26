package web_server;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class HttpRequest implements Runnable{
	
	final static String CRLF="\r\n";
	Socket socket;
	public HttpRequest (Socket socket) throws Exception
	{
		this.socket=socket;
	}
	
	public void run() {
		
		try {
				processRequest();
		        }catch(Exception e)
				{
		        System.out.println(e);
				}
				}
	private void processRequest() throws Exception
		{	
//			DataOutputStream os = new DataOutputStream(socket.getInputStream())
//			setup input stream filters
		
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			BufferedReader br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String requestLine=null;
			
			// System.out.println();
			// System.out.println(requestLine);
			
			String headerline=null ;
			boolean fileExists= true;
			FileInputStream fis= null;
			String file=null;
			if ((headerline=br.readLine()).length()!= 0)
			{
				
				System.out.println(headerline);
				
				StringTokenizer tokens= new StringTokenizer(headerline);
				tokens.nextToken();
				file= tokens.nextToken();
				file="."+file;
				System.out.println("file is "+file);
				try{
				fis= new FileInputStream (file);
				}catch(FileNotFoundException e)
				{
				fileExists=false;
				}
			
			}
			String status= null;
			String content= null;
			String body= null;
			if(fileExists)
			{
			status="HTTP/1.0 200 OK\r\n";
			content="Content-Type: "+contentType(file)+CRLF;
			}
			else
			{
			status="HTTP/1.1 404 File Not Found\r\n";
			content= "Content-Type: text/html\r\n";
			body= "File Is Not Found.";
			}
			os.writeBytes(status);
			os.writeBytes(content);
			os.writeBytes(CRLF);
			if(fileExists)
			{
			sendBytes(fis,os);
			fis.close();
			}else{
			os.writeBytes(body);
			
			}
			
			os.close();
			br.close();
			socket.close();
			System.out.println("REQUEST Ends");
			// os.close();
			//br.close();
			//cket.close();
			}
	
	void sendBytes(FileInputStream fis,OutputStream os) throws Exception
	{
		byte[] buffer= new byte[1024];
		int bytes= 0;
		while ((bytes=fis.read(buffer))!=-1)
	{
	os.write(buffer,0,bytes);
	}
	}
	private static String contentType(String fileName)
	{
	if (fileName.endsWith(".htm")|| fileName.endsWith(".html"))
	{
	return "text/html";
	}
	if(fileName.endsWith(".jpg")){
	return "image/jpeg";
	}
	if(fileName.endsWith(".gif")){
	return "image/gif";
	}
	return "application/octet-stream";
	}
	}

		
	


