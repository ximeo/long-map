package de.comparus.opensource.longmap;

import de.comparus.opensource.longmap.model.TestObject;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

public class LongMapImplTest {

  private LongMap<TestObject> testedInstance;

  private final TestObject testObject3 = new TestObject(10L, "Plain");
  private final TestObject testObject4 = new TestObject(100L, "Boat");

  private TestObject testObject1;
  private TestObject testObject2;

  @Before
  public void init() {
    testedInstance = new LongMapImpl<>();
    testObject1 = new TestObject(1L, "Car");
    testedInstance.put(testObject1.getId(), testObject1);
    testObject2 = new TestObject(5L, "Bus");
    testedInstance.put(testObject2.getId(), testObject2);
  }

  @Test
  public void shouldSaveData() {
    TestObject response = testedInstance.put(testObject3.getId(), testObject3);

    assertNotNull(response);
  }

  @Test
  public void shouldGetSavedData() {
    testedInstance.put(testObject3.getId(), testObject3);

    assertNotNull(testedInstance.get(testObject3.getId()));
    assertEquals(testObject3, testedInstance.get(testObject3.getId()));
  }

  @Test
  public void shouldDeleteData() {

    assertNotNull(testedInstance.get(testObject2.getId()));
    assertEquals(testObject2, testedInstance.get(testObject2.getId()));

    testedInstance.remove(testObject2.getId());

    assertNull(testedInstance.get(testObject2.getId()));
  }

  @Test
  public void shouldClearData() {

    assertNotNull(testedInstance.get(testObject1.getId()));
    assertNotNull(testedInstance.get(testObject2.getId()));

    testedInstance.clear();

    assertNull(testedInstance.get(testObject1.getId()));
    assertNull(testedInstance.get(testObject2.getId()));
  }

  @Test
  public void shouldCangeSize() {

    assertEquals(2L, testedInstance.size());

    testedInstance.remove(testObject1.getId());

    assertEquals(1L, testedInstance.size());

    testedInstance.put(testObject3.getId(), testObject3);
    testedInstance.put(testObject4.getId(), testObject4);

    assertEquals(3L, testedInstance.size());
  }

  @Test
  public void shouldShowContainsKeyCorrectly() {

    assertFalse(testedInstance.containsKey(testObject3.getId()));

    testedInstance.put(testObject3.getId(), testObject3);

    assertTrue(testedInstance.containsKey(testObject3.getId()));

    testedInstance.remove(testObject3.getId());

    assertFalse(testedInstance.containsKey(testObject3.getId()));
  }

  @Test
  public void shouldShowContainsValueCorrectly() {

    assertFalse(testedInstance.containsValue(testObject3));

    testedInstance.put(testObject3.getId(), testObject3);

    assertTrue(testedInstance.containsValue(testObject3));

    testedInstance.remove(testObject3.getId());

    assertFalse(testedInstance.containsValue(testObject3));
  }

  @Test
  public void shouldGetKeys() {

    long[] response = testedInstance.keys();

    assertNotNull(response);
    assertEquals(2, response.length);
    assertEquals(testObject1.getId(), response[0]);
    assertEquals(testObject2.getId(), response[1]);
  }

  @Test
  public void shouldGetValues() {

    Object[] response = testedInstance.values();

    assertNotNull(response);
    assertEquals(2, response.length);
    assertEquals(testObject1, response[0]);
    assertEquals(testObject2, response[1]);
  }

  @Test
  public void shouldSnowIsEmpty() {

    assertFalse(testedInstance.isEmpty());

    testedInstance.remove(testObject1.getId());
    testedInstance.remove(testObject2.getId());

    assertTrue(testedInstance.isEmpty());

    testedInstance.put(testObject3.getId(), testObject3);

    assertFalse(testedInstance.isEmpty());
  }
}