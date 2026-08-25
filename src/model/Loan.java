package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Loan implements java.io.Serializable {
  private final String id;
  private final String memberId;
  private final LocalDate borrowDate;
  private final List<LoanDetail> details = new ArrayList<>();
  private LoanStatus status = LoanStatus.BORROWING;
  private LocalDate returnDate;
  private long fine;

  public Loan(String id, String memberId, LocalDate borrowDate) {
    this.id = id;
    this.memberId = memberId;
    this.borrowDate = borrowDate;
  }

  public String getId() {
    return id;
  }

  public String getMemberId() {
    return memberId;
  }

  public LocalDate getBorrowDate() {
    return borrowDate;
  }

  public List<LoanDetail> getDetails() {
    return List.copyOf(details);
  }

  public LoanStatus getStatus() {
    return status;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

  public long getFine() {
    return fine;
  }

  public void addDetail(LoanDetail detail) {
    details.add(detail);
  }

  public void markReturned(LocalDate date, long fine) {
    this.returnDate = date;
    this.fine = fine;
    status = LoanStatus.RETURNED;
  }
}
