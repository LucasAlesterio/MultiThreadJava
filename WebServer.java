
//Lucas Alesterio Marques Vieira 11621ECP016
import java.net.*;

public final class WebServer {
    public static void main(String arvg[]) throws Exception {
        int porta = 4242;
        try {
            ServerSocket socketServe = new ServerSocket(porta);
            while (true) {
                Socket connectionSocket = socketServe.accept();
                HttpRequest request = new HttpRequest(connectionSocket);
                Thread thread = new Thread(request);
                thread.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
