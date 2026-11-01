package view;

import java.util.Scanner;

public class AdminView extends ConsoleView {
  public AdminView(Scanner scanner) {
    super(scanner);
  }

  public void menu() {
    title("MENU QUẢN TRỊ VIÊN");
    line(
        "1. Quản lý tài liệu\n"
            + "2. Quản lý độc giả\n"
            + "3. Tạo phiếu mượn\n"
            + "4. Nhận trả tài liệu\n"
            + "5. Báo cáo thống kê\n"
            + "0. Đăng xuất");
  }

  public void itemMenu() {
    title("QUẢN LÝ TÀI LIỆU");
    line("1. Xem danh sách\n2. Thêm\n3. Sửa\n4. Xóa\n5. Tìm kiếm\n6. Tìm theo loại\n0. Quay lại");
  }

  public void memberMenu() {
    title("QUẢN LÝ ĐỘC GIẢ");
    line("1. Xem danh sách\n2. Thêm\n3. Sửa\n4. Xóa\n5. Tìm kiếm\n0. Quay lại");
  }
}
