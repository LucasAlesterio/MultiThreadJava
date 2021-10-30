//Lucas Alesterio Marques Vieira 11621ECP016

import java.io.*;
import java.net.*;
import java.util.*;

public final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        InputStream is = this.socket.getInputStream();
        DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String requestLine = br.readLine();
        System.out.println();
        System.out.println(requestLine);

        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();
        String fileName = tokens.nextToken();
        System.out.println("FileName: " + fileName);

        fileName = "." + fileName;

        FileInputStream fis = null;
        Boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK " + CRLF;
            contentTypeLine = contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found " + CRLF;
            contentTypeLine = contentType(".htm") + CRLF;
            entityBody = "<html>" + "<head><title>Not Found</title></head>" + "<body>" + fileName
                    + " não encontrado</body>" + "</html>";
        }
        os.writeBytes(statusLine);
        os.writeBytes(contentTypeLine);
        os.writeBytes(CRLF);

        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }

        os.close();
        br.close();
        socket.close();
    }

    private String contentType(String fileName) {
        String line = "Content-type: ";
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return line + "text/html";
        }

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            return line + "image/jpeg";
        }

        if (fileName.endsWith(".gif")) {
            return line + "image/gif";
        }

        if (fileName.endsWith(".pdf")) {
            return line + "application/pdf";
        }

        return line + "application/octet-stream";
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
}