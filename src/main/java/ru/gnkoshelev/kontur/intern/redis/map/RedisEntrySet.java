package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

final class RedisEntrySet extends AbstractSet<Entry<String, String>> {

  private final RedisMap source;

  RedisEntrySet(RedisMap source) {
    this.source = source;
  }

  @Override
  public Iterator<Entry<String, String>> iterator() {
    return new RedisEntryIterator();
  }

  @Override
  public int size() {
    return source.size();
  }

  @Override
  public void clear() {
    source.clear();
  }

  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public boolean contains(Object o) {
    if (!(o instanceof Map.Entry)) {
      return false;
    }
    Map.Entry<?, ?> e = (Entry<?, ?>) o;
    return source.get(e.getKey()).equals(e.getValue());
  }

  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public boolean remove(Object o) {
    if (o instanceof Map.Entry) {
      Map.Entry<?, ?> e = (Entry<?, ?>) o;
      return source.remove(e.getKey(), e.getValue());
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof RedisEntrySet)) {
      return false;
    }

    return source.equals(((RedisEntrySet) o).source);
  }

  @Override
  public int hashCode() {
    return this.source.hashCode();
  }

  final class RedisEntryIterator extends RedisMapIterator
      implements Iterator<Entry<String, String>> {

    RedisEntryIterator() {
      super(source);
    }

    @Override
    public Entry<String, String> next() {
      return super.nextEntry();
    }
  }
}
