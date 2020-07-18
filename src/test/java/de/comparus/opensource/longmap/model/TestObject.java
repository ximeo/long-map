package de.comparus.opensource.longmap.model;

public class TestObject {
  private long id;
  private String name;

  public TestObject() {
  }

  public TestObject(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "TestObject{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
  }
}
