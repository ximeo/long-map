package de.comparus.opensource.longmap;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a linked list implementation of a LongMap. There are existing two main implementation of mapping
 * in Java:
 *    1. based on using trees
 *    2. based on using arrays and linked-lists.
 * This implementation of LongMap based on triple-linked list known as Red-Black Tree. This implementation is similar
 * to the implementation of a tree-map, but adapted for a specific task (storage of longs as keys) and does not use
 * comparators, preferring natural ordering.
 * I have also implemented LongMap by array method using nested arrays (You can see this implementation in the package
 * alternative) but during testing it turned out that it is much slower and takes up much more memory than this
 * implementation based on using trees.
 * Therefore, the method used in this class is the most successful in terms of efficiency.
 */
public class LongMapImpl<V> implements LongMap<V> {

  private static final boolean BLACK = true;
  private static final boolean RED = false;

  private Entry<V> rootEntry;
  private long size = 0;

  @Override
  public V put(long key, V value) {
    if (this.rootEntry == null) {
      this.rootEntry = new Entry<>(key, value, null);
      this.size = 1;
      return null;
    }
    return supplementTree(key, value);
  }

  @Override
  public V get(long key) {
    Entry<V> entry = getEntry(key);
    return (entry == null ? null : entry.value);
  }

  @Override
  public V remove(long key) {
    Entry<V> entry = getEntry(key);
    if (entry == null) {
      return null;
    }
    V oldValue = entry.value;
    deleteEntry(entry);
    return oldValue;
  }

  @Override
  public boolean isEmpty() {
    return this.size == 0;
  }

  @Override
  public boolean containsKey(long key) {
    return getEntry(key) != null;
  }

  @Override
  public boolean containsValue(V value) {
    return containValue(rootEntry, value);
  }

  @Override
  public long[] keys() {
    List<Long> res = new ArrayList<>((int)size);
    collectKeys(rootEntry, res);
    return res.stream().mapToLong(l -> l).toArray();
  }

  @Override
  public V[] values() {
    List<V> res = new ArrayList<>((int)size);
    collectValues(rootEntry, res);
    return new GenericArray<V>(res.toArray()).get();
  }

  @Override
  public long size() {
    return this.size;
  }

  @Override
  public void clear() {
    this.size = 0;
    this.rootEntry = null;
  }

  private V supplementTree(long key, V value) {
    Entry<V> currentEntry = this.rootEntry;
    long compare;
    Entry<V> parentEntry;
    do {
      parentEntry = currentEntry;
      compare = key - currentEntry.key;
      if (compare < 0) {
        currentEntry = currentEntry.leftChild;
      } else if (compare > 0) {
        currentEntry = currentEntry.rightChild;
      } else {
        return currentEntry.setValue(value);
      }
    } while (currentEntry != null);
    Entry<V> entry = new Entry<>(key, value, parentEntry);
    if (compare < 0) {
      parentEntry.leftChild = entry;
    } else {
      parentEntry.rightChild = entry;
    }
    fixTreeAfterInsertion(entry);
    this.size++;
    return value;
  }

  private Entry<V> getEntry(long key) {
    Entry<V> entry = rootEntry;
    while (entry != null) {
      long compare = key - entry.key;
      if (compare < 0) {
        entry = entry.leftChild;
      } else if (compare > 0) {
        entry = entry.rightChild;
      } else {
        return entry;
      }
    }
    return null;
  }

  private void deleteEntry(Entry<V> entry) {
    this.size--;
    if (entry.leftChild != null && entry.rightChild != null) {
      Entry<V> success = makeSuccessful(entry);
      entry.key = success.key;
      entry.value = success.value;
      entry = success;
    }
    Entry<V> replacement = (entry.leftChild != null ? entry.leftChild : entry.rightChild);
    if (replacement != null) {
      replacement.parent = entry.parent;
      if (entry.parent == null) {
        rootEntry = replacement;
      } else if (entry == entry.parent.leftChild) {
        entry.parent.leftChild = replacement;
      } else {
        entry.parent.rightChild = replacement;
      }
      entry.leftChild = entry.rightChild = entry.parent = null;

      if (entry.isBlack) {
        fixTreeAfterDeletion(replacement);
      }
    } else if (entry.parent == null) {
      rootEntry = null;
    } else {
      if (entry.isBlack) {
        fixTreeAfterDeletion(entry);
      }
      if (entry.parent != null) {
        if (entry == entry.parent.leftChild) {
          entry.parent.leftChild = null;
        } else if (entry == entry.parent.rightChild) {
          entry.parent.rightChild = null;
        }
        entry.parent = null;
      }
    }
  }

