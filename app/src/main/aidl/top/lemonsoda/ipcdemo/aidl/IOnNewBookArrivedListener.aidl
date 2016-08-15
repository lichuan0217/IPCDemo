// IOnNewBookArrivedListener.aidl
package top.lemonsoda.ipcdemo.aidl;

import top.lemonsoda.ipcdemo.aidl.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
