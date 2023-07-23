package loginService.DB;

public interface IUserDAO {

    AccountInformation getByUserName(String userName);

    void insertInto(AccountInformation accountInformation);

}
