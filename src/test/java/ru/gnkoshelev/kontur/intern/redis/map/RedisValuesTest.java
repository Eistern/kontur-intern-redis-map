package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class RedisValuesTest {

  @Test
  public void valuesBasicTest() {
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
}
