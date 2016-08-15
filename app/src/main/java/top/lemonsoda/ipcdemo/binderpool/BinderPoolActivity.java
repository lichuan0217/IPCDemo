package top.lemonsoda.ipcdemo.binderpool;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import top.lemonsoda.ipcdemo.R;

public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = BinderPoolActivity.class.getCanonicalName();

    private ISecurityCenter securityCenter;
    private ICompute compute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }).start();
    }

    private void doWork() {
        BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);

        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        securityCenter = BinderPool.SecurityCenterImpl.asInterface(securityBinder);
        Log.d(TAG, "Call ISecurityCenter...");
        String msg = "Hello, Android";
        Log.d(TAG, "Content: " + msg);
        try {
            String password = securityCenter.encrypt(msg);
            Log.d(TAG, "Encrypt: " + password);
            Log.d(TAG, "Decrypt: " + securityCenter.decrypt(password));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Call ICompute...");
        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        compute = BinderPool.ComputeImpl.asInterface(computeBinder);
        try {
            Log.d(TAG, "Compute: 3 + 5 = " + compute.add(3, 5));
        }catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
