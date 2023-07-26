package loginService.DB;

public interface IUserDao {

    AccountInformation getByUserName(String userName);

    void insertInto(AccountInformation accountInformation);

}
