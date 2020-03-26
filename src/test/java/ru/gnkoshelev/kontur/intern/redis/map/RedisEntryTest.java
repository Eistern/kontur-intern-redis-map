package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class RedisEntryTest {

  @Test
  public void basicEntryTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");

    Set<Entry<String, String>> entrySet = map.entrySet();
    Entry<String, String> entry = entrySet.iterator().next();

    entry.setValue("next_test");
    Assert.assertTrue(map.containsValue("next_test"));
  }

  @Test
  public void equalsEntryTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");

    Set<Entry<String, String>> entrySet = map.entrySet();
    Entry<String, String> entry1 = entrySet.iterator().next();
    Entry<String, String> entry2 = entrySet.iterator().next();

    Assert.assertEquals(entry1, entry1);

    Assert.assertEquals(entry1, entry2);
    Assert.assertEquals(entry2, entry1);

    Assert.assertNotEquals(entry1, null);
  }

  @Test
  public void hashCodeEntryTest() {
    Map<String, String> map = new RedisMap();
    map.put("test", "test");

    Set<Entry<String, String>> entrySet = map.entrySet();
    Entry<String, String> entry1 = entrySet.iterator().next();
    Entry<String, String> entry2 = entrySet.iterator().next();

    Assert.assertEquals(entry1.hashCode(), entry1.hashCode());

    Assert.assertEquals(entry1, entry2);
    Assert.assertEquals(entry1.hashCode(), entry2.hashCode());
  }
}
