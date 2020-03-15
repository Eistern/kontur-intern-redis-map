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
    return source.pullKeys().size();
  }

  @Override
  public boolean contains(Object o) {
    return source.pullKeys().contains(o);
  }

  @Override
  public Iterator<String> iterator() {
    return null;
  }

  @Override
  public boolean remove(Object o) {
    return source.remove(o) != null;
  }

  @Override
  public void clear() {
    source.clear();
  }
}
