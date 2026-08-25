package model;

import util.Validator;

public class Member extends Person {
  private String phone;
  private String email;

  public Member(String id, String fullName) {
    this(id, fullName, "0000000000", "unknown@example.com");
  }

  public Member(String id, String fullName, String phone, String email) {
    super(id, fullName);
    setPhone(phone);
    setEmail(email);
  }

  public String getPhone() {
    return phone;
  }

  public String getEmail() {
    return email;
  }

  public void setPhone(String phone) {
    this.phone = Validator.phone(phone);
  }

  public void setEmail(String email) {
    this.email = Validator.email(email);
  }

  @Override
  public Role getRole() {
    return Role.MEMBER;
  }

  @Override
  public String toString() {
    return getId() + " | " + getFullName() + " | " + phone + " | " + email;
  }
}
