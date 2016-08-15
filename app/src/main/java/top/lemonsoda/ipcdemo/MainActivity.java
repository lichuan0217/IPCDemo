package top.lemonsoda.ipcdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import top.lemonsoda.ipcdemo.aidl.BookManagerActivity;
import top.lemonsoda.ipcdemo.binderpool.BinderPoolActivity;
import top.lemonsoda.ipcdemo.contentprovider.ProviderActivity;
import top.lemonsoda.ipcdemo.messenger.MessengerActivity;
import top.lemonsoda.ipcdemo.socket.TCPClientActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void onMessenger(View view) {
        Intent intent = new Intent(this, MessengerActivity.class);
        startActivity(intent);
    }

    public void onAIDL(View view) {
        Intent intent = new Intent(this, BookManagerActivity.class);
        startActivity(intent);
    }

    public void onContentProvider(View view) {
        Intent intent = new Intent(this, ProviderActivity.class);
        startActivity(intent);
    }

    public void onSocket(View view) {
        Intent intent = new Intent(this, TCPClientActivity.class);
        startActivity(intent);
    }

    public void onBinderPool(View view) {
        Intent intent = new Intent(this, BinderPoolActivity.class);
        startActivity(intent);
    }

}
