package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/** @author Gregory Koshelev */
public class RedisMap implements Map<String, String> {
  private static volatile JedisPool jedisPool = new JedisPool();
  private final String MAP_ID;

  {
    try (Jedis jedisConnection = jedisPool.getResource()) {
      this.MAP_ID = "redis-map:" + jedisConnection.incr("redis-map-id-generator").toString();
    }
  }

  @Override
  public int size() {
    Long length;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      length = jedisConnection.hlen(MAP_ID);
    }
    return length.intValue();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean containsKey(Object key) {
    if (key == null) throw new NullPointerException();

    if (!(key instanceof String)) throw new ClassCastException();

    boolean result;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      result = jedisConnection.hexists(MAP_ID, (String) key);
    }

    return result;
  }

  @Override
  public boolean containsValue(Object value) {
    if (value == null) {
      throw new NullPointerException();
    }

    if (!(value instanceof String)) {
      throw new ClassCastException();
    }

    boolean result = false;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      List<String> values = jedisConnection.hvals(MAP_ID);
      result = values.contains(value);
    }
    return result;
  }

  @Override
  public String get(Object key) {
    if (key == null) {
      throw new NullPointerException();
    }

    if (!(key instanceof String)) {
      throw new ClassCastException();
    }

    String result;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      result = jedisConnection.hget(MAP_ID, (String) key);
    }
    // TODO TEST 'nil' VALUE (Jedis.get SOMEHOW RETURNS JUST null :( )
    return result;
  }

  @Override
  public String put(String key, String value) {
    String result = null;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      Transaction transaction = jedisConnection.multi();

      Response<String> previousValue = transaction.hget(MAP_ID, key);
      Response<Long> setCheck = transaction.hset(MAP_ID, key, value);

      transaction.exec();

      if (setCheck.get() == 0L) {
        result = previousValue.get();
      }
    }

    return result;
  }

  @Override
  public String remove(Object key) {
    if (key == null) {
      throw new NullPointerException();
    }

    if (!(key instanceof String)) {
      throw new ClassCastException();
    }

    String result = null;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      Transaction transaction = jedisConnection.multi();

      Response<String> previousValue = transaction.hget(MAP_ID, (String) key);
      Response<Long> removedCheck = transaction.hdel(MAP_ID, (String) key);

      transaction.exec();

      if (removedCheck.get() == 1L) {
        result = previousValue.get();
      }
    }
    return result;
  }

  @Override
  public void putAll(Map<? extends String, ? extends String> m) {
    if (m.containsKey(null) || m.containsValue(null)) {
      throw new NullPointerException();
    }

    if (m.isEmpty()) {
      return;
    }

    try (Jedis jedisConnection = jedisPool.getResource()) {
      jedisConnection.hmset(MAP_ID, Collections.unmodifiableMap(m));
    }
  }

  @Override
  public void clear() {
    try (Jedis jedisConnection = jedisPool.getResource()) {
      jedisConnection.unlink(MAP_ID);
    }
  }

  @Override
  public Set<String> keySet() {
    // TODO HARD TASK
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<String> values() {
    // TODO HARD TASK
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Entry<String, String>> entrySet() {
    // TODO HARD TASK
    throw new UnsupportedOperationException();
  }
}
