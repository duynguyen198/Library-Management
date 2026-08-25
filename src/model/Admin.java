package model;

public class Admin extends Person {
  public Admin(String id, String fullName) {
    super(id, fullName);
  }

  @Override
  public Role getRole() {
    return Role.ADMIN;
  }
}
