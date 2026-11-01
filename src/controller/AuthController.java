package controller;

import model.Member;
import model.Role;
import model.UserAccount;
import service.AuthenticationService;
import view.LoginView;

public class AuthController {
  private final LoginView view;
  private final AuthenticationService auth;
  private final AdminController adminController;
  private final MemberController memberController;

  public AuthController(
      LoginView view, AuthenticationService auth, AdminController admin, MemberController member) {
    this.view = view;
    this.auth = auth;
    this.adminController = admin;
    this.memberController = member;
  }

  public void start() {
    boolean on = true;
    while (on) {
      view.menu();
      switch (view.number("Chọn: ")) {
        case 1 -> login();
        case 0 -> on = false;
        default -> view.error("Lựa chọn không hợp lệ.");
      }
    }
  }

  private void login() {
    UserAccount account =
        auth.login(view.text("Tên đăng nhập: "), view.text("Mật khẩu: ")).orElse(null);
    if (account == null) {
      view.error("Tên đăng nhập hoặc mật khẩu không chính xác.");
      return;
    }
    if (account.getRole() == Role.ADMIN) adminController.run();
    else memberController.run((Member) account.getOwner());
  }
}
