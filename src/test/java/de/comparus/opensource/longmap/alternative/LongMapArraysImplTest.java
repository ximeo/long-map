package de.comparus.opensource.longmap.alternative;

import de.comparus.opensource.longmap.LongMap;
import de.comparus.opensource.longmap.model.TestObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongMapArraysImplTest {

  private LongMap<TestObject> testedInstance;

  private TestObject testObject1;
  private TestObject testObject2;

  @Before
  public void init() {
    testedInstance = new LongMapArraysImpl<>();
    testObject1 = new TestObject(10, "Car");
    testObject2 = new TestObject(10, "Bus");
  }

  @Test
  public void shouldSaveItem() {
    testedInstance.put(1, testObject1);

    assertNotNull(testedInstance.get(1L));
    assertEquals(1L, testedInstance.size());
  }

  @Test
  public void shouldRemoveItem() {
    testedInstance.put(1L, testObject1);
    testedInstance.remove(1);

    assertNull(testedInstance.get(1L));
    assertEquals(testedInstance.size(), 0);
  }

  @Test
  public void shouldRewrite() {
    testedInstance.put(1L, testObject1);
    testedInstance.put(1L, testObject2);

    assertEquals(testedInstance.get(1L).getName(), "Bus");
  }

  @Test
  public void shouldGetKeys() {
    testedInstance.put(1L, testObject1);
    testedInstance.put(10L, testObject2);

    long[] result = testedInstance.keys();

    assertNotNull(result);
    assertEquals(2, result.length);
  }

  @Test
  public void shouldGetValues() {
    testedInstance.put(1L, testObject1);
    testedInstance.put(10L, testObject2);

    Object[] result = testedInstance.values();

    assertNotNull(result);
    assertEquals(2, result.length);
  }

  @Test
  public void shouldClear() {
    testedInstance.put(1L, testObject1);
    testedInstance.clear();

    assertNull(testedInstance.get(1L));
    assertEquals(0, testedInstance.size());
  }

  @Test
  public void shouldEmpty() {
    testedInstance.put(1L, testObject1);

    assertFalse(testedInstance.isEmpty());

    testedInstance.remove(1L);

    assertTrue(testedInstance.isEmpty());
  }

  @Test
  public void shouldContainsKey() {
    testedInstance.put(1L, testObject1);

    assertTrue(testedInstance.containsKey(1L));

    testedInstance.remove(1L);

    assertFalse(testedInstance.containsKey(1L));
  }

  @Test
  public void shouldContainsValue() {
    testedInstance.put(1L, testObject1);

    assertTrue(testedInstance.containsValue(testObject1));

    testedInstance.remove(1L);

    assertFalse(testedInstance.containsValue(testObject1));
  }
}
