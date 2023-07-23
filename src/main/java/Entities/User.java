package Entities;

public class User {
    public int userID;
    public String userName;
    public String heroAvatar;
    public int currHP;
    public final MoveState state = new MoveState();
}
