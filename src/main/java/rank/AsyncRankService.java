package rank;

import async.AsyncOperationProcessor;
import async.IAsyncOperation;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import utils.RedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;


//basic info will also be stored in the redis db.
public class AsyncRankService {

    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncRankService.class);

    static private AsyncRankService _service = new AsyncRankService();

    private AsyncRankService(){}

    static public AsyncRankService getInstance(){
        return _service;
    }

    public void getRank(Function<List<RankItem>, Void> cb){
        if (cb == null) return;
        AsyncOperationProcessor.getInstance().process(new AsyncRankManipulator(){
            @Override
            public void doFinish() {
                cb.apply(this.getRankItemList());
            }
        });
    }

    public void refreshRank(int winnerID, int loserID) {
        if (winnerID <= 0 || loserID <= 0) return;

        try (Jedis redis = RedisUtil.getJedis()) {
            redis.hincrBy("User_" + winnerID, "Win", 1);
            redis.hincrBy("User_" + loserID, "Lose", 1);

            String winStr = redis.hget("User_" + winnerID, "Win");
            int winNum = Integer.parseInt(winStr);

            redis.zadd("Rank", winNum, String.valueOf(winnerID));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }


    static private class AsyncRankManipulator implements IAsyncOperation {

        private List<RankItem> _rankItemList;

        List<RankItem> getRankItemList() {
            return _rankItemList;
        }


        @Override
        public void doAsync() {
            try (Jedis redis = RedisUtil.getJedis()) {
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0 ,9);
                List<RankItem> rankItemList = new ArrayList<>();

                int i = 0;
                for (Tuple t : valSet) {
                    if (null == t) {
                        continue;
                    }
                    int userID = Integer.parseInt(t.getElement());
                    String jsonStr = redis.hget("User_" + userID, "BasicInfo");
                    if (null == jsonStr) continue;

                    RankItem newItem = new RankItem();
                    newItem.rankID = ++i;
                    newItem.userID = userID;
                    newItem.win = (int) t.getScore();

                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    newItem.userName = jsonObject.getString("userName");
                    newItem.avatar = jsonObject.getString("heroAvatar");

                    rankItemList.add(newItem);
                }
                _rankItemList = rankItemList;

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

}
