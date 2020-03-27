package ru.gnkoshelev.kontur.intern.redis.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import redis.clients.jedis.ScanResult;

abstract class RedisMapIterator {

  private final RedisMap source;
  private ScanResult<Entry<String, String>> scanResult = null;
  private Iterator<Entry<String, String>> mapIterator = null;
  private Entry<String, String> currentEntry = null;
  private int localModifications;

  RedisMapIterator(RedisMap source) {
    this.source = source;
    this.localModifications = source.getModificationCount();
  }

  public boolean hasNext() {
    if (scanResult == null) {
      scanResult = source.iterateOverMap("0");
    }
    if (mapIterator == null) {
      mapIterator = scanResult.getResult().iterator();
    }
    return mapIterator.hasNext() || !scanResult.isCompleteIteration();
  }

  public Entry<String, String> nextEntry() {
    if (localModifications != this.source.getModificationCount()) {
      throw new ConcurrentModificationException();
    }
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    while (!mapIterator.hasNext()) {
      scanResult = source.iterateOverMap(scanResult.getCursor());
      mapIterator = scanResult.getResult().iterator();
    }
    currentEntry = mapIterator.next();

    return new RedisEntry(source, currentEntry.getKey(), currentEntry.getValue());
  }

  public void remove() {
    if (currentEntry == null) {
      throw new IllegalStateException();
    }

    if (localModifications != this.source.getModificationCount()) {
      throw new ConcurrentModificationException();
    }

    source.remove(currentEntry.getKey(), currentEntry.getValue());
    currentEntry = null;
    mapIterator.remove();
    localModifications++;
  }
}
