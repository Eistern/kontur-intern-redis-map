package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.AbstractCollection;
import java.util.Iterator;

final class RedisValueCollection extends AbstractCollection<String> {

  private final RedisMap source;

  RedisValueCollection(RedisMap source) {
    this.source = source;
  }

  @Override
  public Iterator<String> iterator() {
    return new RedisValueIterator();
  }

  @Override
  public int size() {
    return source.size();
  }

  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public boolean contains(Object o) {
    return source.containsValue(o);
  }

  @Override
  public void clear() {
    source.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof RedisValueCollection)) {
      return false;
    }

    return source.equals(((RedisValueCollection) o).source);
  }

  @Override
  public int hashCode() {
    return this.source.hashCode();
  }

  final class RedisValueIterator extends RedisMapIterator implements Iterator<String> {

    RedisValueIterator() {
      super(source);
    }

    public String next() {
      return super.nextEntry().getValue();
    }
  }
}
