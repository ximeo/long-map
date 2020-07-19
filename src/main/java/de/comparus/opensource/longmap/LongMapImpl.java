package de.comparus.opensource.longmap;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This is an implementation of LongMap interface and using similarity of a hash-table (as specified
 * in the task "It has to be a hash table (like HashMap)"). This implementation uses a mechanism
 * similar to the hash-map implementation: storing balanced trees in an array. But we really don't
 * need to use tables like hash-tables in the case of longs as keys, because the long value is
 * better than the cache value (it uniquely defines the key and no additional calculations are
 * required) and I believe that using trees directly suits this case more especially considering
 * the possible magnitudes of the key values. Therefore, I specifically implemented a balanced tree
 * for our case with longs as keys(please @see LongMapOnBalancedTree in this package) and it is more
 * efficient for storing objects than this class using a kind of hash tables  in terms of speed and
 * as well as in memory use. This class uses it as a storage of objects in each cell of the array,
 * but we can use it directly for the same purposes and this is more efficient.
 *
 * I have also implemented LongMap by array method using nested arrays as an experiment (You can
 * @see this implementation in the package alternative) but during testing it turned out that it is
 * much slower and takes up much more memory than this implementation based on using trees.
 *
 */
public class LongMapImpl<V> implements LongMap<V> {
    private static final int DEFAULT_TABLE_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private final float loadFactor;
    private int currentTableCapacity;
    private int currentBucketNumber;
    private Object[] dataStorage;
    private long size;

    public LongMapImpl() {
        this(DEFAULT_TABLE_CAPACITY);
    }

    public LongMapImpl(int startCapacity) {
        this(startCapacity, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int startCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.dataStorage = new Object[startCapacity];
        this.currentTableCapacity = startCapacity;
    }

    @Override
    public V put(long key, V value) {
        LongMap<V> nestedTreeMap = getNestedLongTreeMapByKey(key, true);
        V oldValue = nestedTreeMap.get(key);
        nestedTreeMap.put(key, value);
        if (oldValue != null) return oldValue;
        size++;
        return value;
    }

    @Override
    public V get(long key) {
        LongMap<V> nestedTreeMap = getNestedLongTreeMapByKey(key, false);
        if (nestedTreeMap == null) {
            return null;
        }
        return nestedTreeMap.get(key);
    }

    @Override
    public V remove(long key) {
        LongMap<V> nestedTreeMap = getNestedLongTreeMapByKey(key, false);
        V result = nestedTreeMap.remove(key);
        if (result == null) return null;
        size--;
        return result;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    @Override
    public boolean containsKey(long key) {
        LongMap<V> nestedTreeMap = getNestedLongTreeMapByKey(key, false);
        if (nestedTreeMap == null) return false;
        return nestedTreeMap.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return Arrays.stream(dataStorage)
            .filter(Objects::nonNull)
            .anyMatch(e -> ((LongMap<V>)e).containsValue(value));
    }

    @Override
    public long[] keys() {
        return Arrays.stream(dataStorage)
            .filter(Objects::nonNull)
            .map(e -> ((LongMap<V>)e))
            .map(v -> v.keys())
            .flatMapToLong(k -> Arrays.stream(k))
            .toArray();
    }

    @Override
    public V[] values() {
        return (V[]) Arrays.stream(dataStorage)
            .filter(Objects::nonNull)
            .map(e -> ((LongMap<V>)e)
                .values()).flatMap(Stream::of).toArray();
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        this.currentTableCapacity = DEFAULT_TABLE_CAPACITY;
        dataStorage = new Object[this.currentTableCapacity];
        size = 0;
    }

    private LongMapOnBalancedTree<V> getNestedLongTreeMapByKey(long key, boolean isForInserting) {
        int index = getIndexByKey(key, isForInserting);
        return (LongMapOnBalancedTree<V>) dataStorage[index];
    }

    private int getIndexByKey(long key, boolean isToInsert) {
        int index = calculateIndex(key, this.currentTableCapacity);
        if (!isToInsert) {
            return index;
        }
        boolean hasBeenOptimized = optimizeTable(index);
        if (hasBeenOptimized) {
            index = calculateIndex(key, this.currentTableCapacity);
        }
        return index;
    }

    private boolean optimizeTable(int index) {
        if (dataStorage[index] == null) {
            dataStorage[index] = new LongMapOnBalancedTree<>();
            currentBucketNumber++;

            float currentOccupancy = 1.0f * currentBucketNumber / currentTableCapacity;
            if (currentOccupancy < loadFactor) {
                return false;
            }

            long[] keys = keys();
            V[] values = values();

            this.currentTableCapacity = calculateNewTableCapacity(this.currentTableCapacity);
            this.dataStorage = new LongMapOnBalancedTree[this.currentTableCapacity];
            this.size = 0;
            this.currentBucketNumber = 0;

            fillTableWithOldValuesAfterResizing(keys, values);

            return true;
        }
        return false;
    }

    private void fillTableWithOldValuesAfterResizing(long[] keys, V[] values) {
        for (int i = 0; i < keys.length; i++) {
            put(keys[i], values[i]);
        }
    }

    private static int calculateIndex(long key, int currentCapacity) {
        boolean isPositive = true;
        if (key < 0) {
            isPositive = false;
            key = Math.abs(key);
        }
        // to account for both positive and negative long-keys
        int halfCapacity = currentCapacity / 2;
        int index = (int) (key & (halfCapacity - 1));
        if (isPositive) {
            index += currentCapacity / 2;
        }
        return index;
    }

    private static int calculateNewTableCapacity(int currentTableCapacity) {
        int result = currentTableCapacity + (currentTableCapacity >> 1) + 1;
        if (result < 0) result = Integer.MAX_VALUE;
        return result;
    }
}
