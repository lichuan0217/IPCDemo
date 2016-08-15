// IBookManager.aidl
package top.lemonsoda.ipcdemo.aidl;

import top.lemonsoda.ipcdemo.aidl.Book;
import top.lemonsoda.ipcdemo.aidl.IOnNewBookArrivedListener;

interface IBookManager {
    void addBook(in Book book);
    List<Book> getBookList();
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}
