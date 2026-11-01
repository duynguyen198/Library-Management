package view;

import java.util.Scanner;

public class LoginView extends ConsoleView {
  public LoginView(Scanner scanner) {
    super(scanner);
  }

  public void menu() {
    title("HỆ THỐNG QUẢN LÝ THƯ VIỆN");
    line("1. Đăng nhập\n0. Thoát");
  }
}
