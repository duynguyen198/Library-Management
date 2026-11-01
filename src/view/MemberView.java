package view;

import java.util.Scanner;

public class MemberView extends ConsoleView {
  public MemberView(Scanner scanner) {
    super(scanner);
  }

  public void menu(String name) {
    title("MENU ĐỘC GIẢ - " + name.toUpperCase());
    line(
        "1. Tìm kiếm tài liệu\n"
            + "2. Tài liệu đang mượn\n"
            + "3. Lịch sử mượn - trả\n"
            + "4. Xem danh sách tài liệu\n"
            + "0. Đăng xuất");
  }
}
