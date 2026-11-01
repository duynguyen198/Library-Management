package app;

import controller.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import model.*;
import repository.CsvDataManager;
import service.*;
import strategy.StandardFineStrategy;
import view.*;

public class Main {
  public static void main(String[] args) {
    ConsoleView.configureUtf8();
    ItemService items = new ItemService();
    MemberService members = new MemberService();
    LoanService loans = new LoanService(items, members, new StandardFineStrategy());
    ReportService reports = new ReportService(items, members, loans);
    AuthenticationService auth = new AuthenticationService();
    CsvDataManager dataManager = CsvDataManager.getInstance();
    if (!dataManager.load(items, members, loans)) {
      members.add("M001", "Nguyễn Văn An", "0912345678", "an@example.com");
      items.add("BOOK", "B001", "Lập trình hướng đối tượng", "Nguyễn Dũng", 2024, 3);
      items.add("MAGAZINE", "MG001", "Tạp chí Công nghệ", "NXB Trẻ", 2025, 2);
      items.add("THESIS", "T001", "Đồ án thư viện", "Trần Minh", 2023, 1);
      dataManager.save(items, members, loans);
    }
    Member member = members.findById("M001");
    auth.addAccount(new UserAccount("admin", "admin123", new Admin("A001")));
    auth.addAccount(new UserAccount("member", "member123", member));
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    LoginView loginView = new LoginView(scanner);
    AdminView adminView = new AdminView(scanner);
    MemberView memberView = new MemberView(scanner);
    AdminController admin =
        new AdminController(adminView, items, members, loans, reports, dataManager);
    MemberController memberController = new MemberController(memberView, items, loans);
    new AuthController(loginView, auth, admin, memberController).start();
  }
}
