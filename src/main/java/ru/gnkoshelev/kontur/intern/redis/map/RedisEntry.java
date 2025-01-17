package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.Map.Entry;

class RedisEntry implements Entry<String, String> {

  private final RedisMap source;
  private String key;
  private String value;

  RedisEntry(RedisMap source, String key, String value) {
    this.source = source;
    this.key = key;
    this.value = value;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public String setValue(String value) {
    String oldValue = source.put(this.key, value);
    this.value = value;
    return oldValue;
  }

  @Override
  public int hashCode() {
    return source.hashCode() + key.hashCode() + value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof RedisEntry)) {
      return false;
    }
    return key.equals(((RedisEntry) obj).key)
        && value.equals(((RedisEntry) obj).value)
        && source.equals(((RedisEntry) obj).source);
  }
}
