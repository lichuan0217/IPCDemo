package top.lemonsoda.ipcdemo.binderpool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

/**
 * Created by chuanl on 8/15/16.
 */
public class BinderPool {

    public static final String TAG = BinderPool.class.getCanonicalName();
    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;

    private Context context;
    private IBinderPool binderPool;
    private static volatile BinderPool instance;
    private CountDownLatch connectBinderPoolCountDownLatch;

    private BinderPool(Context cxt) {
        context = cxt.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (instance == null) {
            synchronized (BinderPool.class) {
                if (instance == null) {
                    instance = new BinderPool(context);
                }
            }
        }
        return instance;
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (binderPool != null) {
                binder = binderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private synchronized void connectBinderPoolService() {
        connectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(context, BinderPoolService.class);
        context.bindService(intent, binderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            connectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection binderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binderPool = IBinderPool.Stub.asInterface(iBinder);
            try {
                binderPool.asBinder().linkToDeath(binderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            connectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private IBinder.DeathRecipient binderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "Binder Died...");
            binderPool.asBinder().unlinkToDeath(binderPoolDeathRecipient, 0);
            binderPool = null;
            connectBinderPoolService();
        }
    };

    public static class BinderPoolImpl extends IBinderPool.Stub {

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
                    break;
                case BINDER_SECURITY_CENTER:
                    binder = new SecurityCenterImpl();
                    break;
                default:
                    break;
            }
            return binder;
        }
    }

    public static class SecurityCenterImpl extends ISecurityCenter.Stub {

        private static final char SECURITY_CODE = '^';

        @Override
        public String encrypt(String content) throws RemoteException {
            char[] chars = content.toCharArray();
            for (int i = 0; i < chars.length; ++i) {
                chars[i] ^= SECURITY_CODE;
            }
            return new String(chars);
        }

        @Override
        public String decrypt(String password) throws RemoteException {
            return encrypt(password);
        }
    }

    public static class ComputeImpl extends ICompute.Stub {

        @Override
        public int add(int a, int b) throws RemoteException {
            return a + b;
        }
    }
}
