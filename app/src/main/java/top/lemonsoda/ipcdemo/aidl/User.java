package top.lemonsoda.ipcdemo.aidl;

/**
 * Created by chuanl on 8/12/16.
 */
public class User {
    public int userId;
    public String userName;
    public boolean isMale;

    public User(){}

    @Override
    public String toString() {
        return "UserId: " + userId + " UserName: " + userName + " sex: " + (isMale ? "Male" : "Female");
    }
}
