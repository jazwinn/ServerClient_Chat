import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.*;
import java.util.*;

public class Server {
    private final int m_port;
    public Set<PrintWriter> clientWriters = new HashSet<PrintWriter>();

    //constructor
    Server(int portNumber){
        m_port = portNumber;
    }

    public void listen() throws IOException {
        ServerSocket serverSocket = new ServerSocket(m_port);
        System.out.println("Server listening on port " + m_port);

        while(true){
         Socket clientSocket = serverSocket.accept();
         System.out.println("New client connected from " + clientSocket.getInetAddress().getHostAddress());

         new Thread(new ClientInstance(clientSocket)).start();

        }
    }

    private class ClientInstance implements Runnable{
        private final Socket m_socket;
        private PrintWriter m_writer;
        private BufferedReader m_reader;

        ClientInstance(Socket socket){
            m_socket = socket;
        }

        @Override
        public void run() {
            try {
                m_reader = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
                m_writer = new PrintWriter(m_socket.getOutputStream(), true);

                //prevent race
                synchronized (clientWriters){
                    clientWriters.add(m_writer);
                }

                String message;
                while(m_reader.readLine() != null){
                    message = m_reader.readLine();
                    synchronized (clientWriters){
                        for(PrintWriter clientWriter : clientWriters){
                            clientWriter.println(message);
                        }

                    }

                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally { // always call no matter exception occurs
                try {
                    m_socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                synchronized (clientWriters){
                    clientWriters.remove(m_writer);
                }

            }
        }
    }


}
