package de.comparus.opensource.longmap.alternative;

import de.comparus.opensource.longmap.LongMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This implementation of LongMap is based on using nested arrays as a storage of objects, their indexes are using like
 * keys of map. This implementation method is significantly outperformed by tree-based implementations in terms of speed
 * and memory usage. Therefore, it can only be considered as an alternative.
 */
public class LongMapArraysImpl<V> implements LongMap<V> {

    private Object[][][] dataStorage = new Object[3][][];
    private long size = 0L;

    @Override
    public V put(long key, V value) {
        ComplexIndex complexIndex = calculateComplexIndex(key);
        provideAvailability(complexIndex);
        dataStorage[complexIndex.firstLevelIndex][complexIndex.secondLevelIndex][complexIndex.thirdLevelIndex] = value;
        size++;
        return value;
    }

    @Override
    public V get(long key) {
        ComplexIndex complexIndex = calculateComplexIndex(key);
        if (dataStorage == null) return null;
        return (V) dataStorage[complexIndex.firstLevelIndex][complexIndex.secondLevelIndex][complexIndex.thirdLevelIndex];
    }

    @Override
    public V remove(long key) {
        ComplexIndex complexIndex = calculateComplexIndex(key);
        V toSend = (V) dataStorage[complexIndex.firstLevelIndex][complexIndex.secondLevelIndex][complexIndex.thirdLevelIndex];
        dataStorage[complexIndex.firstLevelIndex][complexIndex.secondLevelIndex][complexIndex.thirdLevelIndex] = null;
        this.size--;
        return toSend;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        ComplexIndex complexIndex = calculateComplexIndex(key);
        return dataStorage[complexIndex.firstLevelIndex][complexIndex.secondLevelIndex][complexIndex.thirdLevelIndex] != null;
    }

    @Override
    public boolean containsValue(V value) {
        List<V> values = getValues();
        for (V val : values) {
            if (val != null && val.equals(value)) return true;
        }
        return false;
    }

    @Override
    public long[] keys() {
        ArrayList<Long> res = new ArrayList();
        if (dataStorage == null) return new long[0];
        for (int i = 0; i < dataStorage.length; i++) {
            if (dataStorage[i] == null) continue;
            for (int k = 0; k < dataStorage[i].length; k++) {
                if (dataStorage[i][k] == null) continue;
                for (int m = 0; m < dataStorage[i][k].length; m++) {
                    if (dataStorage[i][k][m] != null) {
                        res.add(calculateSimpleIndex(new ComplexIndex(i, k, m)));
                    }
                }
            }
        }
        return res.stream().mapToLong(l -> l).toArray();
    }

    @Override
    public V[] values() {
        return (V[]) getValues().stream().filter(Objects::nonNull).toArray();
    }

    @Override
    public void clear() {
        dataStorage = null;
        size = 0L;
    }

    private List<V> getValues() {
        List<V> res = new ArrayList<>(0);
        for (Object[][] obj : dataStorage) {
            if (obj == null) continue;
            for (Object[] obj2 : obj) {
                if (obj2 == null) continue;
                for (Object obj3 : obj2) {
                    res.add((V)obj3);
                }
            }
        }
        return res;
    }

    @Override
    public long size() {
        return this.size;
    }

    private static ComplexIndex calculateComplexIndex(long simpleIndex) {
        int maxInteger = Integer.MAX_VALUE;
        long maxDoubleLevelArraySize = (long)maxInteger * maxInteger;
        long firstLevelIndex = simpleIndex/(maxDoubleLevelArraySize * 2);
        if ((simpleIndex - maxDoubleLevelArraySize * 2) > 0) {
            firstLevelIndex += 1;
        }
        long remainder = simpleIndex - firstLevelIndex * maxDoubleLevelArraySize;
        long secondLevelIndex = remainder/maxInteger;
        long thirdLevelIndex = remainder % maxInteger;
        return new ComplexIndex((int)firstLevelIndex, (int)secondLevelIndex, (int)thirdLevelIndex);
    }

    private static long calculateSimpleIndex(ComplexIndex complexIndex) {
        return (long) complexIndex.firstLevelIndex * complexIndex.secondLevelIndex * complexIndex.thirdLevelIndex;
    }

    private void provideAvailability(ComplexIndex complexIndex) {
        int i = complexIndex.firstLevelIndex;
        int k = complexIndex.secondLevelIndex;
        int m = complexIndex.thirdLevelIndex;

        if (dataStorage == null) {
            dataStorage = new Object[i][k][m];
        }
        if (dataStorage.length <= i) {
            Object[][][] level1Storage = new Object[i][k][m];
            System.arraycopy(dataStorage, 0, level1Storage, 0, dataStorage.length);
        }
        if (dataStorage[i] == null) {
            dataStorage[i] = new Object[k + 1][m + 1];
        }
        if (dataStorage[i].length <= k) {
            Object[][] level2Storage = new Object[k + 1][m + 1];
            System.arraycopy(dataStorage[i], 0, level2Storage, 0, dataStorage[i].length);
            dataStorage[i] = level2Storage;
        }
        if (dataStorage[i][k] == null) {
            dataStorage[i][k] = new Object[m + 1];
        }
        if (dataStorage[i][k].length <= m) {
            Object[] level3Storage = new Object[m + 1];
            System.arraycopy(dataStorage[i][k], 0, level3Storage, 0, dataStorage[i][k].length);
            dataStorage[i][k] = level3Storage;
        }
    }

    private static class ComplexIndex {

        private final int firstLevelIndex;
        private final int secondLevelIndex;
        private final int thirdLevelIndex;

        public ComplexIndex(int firstLevelIndex, int secondLevelIndex, int thirdLevelIndex) {
            this.firstLevelIndex = firstLevelIndex;
            this.secondLevelIndex = secondLevelIndex;
            this.thirdLevelIndex = thirdLevelIndex;
        }

        public int getFirstLevelIndex() {
            return firstLevelIndex;
        }

        public int getSecondLevelIndex() {
            return secondLevelIndex;
        }

        public int getThirdLevelIndex() {
            return thirdLevelIndex;
        }
    }
}
