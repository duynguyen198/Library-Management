package service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import model.LibraryItem;
import model.Loan;
import model.LoanStatus;
import util.Validator;

public class ReportService {
  public record OverdueRow(
      String memberId, String memberName, String itemId, LocalDate dueDate, long overdueDays) {}

  private final ItemService itemService;
  private final MemberService memberService;
  private final LoanService loanService;

  public ReportService(
      ItemService itemService, MemberService memberService, LoanService loanService) {
    this.itemService = itemService;
    this.memberService = memberService;
    this.loanService = loanService;
  }

  public List<LibraryItem> topItems() {
    return topItems(5);
  }

  public List<LibraryItem> topItems(int limit) {
    return itemService.findAll().stream()
        .sorted(Comparator.comparingInt(LibraryItem::getBorrowCount).reversed())
        .limit(Validator.positive(limit, "Số dòng thống kê"))
        .toList();
  }

  public List<OverdueRow> overdueMembers() {
    return overdueMembers(LocalDate.now());
  }

  public List<OverdueRow> overdueMembers(LocalDate today) {
    return loanService.findAll().stream()
        .filter(l -> l.getStatus() == LoanStatus.BORROWING)
        .flatMap(
            loan ->
                loan.getDetails().stream()
                    .filter(d -> d.getDueDate().isBefore(today))
                    .map(d -> row(loan, d.getItemId(), d.getDueDate(), today)))
        .toList();
  }

  private OverdueRow row(Loan loan, String itemId, LocalDate due, LocalDate today) {
    return new OverdueRow(
        loan.getMemberId(),
        memberService.findById(loan.getMemberId()).getFullName(),
        itemId,
        due,
        ChronoUnit.DAYS.between(due, today));
  }
}
