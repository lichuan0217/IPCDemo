// ISecurityCenter.aidl
package top.lemonsoda.ipcdemo.binderpool;

interface ISecurityCenter {

    String encrypt(String content);
    String decrypt(String password);
}
