package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class RedisValuesTest {

  @Test
  public void valuesBasicTest() {
    Map<String, String> map = new RedisMap();
    Collection<String> values = map.values();

    map.put("test", "aaa");
    map.put("1", "aaa");

    Assert.assertEquals(2, values.size());
    Assert.assertTrue(values.contains("aaa"));

    map.clear();
    Assert.assertEquals(0, values.size());
    Assert.assertTrue(values.isEmpty());
    Assert.assertFalse(map.containsValue("aaa"));
    Assert.assertTrue(map.isEmpty());

    map.put("test", "test");
    Assert.assertFalse(map.isEmpty());
    Assert.assertFalse(values.isEmpty());

    values.clear();
    Assert.assertTrue(map.isEmpty());
    Assert.assertTrue(values.isEmpty());
  }

  @Test
  public void valuesIteratorTest() {
    Map<String, String> map = new RedisMap();
    Collection<String> values = map.values();

    map.put("first", "1");
    map.put("second", "2");

    Iterator<String> valuesIterator = values.iterator();
    Assert.assertTrue(valuesIterator.hasNext());

    String nextValue = valuesIterator.next();
    Assert.assertTrue(map.containsValue(nextValue));
    valuesIterator.remove();
    Assert.assertEquals(1, map.size());
    Assert.assertEquals(1, values.size());
    Assert.assertFalse(map.containsValue(nextValue));
  }

  @Test(expected = ConcurrentModificationException.class)
  public void valuesIteratorFailOnNextTest() {
    Map<String, String> map = new RedisMap();
    Collection<String> values = map.values();

    map.put("first", "1");
    map.put("second", "2");

    Iterator<String> valuesIterator = values.iterator();
    map.put("third", "3");
    Assert.assertTrue(valuesIterator.hasNext());
    valuesIterator.next();
  }

  @Test(expected = ConcurrentModificationException.class)
  public void valuesIteratorFailOnRemoveTest() {
    Map<String, String> map = new RedisMap();
    Collection<String> values = map.values();

    map.put("first", "1");
    map.put("second", "2");

    Iterator<String> valuesIterator = values.iterator();
    Assert.assertTrue(valuesIterator.hasNext());
    valuesIterator.next();
    map.put("third", "3");
    valuesIterator.remove();
  }
}
