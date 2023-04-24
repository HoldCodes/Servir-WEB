import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer{
    public static void main(String args[]) throws Exception {
        //setando numero da porta
        int port = 7000;

        //estabelecendo um socket de escuta
        ServerSocket serverSocket = new ServerSocket(port);

        //processando solicitações do servidor HTTP em loop infinito
        while(true){
            //ouça uma solicitação de conecção TCP
            Socket socket = serverSocket.accept();

            //Construa um objeto para processar a mensagem de solicitação HTTP.
            HttpRequest request = new HttpRequest(socket);

            //criar uma nova thread para o processo de request
            Thread thread = new Thread(request);

            //iniciando a thread
            thread.start();

        }
    }
}

final class HttpRequest implements Runnable{
    final static String CRLF = "\r\n";
    Socket socket;

    //construtor
    public HttpRequest(Socket socket) throws Exception{
        this.socket = socket;
    }

    //implementação d método run()
    public void run(){
        try{
            processRequest();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception{
        //Obtenha uma referência aos fluxos de entrada e saída do soquete.
        InputStream is = socket.getInputStream();

        DataOutput os = new DataOutputStream(socket.getOutputStream());

        //Configure filtros de fluxo de entrada. //solução do livro abaixo
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        //Obtenha a linha de solicitação da mensagem de solicitação HTTP.
        String requestLine = br.readLine();

        //Exibir a linha de solicitação.
        System.out.println();
        System.out.println(requestLine);

        //Obtenha e exiba as linhas de cabeçalho.
        String headerLine = null;

        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        // Extraindo o nome do arquivo da linha de requisição.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken(); //pule o método, que deve ser "GET"
        String fileName = tokens.nextToken();

        // Acrescente um "." para que a solicitação de arquivo esteja dentro do diretório atual.
        fileName = "." + fileName;

        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;

        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        //Construa a mensagem de resposta.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if (fileExists) {
            statusLine = "200 OK:";
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 NOT FOUND";
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
        }

        //Envie a linha de status.
        os.writeBytes(statusLine);

        //Envie a linha do tipo de conteúdo.
        os.writeBytes(contentTypeLine);

        //Envie uma linha em branco para indicar o fim das linhas de cabeçalho.
        os.writeBytes(CRLF);

        // Send the entity body.
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }

        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

        //String redes1 = "C:\\Users\\bruno\\OneDrive\\Documentos\\BSI\\redes de computadores\\redes1.jpg";
        //String redes2 = "C:\\Users\\bruno\\OneDrive\\Documentos\\BSI\\redes de computadores\\redes2.gif";
        //outToClient.writeBytes("Content-Type: text/html\n\n");
        //outToClient.writeBytes("<>html><body><h1>Teste.</h1><img src=\"C:\\Users\\bruno\\OneDrive\\Documentos\\BSI\\redes de computadores\\redes.jpg\"><img src=\"C:\\Users\\bruno\\OneDrive\\Documentos\\BSI\\redes de computadores\\redes2\"></body></html>\n");
        //outToClient.close();

        outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
        outToClient.writeBytes("Content-Type: text/html\r\n");
        outToClient.writeBytes("\r\n");

        //escrever o corpo da resposta
        outToClient.writeBytes("<html><body><h1>Teste.</h1><img src=\"redes1.jpg\"><img src=\"redes2.gif\"></body></html>\n");       
        outToClient.close();

        //fechando os socket's. de acordo com o livro
        ((ServerSocket) os).close();
        br.close();
        socket.close();
    }


    private static void sendBytes(FileInputStream fis, DataOutput os) throws Exception{
        //construa um buffer de 1K para manter os bytes em seu caminho para o soquete.
        byte[] buffer = new byte[1024];
        int bytes = 0;

        //Copie o arquivo solicitado no fluxo de saída do soquete.
        while((bytes = fis.read(buffer)) != -1 ) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName){
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }

        if(fileName.endsWith(".gif")) {
            return "image/gif";
        }

        if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }

        if(fileName.endsWith(".png")) {
            return "image/png";
        }

        return "application/octet-stream";
    }
}