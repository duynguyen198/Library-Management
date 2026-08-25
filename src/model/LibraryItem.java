package model;

import exception.ItemNotAvailableException;
import interfacepkg.Borrowable;
import java.util.Objects;
import util.Validator;

public abstract class LibraryItem implements Borrowable, java.io.Serializable {
  private final String id;
  private String title;
  private String author;
  private int publicationYear;
  private int totalQuantity;
  private int availableQuantity;
  private int borrowCount;

  protected LibraryItem(String id, String title, String author, int year, int quantity) {
    this.id = Validator.text(id, "Mã tài liệu").toUpperCase();
    setTitle(title);
    setAuthor(author);
    setPublicationYear(year);
    this.totalQuantity = Validator.positive(quantity, "Số lượng");
    this.availableQuantity = quantity;
  }

  public abstract String getItemType();

  public String getItemTypeDisplayName() {
    return switch (getItemType()) {
      case "BOOK" -> "Sách";
      case "MAGAZINE" -> "Tạp chí";
      case "THESIS" -> "Luận văn";
      default -> getItemType();
    };
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public int getPublicationYear() {
    return publicationYear;
  }

  public int getTotalQuantity() {
    return totalQuantity;
  }

  @Override
  public int getAvailableQuantity() {
    return availableQuantity;
  }

  public int getBorrowCount() {
    return borrowCount;
  }

  public void setTitle(String value) {
    title = Validator.text(value, "Tiêu đề");
  }

  public void setAuthor(String value) {
    author = Validator.text(value, "Tác giả");
  }

  public void setPublicationYear(int value) {
    publicationYear = Validator.year(value);
  }

  public void setTotalQuantity(int value) {
    value = Validator.positive(value, "Số lượng");
    int borrowed = totalQuantity - availableQuantity;
    if (value < borrowed)
      throw new IllegalArgumentException("Số lượng mới nhỏ hơn số bản đang được mượn.");
    totalQuantity = value;
    availableQuantity = value - borrowed;
  }

  public void borrowOne() {
    if (!canBorrow()) throw new ItemNotAvailableException("Tài liệu đã hết.");
    availableQuantity--;
    borrowCount++;
  }

  public void returnOne() {
    if (availableQuantity >= totalQuantity)
      throw new IllegalStateException("Số lượng trả vượt quá số lượng trong kho.");
    availableQuantity++;
  }

  public void restoreInventory(int availableQuantity, int borrowCount) {
    if (availableQuantity < 0 || availableQuantity > totalQuantity || borrowCount < 0) {
      throw new IllegalArgumentException("Dữ liệu tồn kho không hợp lệ.");
    }
    this.availableQuantity = availableQuantity;
    this.borrowCount = borrowCount;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof LibraryItem item && id.equalsIgnoreCase(item.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "%s | %s | %s | %s | %d | còn %d/%d"
        .formatted(
            id,
            getItemTypeDisplayName(),
            title,
            author,
            publicationYear,
            availableQuantity,
            totalQuantity);
  }
}
