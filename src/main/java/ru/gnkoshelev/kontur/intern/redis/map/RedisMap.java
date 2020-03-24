package ru.gnkoshelev.kontur.intern.redis.map;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Transaction;

/**
 * @author Daniil Zulin
 */
public class RedisMap implements Map<String, String>, AutoCloseable {

  private static final String REDIS_IP = "192.168.1.129";
  private static final int REDIS_PORT = 6379;
  private static final String GENERATOR_ID = "redis-map-id-generator";
  private static final String CONNECTIONS_SUFFIX = ":connections";
  private static final String REDIS_MAP_PREFIX = "redis-map:";
  private static final String REDIS_DISCONNECT_SCRIPT =
      "local connections = redis.call(\"decr\", KEYS[2])\n"
          + "if connections == 0 then\n"
          + "\tredis.call(\"unlink\", KEYS[1])\n"
          + "\tredis.call(\"unlink\", KEYS[2])\n"
          + "end\n";
  private static final JedisPool jedisPool = new JedisPool(REDIS_IP, REDIS_PORT);
  private static final Cleaner redisMapCleaner = Cleaner.create();
  private static final ShutdownController shutdownController = new ShutdownController();
  private final MapCleaner cleaner;
  private final Cleaner.Cleanable cleanable;
  private String MAP_ID;
  private int modificationCount = 0;

  static {
    Runtime.getRuntime().addShutdownHook(shutdownController);
  }

  public RedisMap() {
    try (Jedis jedisConnection = jedisPool.getResource()) {
      MAP_ID = REDIS_MAP_PREFIX + jedisConnection.incr(GENERATOR_ID);
      jedisConnection.incr(MAP_ID + CONNECTIONS_SUFFIX);
    }

    cleaner = new MapCleaner(MAP_ID);
    cleanable = redisMapCleaner.register(this, cleaner);

    shutdownController.registerMap(MAP_ID);
  }

  @SuppressWarnings("CopyConstructorMissesField")
  public RedisMap(RedisMap connectedMap) {
    this(connectedMap.MAP_ID);
  }

  public RedisMap(String connectionKey) {
    MAP_ID = connectionKey;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      jedisConnection.incr(MAP_ID + CONNECTIONS_SUFFIX);
    }

    cleaner = new MapCleaner(MAP_ID);
    cleanable = redisMapCleaner.register(this, cleaner);

    shutdownController.registerMap(MAP_ID);
  }

  @Override
  public void close() {
    cleanable.clean();
  }

  public String getMapKey() {
    return MAP_ID;
  }

  public void connectToRedisMap(String mapId) {
    cleaner.run();
    MAP_ID = mapId;
    modificationCount = 0;
    cleaner.setMapId(mapId);
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
      ScanResult<Map.Entry<String, String>> scanResult = jedisConnection.hscan(MAP_ID, "0");
      while (!result) {
        result = scanResult.getResult().parallelStream().anyMatch(e -> e.getValue().equals(value));
        if (scanResult.isCompleteIteration()) {
          return result;
        }
        scanResult = jedisConnection.hscan(MAP_ID, scanResult.getCursor());
      }
    }
    return true;
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

    modificationCount++;
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

    modificationCount++;
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

    modificationCount += m.size();
  }

  @Override
  public void clear() {
    try (Jedis jedisConnection = jedisPool.getResource()) {
      jedisConnection.unlink(MAP_ID);
    }
    modificationCount++;
  }

  @Override
  public Set<String> keySet() {
    return new RedisKeySet(this);
  }

  @Override
  public Collection<String> values() {
    return new RedisValueCollection(this);
  }

  @Override
  public Set<Entry<String, String>> entrySet() {
    return new RedisEntrySet(this);
  }

  int getModificationCount() {
    return modificationCount;
  }

  ScanResult<Map.Entry<String, String>> iterateOverMap(String iteratorKey) {
    ScanResult<Map.Entry<String, String>> scanResult;
    try (Jedis jedisConnection = jedisPool.getResource()) {
      scanResult = jedisConnection.hscan(MAP_ID, iteratorKey);
    }
    return scanResult;
  }

  private static final class ShutdownController extends Thread {

    private List<String> mapIds = new ArrayList<>();

    private void registerMap(String mapId) {
      mapIds.add(mapId);
    }

    private void removeMap(String mapId) {
      mapIds.remove(mapId);
    }

    @Override
    public void run() {
      try (Jedis jedisConnection = new Jedis(REDIS_IP, REDIS_PORT)) {
        for (String mapId : mapIds) {
          jedisConnection.eval(REDIS_DISCONNECT_SCRIPT, 2, mapId, mapId + CONNECTIONS_SUFFIX);
        }
      }
    }
  }

  private static final class MapCleaner implements Runnable {

    private volatile String MAP_ID;

    private MapCleaner(String mapId) {
      MAP_ID = mapId;
    }

    private void setMapId(String mapId) {
      MAP_ID = mapId;
    }

    @Override
    public void run() {
      if (MAP_ID == null) {
        Logger.getLogger(MapCleaner.class.getName()).warning("MAP ID IS NULL");
        return;
      }
      try (Jedis jedisConnection = new Jedis(REDIS_IP, REDIS_PORT)) {
        jedisConnection.eval(REDIS_DISCONNECT_SCRIPT, 2, MAP_ID, MAP_ID + CONNECTIONS_SUFFIX);
      }
      shutdownController.removeMap(MAP_ID);
      MAP_ID = null;
    }
  }
}
