package loginService;

import async.AsyncOperationProcessor;
import async.IAsyncOperation;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.log.Log;
import loginService.DB.AccountInformation;
import loginService.DB.IUserDAO;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import utils.MySQLFactory;
import utils.RedisUtil;

import java.util.function.Function;

public class AsyncLoginService {
    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncLoginService.class);

    static private final AsyncLoginService _instance = new AsyncLoginService();



    private AsyncLoginService(){

    }

    static public AsyncLoginService getInstance() {
        return _instance;
    }

    public void userLogin(String userName, String password, Function<AccountInformation, Void> callback){
        if (null == userName || null == password) {
            return;
        }
        AsyncOperationProcessor.getInstance().process(new AsyncGetUserEntity(userName, password) {

            public void doFinish() {
                if (null != callback) {
                    callback.apply(this.getAccountInformation());
                }
            }
        });



    }

    private void updateBasicInfoInRedis(AccountInformation information){
        if(null == information) return;
        try (Jedis redis = RedisUtil.getJedis()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userName", information.userName);
            jsonObject.put("heroAvatar", information.heroAvatar);
            redis.hset("User_" + information.userID, "BasicInfo", jsonObject.toJSONString());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }


    //this class is used to process Operation
    static private class AsyncGetUserEntity implements IAsyncOperation {
        String _userName, _password;
        private AccountInformation _information;


        AsyncGetUserEntity(String username, String password){
            _userName = username;
            _password = password;
        }

        AccountInformation getAccountInformation() {
            return _information;
        }


        @Override
        public int getBindID() {
            if (null == _userName) {
                return 0;
            } else {
                return _userName.charAt(_userName.length() - 1);
            }
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySQLFactory.openSession()) {
                IUserDAO dao = mySqlSession.getMapper(IUserDAO.class);
                AccountInformation information = dao.getByUserName(_userName);

                if (null != information) {
                    if (! _password.equals(information.password)) {
                        throw new RuntimeException("password error");
                    }
                } else {
                    information = new AccountInformation();
                    information.userName = _userName;
                    information.password = _password;
                    information.heroAvatar = "Hero_Shaman";
                    dao.insertInto(information);
                }
                AsyncLoginService.getInstance().updateBasicInfoInRedis(information);
                _information = information;
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        @Override
        public void doFinish() {
            IAsyncOperation.super.doFinish();
        }
    }


}
