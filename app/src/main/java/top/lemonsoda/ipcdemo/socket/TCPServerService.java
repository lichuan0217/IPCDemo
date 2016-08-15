package top.lemonsoda.ipcdemo.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {

    private static final String TAG = TCPServerService.class.getCanonicalName();

    private boolean isServiceDestroyed = false;
    private String[] definedMessages = new String[]{
            "Hello, guys",
            "What is your name",
            "How do you do?",
            "What is the weather?",
            "Nice day~"
    };

    public TCPServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "TCP Service onCreate");
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "TCP Service Destroyed");
        isServiceDestroyed = true;
        super.onDestroy();
    }

    private class TcpServer implements Runnable{
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(9999);
            } catch (IOException e) {
                Log.d(TAG, "Establish TCP Server failed, port 9999");
                e.printStackTrace();
                return;
            }
            while (!isServiceDestroyed) {
                try {
                    Log.d(TAG, "Waiting for client....");
                    final Socket socket = serverSocket.accept();
                    Log.d(TAG, "Accept client");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                responseClient(socket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

        out.println("Welcome to Chat Room");
        while (!isServiceDestroyed) {
            String str = in.readLine();
            Log.d(TAG, "Message from client: " + str);
            if (str == null)
                break;
            int i = new Random().nextInt(definedMessages.length);
            String msg = definedMessages[i];
            out.println(msg);
            Log.d(TAG, "Send :" + msg);
        }

        Log.d(TAG, "Client Quit");
        out.close();
        in.close();
        client.close();
    }
}
