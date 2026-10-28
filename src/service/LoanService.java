package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import model.*;
import strategy.FineCalculationStrategy;

public class LoanService {
  public static final int MAX_ACTIVE_ITEMS = 5;
  private final List<Loan> loans = new ArrayList<>();
  private final ItemService itemService;
  private final MemberService memberService;
  private final FineCalculationStrategy fineStrategy;

  public LoanService(
      ItemService itemService, MemberService memberService, FineCalculationStrategy fineStrategy) {
    this.itemService = itemService;
    this.memberService = memberService;
    this.fineStrategy = fineStrategy;
  }

  public List<Loan> findAll() {
    return List.copyOf(loans);
  }

  public void restore(List<Loan> values) {
    loans.clear();
    loans.addAll(values);
  }

  public Loan findById(String id) {
    return loans.stream()
        .filter(l -> l.getId().equalsIgnoreCase(id))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu mượn."));
  }

  public Loan borrow(String memberId, List<String> requestedIds) {
    return borrow(memberId, requestedIds, LocalDate.now());
  }

  public Loan borrow(String memberId, List<String> requestedIds, LocalDate borrowDate) {
    return borrow(memberId, requestedIds, borrowDate, null);
  }

  /** dueDate = null thì hạn trả tự tính theo số ngày mượn của từng loại tài liệu. */
  public Loan borrow(
      String memberId, List<String> requestedIds, LocalDate borrowDate, LocalDate dueDate) {
    if (borrowDate.isBefore(LocalDate.now()))
      throw new IllegalArgumentException("Ngày mượn không được ở trong quá khứ.");
    if (dueDate != null && !dueDate.isAfter(borrowDate))
      throw new IllegalArgumentException("Ngày trả phải sau ngày mượn.");
    memberService.findById(memberId);
    List<String> itemIds = new ArrayList<>(new LinkedHashSet<>(requestedIds));
    if (itemIds.isEmpty())
      throw new IllegalArgumentException("Phiếu mượn phải có ít nhất một tài liệu.");
    if (activeItemCount(memberId) + itemIds.size() > MAX_ACTIVE_ITEMS)
      throw new IllegalStateException("Độc giả không được mượn quá 5 tài liệu.");
    if (hasOverdueLoan(memberId, borrowDate))
      throw new IllegalStateException("Độc giả đang có tài liệu quá hạn.");
    List<LibraryItem> items = itemIds.stream().map(itemService::findById).toList();
    items.forEach(
        item -> {
          if (!item.canBorrow())
            throw new IllegalStateException("Tài liệu " + item.getId() + " đã hết.");
        });

    Loan loan = new Loan("L" + String.format("%04d", loans.size() + 1), memberId, borrowDate);
    for (LibraryItem item : items) {
      item.borrowOne();
      loan.addDetail(
          dueDate == null
              ? new LoanDetail(item.getId(), borrowDate, item.getLoanDays())
              : new LoanDetail(item.getId(), dueDate));
    }
    loans.add(loan);
    return loan;
  }

  public long returnLoan(String loanId, LocalDate returnDate) {
    Loan loan = findById(loanId);
    if (loan.getStatus() != LoanStatus.BORROWING)
      throw new IllegalStateException("Phiếu mượn đã được trả.");
    if (returnDate.isBefore(loan.getBorrowDate()))
      throw new IllegalArgumentException("Ngày trả không hợp lệ.");
    long fine = fineStrategy.calculate(loan, returnDate);
    loan.getDetails().forEach(d -> itemService.findById(d.getItemId()).returnOne());
    loan.markReturned(returnDate, fine);
    return fine;
  }

  public List<Loan> history(String memberId) {
    return loans.stream().filter(l -> l.getMemberId().equalsIgnoreCase(memberId)).toList();
  }

  public List<Loan> activeLoans(String memberId) {
    return history(memberId).stream().filter(l -> l.getStatus() == LoanStatus.BORROWING).toList();
  }

  public boolean hasActiveLoanForMember(String id) {
    return !activeLoans(id).isEmpty();
  }

  public boolean hasActiveLoanForItem(String id) {
    return loans.stream()
        .filter(l -> l.getStatus() == LoanStatus.BORROWING)
        .flatMap(l -> l.getDetails().stream())
        .anyMatch(d -> d.getItemId().equalsIgnoreCase(id));
  }

  public boolean hasOverdueLoan(String memberId, LocalDate date) {
    return activeLoans(memberId).stream()
        .flatMap(l -> l.getDetails().stream())
        .anyMatch(d -> d.getDueDate().isBefore(date));
  }

  private long activeItemCount(String memberId) {
    return activeLoans(memberId).stream().mapToLong(l -> l.getDetails().size()).sum();
  }
}
