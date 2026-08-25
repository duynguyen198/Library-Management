package model;

public enum LoanStatus {
  BORROWING("Đang mượn"),
  RETURNED("Đã trả");

  private final String displayName;

  LoanStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
