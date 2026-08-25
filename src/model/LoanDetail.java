package model;

import java.time.LocalDate;

public class LoanDetail implements java.io.Serializable {
  private final String itemId;
  private final LocalDate dueDate;

  public LoanDetail(String itemId, LocalDate dueDate) {
    this.itemId = itemId;
    this.dueDate = dueDate;
  }

  public String getItemId() {
    return itemId;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }
}
