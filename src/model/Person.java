package model;

import util.Validator;

public abstract class Person implements java.io.Serializable {
  private final String id;
  private String fullName;

  protected Person(String id, String fullName) {
    this.id = Validator.text(id, "Mã người dùng").toUpperCase();
    setFullName(fullName);
  }

  public String getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = Validator.text(fullName, "Họ tên");
  }

  public abstract Role getRole();
}
