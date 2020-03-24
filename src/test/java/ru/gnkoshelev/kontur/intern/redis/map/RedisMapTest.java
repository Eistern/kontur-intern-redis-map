package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

    Assert.assertTrue(map1.containsKey("one"));
    Assert.assertFalse(map1.containsKey("two"));
  }

  @Test
  public void connectTest() {
    RedisMap map = new RedisMap();
    map.put("first", "1");

    Map<String, String> copiedMap = new RedisMap(map);
    Map<String, String> copiedByKeyMap = new RedisMap(map.getMapKey());
    RedisMap postCopiedMap = new RedisMap();
    postCopiedMap.connectToRedisMap(map.getMapKey());

    Assert.assertEquals(copiedMap.get("first"), map.get("first"));
    Assert.assertEquals(copiedByKeyMap.get("first"), map.get("first"));
    Assert.assertEquals(postCopiedMap.get("first"), map.get("first"));
  }
}
