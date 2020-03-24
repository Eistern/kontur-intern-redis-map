package ru.gnkoshelev.kontur.intern.redis.map;


import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class RedisKeySetTest {

  @Test
  public void keySetBasicTest() {
    Map<String, String> map = new RedisMap();
    Set<String> keys = map.keySet();

    map.put("test", "test");
    Assert.assertEquals(1, keys.size());
    Assert.assertTrue(keys.contains("test"));

    map.clear();
    Assert.assertEquals(0, keys.size());
    Assert.assertFalse(keys.contains("test"));

    map.put("remove", "remove");
    Assert.assertTrue(keys.remove("remove"));
    Assert.assertTrue(keys.isEmpty());
    Assert.assertTrue(map.isEmpty());

    map.put("existent", "test");
    Assert.assertFalse(keys.remove("non_existent"));
    keys.clear();
    Assert.assertTrue(map.isEmpty());
    Assert.assertTrue(keys.isEmpty());
  }

  @Test
  public void keySetIteratorTest() {
    Map<String, String> map = new RedisMap();
    Set<String> keys = map.keySet();

    Iterator<String> keysIterator = keys.iterator();
    Assert.assertFalse(keysIterator.hasNext());

    map.put("test", "test");
    keysIterator = keys.iterator();
    Assert.assertTrue(keysIterator.hasNext());
    String nextKey = keysIterator.next();
    Assert.assertTrue(map.containsKey(nextKey));

    keysIterator.remove();
    Assert.assertTrue(keys.isEmpty());
    Assert.assertTrue(map.isEmpty());
  }

  @Test(expected = ConcurrentModificationException.class)
  public void keySetIteratorFailOnNextTest() {
    Map<String, String> map = new RedisMap();
    Set<String> keys = map.keySet();

    Iterator<String> keysIterator = keys.iterator();
    Assert.assertFalse(keysIterator.hasNext());

    map.put("test", "test");
    keysIterator.next();
  }

  @Test(expected = ConcurrentModificationException.class)
  public void keySetIteratorFailOnRemoveTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");
    Set<String> keys = map.keySet();

    Iterator<String> keysIterator = keys.iterator();
    Assert.assertTrue(keysIterator.hasNext());
    keysIterator.next();

    map.put("fail_test", "fail_test");
    keysIterator.remove();
  }
}
