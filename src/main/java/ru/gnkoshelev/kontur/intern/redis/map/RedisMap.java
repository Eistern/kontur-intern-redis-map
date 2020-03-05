package ru.gnkoshelev.kontur.intern.redis.map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/** @author Gregory Koshelev */
public class RedisMap implements Map<String, String> {
  private static volatile JedisPool jedisPool = new JedisPool();
  private final String mapId;

  {
    try (Jedis jedisConnection = jedisPool.getResource()) {
      this.mapId = "redis-map:" + jedisConnection.incr("redis-map-id-generator").toString();
    }
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsKey(Object key) {
    if (key == null) throw new NullPointerException();

    if (!(key instanceof String)) throw new ClassCastException();

    Boolean result;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      result = jedisConnection.hexists(mapId, (String) key);
    }

    return result;
  }

  @Override
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String get(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String put(String key, String value) {
    String result = null;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      Transaction transaction = jedisConnection.multi();

      Response<String> previousValue = transaction.hget(mapId, key);
      Response<Long> checkResponse = transaction.hset(mapId, key, value);

      transaction.exec();

      if (checkResponse.get() == 0L) result = previousValue.get();
    }

    return result;
  }

  @Override
  public String remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends String, ? extends String> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> keySet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<String> values() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Entry<String, String>> entrySet() {
    throw new UnsupportedOperationException();
  }
}
