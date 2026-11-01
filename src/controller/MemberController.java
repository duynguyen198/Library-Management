package controller;

import model.Member;
import service.ItemService;
import service.LoanService;
import view.MemberView;

public class MemberController {
  private final MemberView view;
  private final ItemService items;
  private final LoanService loans;

  public MemberController(MemberView view, ItemService items, LoanService loans) {
    this.view = view;
    this.items = items;
    this.loans = loans;
  }

  public void run(Member member) {
    boolean on = true;
    while (on) {
      view.menu(member.getFullName());
      switch (view.number("Chọn: ")) {
        case 1 -> items.search(view.text("Từ khóa: ")).forEach(i -> view.line(i.toString()));
        case 2 -> loans.activeLoans(member.getId()).forEach(l -> showLoan(l));
        case 3 -> loans.history(member.getId()).forEach(l -> showLoan(l));
        case 0 -> on = false;
        default -> view.error("Lựa chọn không hợp lệ.");
      }
    }
  }

  private void showLoan(model.Loan loan) {
    view.line(
        loan.getId()
            + " | "
            + loan.getBorrowDate()
            + " | "
            + loan.getStatus().getDisplayName()
            + " | tiền phạt="
            + loan.getFine());
    loan.getDetails()
        .forEach(d -> view.line("  " + d.getItemId() + " | hạn trả=" + d.getDueDate()));
  }
}
