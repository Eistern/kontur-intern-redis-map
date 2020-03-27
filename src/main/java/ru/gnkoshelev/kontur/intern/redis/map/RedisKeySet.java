package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.AbstractSet;
import java.util.Iterator;

final class RedisKeySet extends AbstractSet<String> {

  private RedisMap source;

  RedisKeySet(RedisMap source) {
    this.source = source;
  }

  @Override
  public int size() {
    return source.size();
  }

  @Override
  public boolean contains(Object o) {
    return source.containsKey(o);
  }

  @Override
  public Iterator<String> iterator() {
    return new RedisKeyIterator();
  }

  @Override
  public boolean remove(Object o) {
    return source.remove(o) != null;
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
    if (!(o instanceof RedisKeySet)) {
      return false;
    }

    return source.equals(((RedisKeySet) o).source);
  }

  @Override
  public int hashCode() {
    return this.source.hashCode();
  }

  final class RedisKeyIterator extends RedisMapIterator implements Iterator<String> {

    RedisKeyIterator() {
      super(source);
    }

    public String next() {
      return super.nextEntry().getKey();
    }
  }
}
