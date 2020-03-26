package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daniil Zulin
 */
public class RedisMapTest {
  @Test
  public void baseTests() {
    Map<String, String> map1 = new RedisMap();
    Map<String, String> map2 = new RedisMap();

    map1.put("one", "1");

    map2.put("one", "ONE");
    map2.put("two", "TWO");

    Assert.assertNull(map1.get("non_existent"));
    Assert.assertEquals("1", map1.get("one"));
    Assert.assertEquals(1, map1.size());
    Assert.assertEquals(2, map2.size());

    map1.put("one", "first");

    Assert.assertEquals("first", map1.get("one"));
    Assert.assertEquals(1, map1.size());

    Assert.assertTrue(map1.containsValue("first"));
    Assert.assertTrue(map1.containsKey("one"));
    Assert.assertFalse(map1.containsKey("two"));

    Assert.assertTrue(map2.containsKey("one"));
    Assert.assertEquals("ONE", map2.remove("one"));
    Assert.assertFalse(map2.containsKey("one"));

    Map<String, String> map = new RedisMap();
    map.putAll(map1);
    Assert.assertTrue(map.containsKey("one"));
    Assert.assertTrue(map.containsValue("first"));
  }

  @Test(expected = NullPointerException.class)
  public void containsNullKeyTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");
    boolean result = map.containsKey(null);
  }

  @Test(expected = NullPointerException.class)
  public void containsNullValueTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");
    boolean result = map.containsValue(null);
  }

  @Test
  public void connectTest() {
    RedisMap map = new RedisMap();
    map.put("first", "1");
    map.put("second", "2");
    map.put("third", "3");

    Map<String, String> copiedMap = new RedisMap(map);
    Map<String, String> copiedByKeyMap = new RedisMap(map.getMapKey());
    RedisMap postCopiedMap = new RedisMap();
    postCopiedMap.connectToRedisMap(map.getMapKey());

    Assert.assertEquals(map, copiedMap);
    Assert.assertEquals(map, copiedByKeyMap);
    Assert.assertEquals(map, postCopiedMap);

    Assert.assertEquals(copiedMap.get("first"), map.get("first"));
    Assert.assertEquals(copiedByKeyMap.get("first"), map.get("first"));
    Assert.assertEquals(postCopiedMap.get("first"), map.get("first"));

    copiedMap.remove("first");
    Assert.assertEquals(2, map.size());
    Assert.assertEquals(2, copiedByKeyMap.size());
    Assert.assertEquals(2, postCopiedMap.size());

    copiedByKeyMap.remove("second");
    Assert.assertEquals(1, map.size());
    Assert.assertEquals(1, copiedMap.size());
    Assert.assertEquals(1, postCopiedMap.size());

    postCopiedMap.remove("third");
    Assert.assertTrue(map.isEmpty());
    Assert.assertTrue(copiedMap.isEmpty());
    Assert.assertTrue(copiedByKeyMap.isEmpty());
  }
}
