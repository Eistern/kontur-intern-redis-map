package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class RedisEntrySetTest {

  @Test
  public void entrySetBasicTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");

    Set<Entry<String, String>> entrySet = map.entrySet();
    Assert.assertEquals(map.size(), entrySet.size());
    Assert.assertTrue(entrySet.contains(new HashMap.SimpleEntry<>("test", "test")));

    Assert.assertTrue(entrySet.remove(new HashMap.SimpleEntry<>("test", "test")));
    Assert.assertTrue(entrySet.isEmpty());
    Assert.assertTrue(map.isEmpty());

    map.put("next_test", "next_test");
    Assert.assertEquals(1, entrySet.size());
    Assert.assertFalse(entrySet.remove(new HashMap.SimpleEntry<>("non_existent", "test")));

    entrySet.clear();
    Assert.assertTrue(map.isEmpty());
    Assert.assertTrue(entrySet.isEmpty());
  }

  @Test
  public void entrySetIteratorTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");

    Set<Entry<String, String>> entrySet = map.entrySet();
    Iterator<Entry<String, String>> entryIterator = entrySet.iterator();
    Entry<String, String> entry = entryIterator.next();

    Assert.assertTrue(map.containsKey(entry.getKey()));
    Assert.assertTrue(map.containsValue(entry.getValue()));

    entryIterator.remove();
    Assert.assertTrue(entrySet.isEmpty());
    Assert.assertTrue(map.isEmpty());
  }

  @Test(expected = ConcurrentModificationException.class)
  public void entrySetIteratorFailOnRemoveTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");

    Set<Entry<String, String>> entrySet = map.entrySet();
    Iterator<Entry<String, String>> entryIterator = entrySet.iterator();

    entryIterator.next();
    map.put("fail-test", "fail-test");
    entryIterator.remove();
  }

  @Test(expected = ConcurrentModificationException.class)
  public void entrySetIteratorFailOnNextTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");

    Set<Entry<String, String>> entrySet = map.entrySet();
    Iterator<Entry<String, String>> entryIterator = entrySet.iterator();

    entryIterator.next();
    map.put("fail-test", "fail-test");
    entryIterator.next();
  }
}
