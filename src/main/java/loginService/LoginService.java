package loginService;

import Entities.MySQLFactory;
import loginService.DB.AccountInformation;
import loginService.DB.IUserDAO;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LoginService {
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    static private final LoginService _instance = new LoginService();

    private LoginService() {
    }

    static public LoginService getInstance() {
        return _instance;
    }

    public AccountInformation userLogin(String userName, String password) {
        if (null == userName || null == password) {
            return null;
        }
        try (SqlSession session = MySQLFactory.openSession()) {
            IUserDAO dao = session.getMapper(IUserDAO.class);

            AccountInformation account = dao.getByUserName(userName);

            LOGGER.info("current thread= {}", Thread.currentThread().getName());

            if (null != account) {
                if (!password.equals(account.password)) {
                    throw new RuntimeException("password error");
                }
            } else {
                account = new AccountInformation();
                account.userName = userName;
                account.password = password;
                account.heroAvatar = "Hero_Shaman";
                dao.insertInto(account);
            }
            return account;
        }catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

}