  private void fixTreeAfterInsertion(Entry<V> entry) {
    entry.isBlack = false;
    while (entry != null && entry != rootEntry && !entry.parent.isBlack) {
      if (parentOf(entry) == leftOf(parentOf(parentOf(entry)))) {
        Entry<V> currentEntry = rightOf(parentOf(parentOf(entry)));
        if (!colorOf(entry)) {
          setColor(parentOf(entry), BLACK);
          setColor(currentEntry, BLACK);
          setColor(parentOf(parentOf(entry)), RED);
          entry = parentOf(parentOf(entry));
        } else {
          if (entry == rightOf(parentOf(entry))) {
            entry = parentOf(entry);
            rotateLeft(entry);
          }
          setColor(parentOf(entry), BLACK);
          setColor(parentOf(parentOf(entry)), RED);
          rotateRight(parentOf(parentOf(entry)));
        }
      } else {
        Entry<V> currentEntry = leftOf(parentOf(parentOf(entry)));
        if (!colorOf(currentEntry)) {
          setColor(parentOf(entry), BLACK);
          setColor(currentEntry, BLACK);
          setColor(parentOf(parentOf(entry)), RED);
          entry = parentOf(parentOf(entry));
        } else {
          if (entry == leftOf(parentOf(entry))) {
            entry = parentOf(entry);
            rotateRight(entry);
          }
          setColor(parentOf(entry), BLACK);
          setColor(parentOf(parentOf(entry)), RED);
          rotateLeft(parentOf(parentOf(entry)));
        }

      }
    }
    rootEntry.isBlack = true;
  }

  private void fixTreeAfterDeletion(Entry<V> entry) {
    while (entry != rootEntry && colorOf(entry)) {
      if (entry == leftOf(parentOf(entry))) {
        Entry<V> currentEntry = rightOf(parentOf(entry));
        if (colorOf(currentEntry) == RED) {
          setColor(currentEntry, BLACK);
          setColor(parentOf(entry), RED);
          rotateLeft(parentOf(entry));
          currentEntry = rightOf(parentOf(entry));
        }
        if (colorOf(leftOf(currentEntry)) == BLACK &&
                colorOf(rightOf(currentEntry)) == BLACK) {
          setColor(currentEntry, RED);
          entry = parentOf(entry);
        } else {
          if (colorOf(rightOf(currentEntry)) == BLACK) {
            setColor(leftOf(currentEntry), BLACK);
            setColor(currentEntry, RED);
            rotateRight(currentEntry);
            currentEntry = rightOf(parentOf(entry));
          }
          setColor(currentEntry, colorOf(parentOf(entry)));
          setColor(parentOf(entry), BLACK);
          setColor(rightOf(currentEntry), BLACK);
          rotateLeft(parentOf(entry));
          entry = rootEntry;
        }
      } else {
        Entry<V> currentEntry = leftOf(parentOf(entry));
        if (colorOf(currentEntry) == RED) {
          setColor(currentEntry, BLACK);
          setColor(parentOf(entry), RED);
          rotateRight(parentOf(entry));
          currentEntry = leftOf(parentOf(entry));
        }
        if (colorOf(rightOf(currentEntry)) == BLACK && colorOf(leftOf(currentEntry)) == BLACK) {
          setColor(currentEntry, RED);
          entry = parentOf(entry);
        } else {
          if (colorOf(leftOf(currentEntry)) == BLACK) {
            setColor(rightOf(currentEntry), BLACK);
            setColor(currentEntry, RED);
            rotateLeft(currentEntry);
            currentEntry = leftOf(parentOf(entry));
          }
          setColor(currentEntry, colorOf(parentOf(entry)));
          setColor(parentOf(entry), BLACK);
          setColor(leftOf(currentEntry), BLACK);
          rotateRight(parentOf(entry));
          entry = rootEntry;
        }
      }
    }
    setColor(entry, BLACK);
  }

