package utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MySQLFactory {
    static private final Logger LOGGER = LoggerFactory.getLogger(MySQLFactory.class);

    static private SqlSessionFactory _sqlSessionFactory;

    private MySQLFactory(){
    }

    static public void init() {
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(
                    Resources.getResourceAsStream("MyBatisConfig.xml")
            );
            SqlSession tempSession = openSession();
            tempSession.getConnection().createStatement().execute("select -1");
            tempSession.close();

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    static public SqlSession openSession() {
        if (null == _sqlSessionFactory) throw new RuntimeException("_sqlSessionFactory");
        return _sqlSessionFactory.openSession(true);
    }





}
