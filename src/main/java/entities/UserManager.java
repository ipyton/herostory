package entities;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    static private final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    private UserManager(){}



    static public void addUser(User u) {
        if (null != u) {
            _userMap.putIfAbsent(u.userID, u);
        }
    }

    static public void removeByUserId(int userID) {
        _userMap.remove(userID);
    }

    static public Collection<User> listUser() {
        return _userMap.values();
    }

    static public User getByUserID(int userID){
        return _userMap.get(userID);
    }
}
