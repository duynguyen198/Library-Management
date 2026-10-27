package factory;

import model.Book;
import model.LibraryItem;
import model.Magazine;
import model.Thesis;

public final class LibraryItemFactory {
  private LibraryItemFactory() {}

  public static LibraryItem create(
      String type, String id, String title, String author, int year, int quantity) {
    return switch (type.trim().toUpperCase()) {
      case "BOOK" -> new Book(id, title, author, year, quantity);
      case "MAGAZINE" -> new Magazine(id, title, author, year, quantity);
      case "THESIS" -> new Thesis(id, title, author, year, quantity);
      default -> throw new IllegalArgumentException("Loại tài liệu không hợp lệ.");
    };
  }
}
