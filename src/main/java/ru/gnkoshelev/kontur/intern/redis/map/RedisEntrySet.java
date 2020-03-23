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

  final class RedisEntryIterator extends RedisMapIterator implements
      Iterator<Entry<String, String>> {

    RedisEntryIterator() {
      super(source);
    }

    @Override
    public Entry<String, String> next() {
      return super.nextEntry();
    }
  }
}
