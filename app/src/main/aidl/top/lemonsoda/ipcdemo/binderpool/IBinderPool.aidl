// IBinderPool.aidl
package top.lemonsoda.ipcdemo.binderpool;

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}
