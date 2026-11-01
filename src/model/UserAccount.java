package model;

import util.Validator;

public class UserAccount {
  private final String username;
  private final String password;
  private final Person owner;

  public UserAccount(Person owner, String password) {
    this(usernameOf(owner), password, owner);
  }

  public UserAccount(String username, String password, Person owner) {
    this.username = Validator.text(username, "Tên đăng nhập");
    this.password = Validator.text(password, "Mật khẩu");
    if (owner == null) throw new IllegalArgumentException("Chủ tài khoản không được để trống.");
    this.owner = owner;
  }

  private static String usernameOf(Person owner) {
    if (owner == null) throw new IllegalArgumentException("Chủ tài khoản không được để trống.");
    return owner.getId().toLowerCase();
  }

  public String getUsername() {
    return username;
  }

  public Person getOwner() {
    return owner;
  }

  public Role getRole() {
    return owner.getRole();
  }

  public boolean matchesPassword(String value) {
    return password.equals(value);
  }
}
