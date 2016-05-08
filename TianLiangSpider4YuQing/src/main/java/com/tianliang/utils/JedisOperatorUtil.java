package com.tianliang.utils;

import org.apache.log4j.Logger; 

import redis.clients.jedis.Jedis;

import com.tianliang.spider.utils.SystemParas;

/*
 * redis操作工具类
 */
public class JedisOperatorUtil {
	public static Logger logger = Logger.getLogger(JedisOperatorUtil.class);
	private Jedis jedis = null;

	public JedisOperatorUtil(String host, int port, String redis_password) {
		// if (!SystemParas.application_is_test) {
		jedis = new Jedis(host, port);// 链接上redis
		jedis.auth(redis_password);
		logger.info("Redis客户端已成功连接至服务器端!");
		// } else {
		// logger.info("程序测试，不连接redis!");
		// }
	}

	public boolean putObj(byte[] key, byte[] value) {
		try {
			jedis.set(key, value);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			jedis.set(key, value);
		}
		return true;
	}

	public boolean putObj(String key, String value) {
		try {
			jedis.set(key, value);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			jedis.set(key, value);
		}
		return true;
	}

	public byte[] getObj(byte[] key) {
		try {
			if (key == null) {
				return null;
			}
			return jedis.get(key);
		} catch (Exception e) {
			logger.info("redis在getObj时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			return jedis.get(key);
		}
	}

	public String getObj(String key) {
		try {
			if (key == null) {
				return null;
			}
			return jedis.get(key);
		} catch (Exception e) {
			logger.info("redis在getObj时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			return jedis.get(key);
		}
	}

	// 将key/value放入一个队列中
	public boolean lpush(String key, String value) {
		try {
			jedis.lpush(key, value);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			jedis.lpush(key, value);
		}
		return true;
	}

	// 放进set中
	public boolean HSet(String key, String field, String value) {
		try {
			// 直接用这个，省去判重了
			jedis.hset(key, field, value);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			jedis.hset(key, field, value);
		}
		return true;
	}

	// 判断是否包括某个条目
	public boolean HContainsFields(String key, String field) {
		boolean status = false;
		try {
			// 直接用这个，省去判重了
			status = jedis.hexists(key, field);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			status = jedis.hexists(key, field);
		}
		return status;
	}

	// 从get出来
	public byte[] HGet(byte[] key, byte[] field) {
		try {
			return jedis.hget(key, field);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			return jedis.hget(key, field);
		}
	}

	// 传入字符串格式
	public String HGet(String key, String field) {
		try {
			return jedis.hget(key, field);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			return jedis.hget(key, field);
		}
	}

	// 从hdel出来,返回0说明没有对应的key，即不成功。返回1则代表删除成功
	public long HDel(byte[] key, byte[] field) {
		try {
			return jedis.hdel(key, field);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			return jedis.hdel(key, field);
		}
	}

	// 字节格式
	public boolean HSetnx(byte[] key, byte[] field, byte[] value) {
		try {
			// 直接用这个，省去判重了
			return jedis.hsetnx(key, field, value) == 0 ? false : true;
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			return jedis.hsetnx(key, field, value) == 0 ? false : true;
		}
	}

	// 将key/value放入一个队列中
	public boolean lpush(byte[] key, byte[] value) {
		try {
			jedis.lpush(key, value);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			jedis.lpush(key, value);
		}
		return true;
	}

	// 从某个队列中将key的值给拿出来
	public String rpop(String key) {
		String value = null;
		try {
			value = jedis.rpop(key);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			value = jedis.rpop(key);
		}
		return value;
	}

	// 从某个队列中将key的值给拿出来
	public byte[] rpop(byte[] key) {
		byte[] value = null;
		try {
			value = jedis.rpop(key);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			value = jedis.rpop(key);
		}
		return value;
	}

	// 取得一个key对应的队列的现有长度
	public long llen(String key) {
		long size = 0;
		try {
			size = jedis.llen(key);
		} catch (Exception e) {
			logger.info("redis在set时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			size = jedis.llen(key);
		}
		return size;
	}

	public void saveAll() {
		try {
			jedis.save();
		} catch (Exception e) {
			logger.info("redis在getObj时，出现异常，将重新联接一次");
			jedis = new Jedis(SystemParas.redis_host, SystemParas.redis_port);// 链接上redis
			jedis.auth(SystemParas.redis_password);
			logger.info("Redis客户端已成功连接至服务器端!");
			jedis.save();
		}
	}

	public static void main(String[] args) {
		// jedis.flushAll();
		JedisOperatorUtil jedisOperatorUtil = new JedisOperatorUtil(
				SystemParas.redis_host, SystemParas.redis_port,
				"sinx/cosx=tanx");
		for (int i = 0; i < 1; i++) {
			jedisOperatorUtil.putObj("k" + i, "v" + i);
		}
		for (int i = 0; i < 1; i++) {
			// jedisOperatorUtil.putObj("k" + i, "v" + i);
			System.out.println(jedisOperatorUtil.getObj("k" + i));
		}
		// String key = jedisOperatorUtil.getObj("vbv");
		// System.out.println(key);
		System.out.println("done!");
	}
}
