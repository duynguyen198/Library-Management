package util;

import exception.InvalidDataException;
import java.time.Year;

public final class Validator {
  private Validator() {}

  public static String text(String value, String field) {
    if (value == null || value.isBlank())
      throw new InvalidDataException(field + " không được để trống.");
    return value.trim();
  }

  public static int positive(int value, String field) {
    if (value <= 0) throw new InvalidDataException(field + " phải lớn hơn 0.");
    return value;
  }

  public static int year(int value) {
    if (value < 1000 || value > Year.now().getValue())
      throw new InvalidDataException("Năm xuất bản không hợp lệ.");
    return value;
  }

  public static String email(String value) {
    value = text(value, "Email");
    if (!value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
      throw new InvalidDataException("Email không hợp lệ.");
    return value;
  }

  public static String phone(String value) {
    value = text(value, "Số điện thoại");
    if (!value.matches("0\\d{9}"))
      throw new InvalidDataException("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.");
    return value;
  }
}
