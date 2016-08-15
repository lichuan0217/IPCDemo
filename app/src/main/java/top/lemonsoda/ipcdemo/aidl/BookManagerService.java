package top.lemonsoda.ipcdemo.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

    private static final String TAG = BookManagerService.class.getCanonicalName();

    private AtomicBoolean isServiceDestroyed = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();
//    private CopyOnWriteArrayList<IOnNewBookArrivedListener> listenerList = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<IOnNewBookArrivedListener> newBookArrivedListenerList = new RemoteCallbackList<>();


    private Binder binder = new IBookManager.Stub() {

        @Override
        public void addBook(Book book) throws RemoteException {
            bookList.add(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
//            if (!listenerList.contains(listener)) {
//                listenerList.add(listener);
//            } else {
//                Log.d(TAG, "listener already exists");
//            }
//            Log.d(TAG, "RegisterListener, size: " + listenerList.size());

            newBookArrivedListenerList.register(listener);

            Log.d(TAG, "RegisterListener, size: " + newBookArrivedListenerList.beginBroadcast());
            newBookArrivedListenerList.finishBroadcast();
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
//            if (listenerList.contains(listener)) {
//                listenerList.remove(listener);
//                Log.d(TAG, "unRegisterListener succeed");
//            } else {
//                Log.d(TAG, "listener not found, can not unregister");
//            }
//            Log.d(TAG, "unRegisterListener, size: " + listenerList.size());

            newBookArrivedListenerList.unregister(listener);
            Log.d(TAG, "unRegisterListener, size: " + newBookArrivedListenerList.beginBroadcast());
            newBookArrivedListenerList.finishBroadcast();
        }
    };

    public BookManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bookList.add(new Book(0, "Android"));
        bookList.add(new Book(1, "IOS"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        isServiceDestroyed.set(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        bookList.add(book);
//        Log.d(TAG, "onNewBookArrived, notify listener: " + listenerList.size());
//        for (IOnNewBookArrivedListener listener : listenerList) {
//            Log.d(TAG, "onNewBookArrived, notify listener: " + listener);
//            listener.onNewBookArrived(book);
//        }

        final int N = newBookArrivedListenerList.beginBroadcast();
        Log.d(TAG, "onNewBookArrived, notify listener: " + N);
        for (int i = 0; i < N; ++i) {
            IOnNewBookArrivedListener listener = newBookArrivedListenerList.getBroadcastItem(i);
            if (listener != null) {
                Log.d(TAG, "onNewBookArrived, notify listener: " + listener);
                listener.onNewBookArrived(book);
            }
        }
        newBookArrivedListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!isServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = bookList.size() + 1;
                Book newBook = new Book(bookId, "new book #" + bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
