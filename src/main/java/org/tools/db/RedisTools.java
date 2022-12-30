package org.tools.db;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;

/**
 * The type Redis tools.
 */
public class RedisTools {
    static final String REDIS_URL = "localhost";
    static final Integer REDIS_PORT = 6379;

    public static void main(String[] args) {
        connectRedis();
    }

    public static void connectRedis() {
        Jedis jedis = null;
        try {
            jedis = new Jedis(REDIS_URL, REDIS_PORT);
            String response = jedis.ping();
            System.out.println("Redis连接状态：" + response);

            System.out.println("删除当前选择数据库中的所有Key:" + jedis.flushDB());

            jedis.set("name", "test");

            System.out.println("name键值存储的字符串为：" + jedis.get("name"));
            System.out.println("name 键是否存在：" + jedis.exists("name"));

            jedis.lpush("list", "num1");
            jedis.lpush("list", "num2");
            jedis.lpush("list", "num3");

            List<String> list = jedis.lrange("list", 0, -1);
            System.out.print("列表项：");
            for (String s : list) {
                System.out.print(s+" ");
            }
            System.out.println();

            Set<String> keys = jedis.keys("*");
            System.out.println("当前选择数据库中所有的键：" + keys);

            System.out.println("事务测试...");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("hello","world");
            String result = jsonObject.toString();

            System.out.println("开启事务");
            Transaction multi = jedis.multi();
            try {
                multi.set("user1", result);
                multi.set("user2", result);
                System.out.println("执行事务");
                multi.exec();
            } catch (Exception exception){
                System.out.println("回滚");
                multi.discard();
                exception.printStackTrace();
            }
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }
}
