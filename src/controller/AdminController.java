package controller;

import java.util.Arrays;
import model.Loan;
import repository.CsvDataManager;
import service.*;
import view.AdminView;

public class AdminController {
  private final AdminView view;
  private final ItemService items;
  private final MemberService members;
  private final LoanService loans;
  private final ReportService reports;
  private final CsvDataManager dataManager;

  public AdminController(
      AdminView view,
      ItemService items,
      MemberService members,
      LoanService loans,
      ReportService reports,
      CsvDataManager dataManager) {
    this.view = view;
    this.items = items;
    this.members = members;
    this.loans = loans;
    this.reports = reports;
    this.dataManager = dataManager;
  }

  public void run() {
    boolean on = true;
    while (on) {
      view.menu();
      try {
        switch (view.number("Chọn: ")) {
          case 1 -> itemCrud();
          case 2 -> memberCrud();
          case 3 -> borrow();
          case 4 -> giveBack();
          case 5 -> report();
          case 0 -> on = false;
          default -> view.error("Lựa chọn không hợp lệ.");
        }
      } catch (RuntimeException e) {
        view.error(e.getMessage());
      }
    }
  }

  private void itemCrud() {
    boolean on = true;
    while (on) {
      view.itemMenu();
      try {
        switch (view.number("Chọn: ")) {
          case 1 -> items.findAll().forEach(i -> view.line(i.toString()));
          case 2 -> addItem();
          case 3 -> updateItem();
          case 4 -> deleteItem();
          case 5 -> items.search(view.text("Từ khóa: ")).forEach(i -> view.line(i.toString()));
          case 6 -> searchItemByType();
          case 0 -> on = false;
          default -> view.error("Lựa chọn không hợp lệ.");
        }
      } catch (RuntimeException e) {
        view.error(e.getMessage());
      }
    }
  }

  private void searchItemByType() {
    String keyword = view.text("Từ khóa (bỏ trống để lấy tất cả): ");
    String type = view.text("Loại (BOOK/MAGAZINE/THESIS): ");
    items.search(keyword, type).forEach(i -> view.line(i.toString()));
  }

  private void addItem() {
    String type = view.text("Loại (BOOK/MAGAZINE/THESIS): ");
    String id = view.text("Mã tài liệu: ");
    String title = view.text("Tiêu đề: ");
    String author = view.text("Tác giả: ");
    int year = view.number("Năm xuất bản: ");
    int qty = view.number("Số lượng: ");
    items.add(type, id, title, author, year, qty);
    saveData();
    view.success("Đã thêm tài liệu.");
  }

  private void updateItem() {
    String id = view.text("Mã tài liệu: ");
    String title = view.text("Tiêu đề mới: ");
    String author = view.text("Tác giả: ");
    int year = view.number("Năm xuất bản: ");
    int qty = view.number("Số lượng: ");
    items.update(id, title, author, year, qty);
    saveData();
    view.success("Đã cập nhật tài liệu.");
  }

  private void deleteItem() {
    items.delete(view.text("Mã tài liệu: "), loans);
    saveData();
    view.success("Đã xóa tài liệu.");
  }

  private void memberCrud() {
    boolean on = true;
    while (on) {
      view.memberMenu();
      try {
        switch (view.number("Chọn: ")) {
          case 1 -> members.findAll().forEach(m -> view.line(m.toString()));
          case 2 -> addMember();
          case 3 -> updateMember();
          case 4 -> deleteMember();
          case 5 -> members.search(view.text("Từ khóa: ")).forEach(m -> view.line(m.toString()));
          case 0 -> on = false;
          default -> view.error("Lựa chọn không hợp lệ.");
        }
      } catch (RuntimeException e) {
        view.error(e.getMessage());
      }
    }
  }

  private void addMember() {
    members.add(
        view.text("Mã độc giả: "),
        view.text("Họ tên: "),
        view.text("Điện thoại: "),
        view.text("Email: "));
    saveData();
    view.success("Đã thêm độc giả.");
  }

  private void updateMember() {
    members.update(
        view.text("Mã độc giả: "),
        view.text("Họ tên: "),
        view.text("Điện thoại: "),
        view.text("Email: "));
    saveData();
    view.success("Đã cập nhật độc giả.");
  }

  private void deleteMember() {
    members.delete(view.text("Mã độc giả: "), loans);
    saveData();
    view.success("Đã xóa độc giả.");
  }

  private void borrow() {
    String memberId = view.text("Mã độc giả: ");
    String raw = view.text("Danh sách mã tài liệu (cách nhau bằng dấu phẩy): ");
    Loan loan =
        loans.borrow(
            memberId,
            Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
    saveData();
    view.success("Đã tạo phiếu mượn " + loan.getId() + ".");
  }

  private void giveBack() {
    long fine = loans.returnLoan(view.text("Mã phiếu: "), view.date("Ngày trả (yyyy-MM-dd): "));
    saveData();
    view.success("Đã trả tài liệu. Tiền phạt: " + fine + " VND.");
  }

  private void report() {
    view.title("TOP 5 TÀI LIỆU ĐƯỢC MƯỢN NHIỀU NHẤT");
    reports
        .topItems()
        .forEach(i -> view.line(i.getId() + " | " + i.getTitle() + " | " + i.getBorrowCount()));
    view.title("DANH SÁCH ĐỘC GIẢ QUÁ HẠN");
    reports
        .overdueMembers()
        .forEach(
            r ->
                view.line(
                    r.memberId()
                        + " | "
                        + r.memberName()
                        + " | "
                        + r.itemId()
                        + " | "
                        + r.dueDate()
                        + " | "
                        + r.overdueDays()
                        + " ngày"));
  }

  private void saveData() {
    dataManager.save(items, members, loans);
  }
}
