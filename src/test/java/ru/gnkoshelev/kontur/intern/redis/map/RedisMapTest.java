package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

    Assert.assertNull(map1.get("non-existent"));
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
  public void valuesTest() {
    Map<String, String> map = new HashMap<>();
    Collection<String> values = map.values();

    map.put("test", "aaa");
    map.put("1", "aaa");
    Assert.assertEquals(2, values.size());
    Assert.assertTrue(values.contains("aaa"));

    map.clear();
    Assert.assertEquals(0, values.size());
    Assert.assertFalse(values.contains("aaa"));
  }

  @Test
  public void keySeyTest() {
    Map<String, String> map = new RedisMap();
    Set<String> keys = map.keySet();

    map.put("test", "test");
    Assert.assertEquals(1, keys.size());
    Assert.assertTrue(keys.contains("test"));

    map.clear();
    Assert.assertEquals(0, keys.size());
    Assert.assertFalse(keys.contains("test"));
  }
}