  private static <V> Entry<V> parentOf(Entry<V> entry) {
    return (entry == null ? null : entry.parent);
  }

  private static <V> Entry<V> leftOf(Entry<V> entry) {
    return (entry == null ? null : entry.leftChild);
  }

  private static <V> Entry<V> rightOf(Entry<V> entry) {
    return (entry == null ? null : entry.rightChild);
  }

  private static <V> boolean colorOf(Entry<V> entry) {
    return (entry == null ? BLACK : entry.isBlack);
  }

  private static <V> void setColor(Entry<V> entry, boolean c) {
    if (entry != null)
      entry.isBlack = c;
  }

  private void rotateLeft(Entry<V> entry) {
    if (entry != null) {
      Entry<V> r = entry.rightChild;
      entry.rightChild = r.leftChild;
      if (r.leftChild != null)
        r.leftChild.parent = entry;
      r.parent = entry.parent;
      if (entry.parent == null)
        rootEntry = r;
      else if (entry.parent.leftChild == entry)
        entry.parent.leftChild = r;
      else
        entry.parent.rightChild = r;
      r.leftChild = entry;
      entry.parent = r;
    }
  }

  private void rotateRight(Entry<V> entry) {
    if (entry != null) {
      Entry<V> leftChild = entry.leftChild;
      entry.leftChild = leftChild.rightChild;
      if (leftChild.rightChild != null) leftChild.rightChild.parent = entry;
      leftChild.parent = entry.parent;
      if (entry.parent == null) {
        rootEntry = leftChild;
      } else if (entry.parent.rightChild == entry) {
        entry.parent.rightChild = leftChild;
      } else {
        entry.parent.leftChild = leftChild;
      }
      leftChild.rightChild = entry;
      entry.parent = leftChild;
    }
  }

  private void collectValues(Entry<V> entry, List<V> entries) {
    if (entry != null) {
      entries.add(entry.value);
      if (entry.leftChild != null) {
        collectValues(entry.leftChild, entries);
      }
      if (entry.rightChild != null) {
        collectValues(entry.rightChild, entries);
      }
    }
  }

  private void collectKeys(Entry<V> entry, List<Long> keys) {
    if (entry != null) {
      keys.add(entry.key);
      if (entry.leftChild != null) {
        collectKeys(entry.leftChild, keys);
      }
      if (entry.rightChild != null) {
        collectKeys(entry.rightChild, keys);
      }
    }
  }

  private boolean containValue(Entry<V> entry, V value) {
    if (entry != null) {
      if (value == entry.value || value.equals(entry.value)) {
        return true;
      }
      return containValue(entry.leftChild, value) || containValue(entry.rightChild, value);
    }
    return false;
  }

  private static <V> Entry<V> makeSuccessful(Entry<V> entry) {
    if (entry == null)
      return null;
    else if (entry.rightChild != null) {
      Entry<V> rightChild = entry.rightChild;
      while (rightChild.leftChild != null)
        rightChild = rightChild.leftChild;
      return rightChild;
    } else {
      Entry<V> parentEntry = entry.parent;
      Entry<V> childEntry = entry;
      while (parentEntry != null && childEntry == parentEntry.rightChild) {
        childEntry = parentEntry;
        parentEntry = parentEntry.parent;
      }
      return parentEntry;
    }
  }

  private static class Entry<V> {

    long key;
    V value;
    Entry<V> leftChild;
    Entry<V> rightChild;
    Entry<V> parent;
    boolean isBlack = true;

    public Entry() {
      //NOP
    }

    public Entry(long key, V value, Entry<V> parent) {
      this.key = key;
      this.value = value;
      this.parent = parent;
    }

    public long getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }

    public V setValue(V value) {
      V formerValue = this.value;
      this.value = value;
      return formerValue;
    }
  }

  public static class GenericArray<V> {
    private final V[] array;

    @SuppressWarnings("unchecked")
    public GenericArray(Object[] obj) {
      this.array = (V[]) obj;
    }

    public V[] get() {
      return this.array;
    }
  }
}
