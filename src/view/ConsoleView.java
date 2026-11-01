package view;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleView {
  protected final Scanner scanner;

  public ConsoleView(Scanner scanner) {
    this.scanner = scanner;
  }

  public static void configureUtf8() {
    try {
      if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
        new ProcessBuilder("cmd", "/c", "chcp 65001 > nul").inheritIO().start().waitFor();
      }
      System.setOut(
          new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
      System.setErr(
          new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
    } catch (Exception ignored) {
      // Giữ encoding mặc định nếu hệ thống không hỗ trợ UTF-8.
    }
  }

  public void title(String value) {
    System.out.println(
        "\n==================================================\n  "
            + value
            + "\n==================================================");
  }

  public void line(String value) {
    System.out.println(value);
  }

  public void success(String value) {
    System.out.println("[THÀNH CÔNG] " + value);
  }

  public void error(String value) {
    System.out.println("[LỖI] " + value);
  }

  public String text(String prompt) {
    System.out.print(prompt);
    return scanner.nextLine().trim();
  }

  public int number(String prompt) {
    while (true) {
      try {
        return Integer.parseInt(text(prompt));
      } catch (NumberFormatException e) {
        error("Vui lòng nhập số nguyên.");
      }
    }
  }

  public LocalDate date(String prompt) {
    while (true) {
      try {
        return LocalDate.parse(text(prompt));
      } catch (DateTimeParseException exception) {
        error("Ngày không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-dd.");
      }
    }
  }
}
