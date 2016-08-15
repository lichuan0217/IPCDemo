package top.lemonsoda.ipcdemo.socket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import top.lemonsoda.ipcdemo.R;

public class TCPClientActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = TCPClientActivity.class.getCanonicalName();

    private static final int MESSAGE_RECEIVE_NEW_MSG = 0;
    private static final int MESSAGE_SOCKET_CONNECTED = 1;

    private Button sendButton;
    private TextView messageTextView;
    private EditText messageEditText;

    private PrintWriter printWriter;
    private Socket clientSocket;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    messageTextView.setText(
                            messageTextView.getText() + (String) msg.obj
                    );
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    sendButton.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        messageEditText = (EditText) findViewById(R.id.editText);
        messageTextView = (TextView) findViewById(R.id.textView);
        sendButton = (Button) findViewById(R.id.button);

        sendButton.setOnClickListener(this);

        Intent intent = new Intent(TCPClientActivity.this, TCPServerService.class);
        startService(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectTCPServer();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (clientSocket != null) {
            try {
                clientSocket.shutdownInput();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == sendButton) {
            final String msg = messageEditText.getText().toString();
            if (!TextUtils.isEmpty(msg) && printWriter != null) {
                printWriter.println(msg);
                messageEditText.setText("");
                String time = formatDateFormat(System.currentTimeMillis());
                final String showMsg = "self " + time + ":" + msg + "\n";
                messageTextView.setText(
                        messageTextView.getText() + showMsg
                );
            }
        }
    }

    private String formatDateFormat(long time) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    private void connectTCPServer() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 9999);
                clientSocket = socket;
                printWriter = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                handler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.d(TAG, "Connect to server successfully");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                Log.d(TAG, "Connect to server failed, retry ...");
                e.printStackTrace();
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!TCPClientActivity.this.isFinishing()) {
                String msg = reader.readLine();
                if (msg != null) {
                    Log.d(TAG, "receive: " + msg);
                    String time = formatDateFormat(System.currentTimeMillis());
                    final String showMsg = "Server " + time + ":" + msg + "\n";
                    handler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showMsg).sendToTarget();
                }
            }
            Log.d(TAG, "Quit...");
            printWriter.close();
            reader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
