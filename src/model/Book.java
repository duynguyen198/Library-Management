package model;

public class Book extends LibraryItem {
  public Book(String id, String title, String author, int year, int quantity) {
    super(id, title, author, year, quantity);
  }

  @Override
  public int getLoanDays() {
    return 14;
  }

  @Override
  public String getItemType() {
    return "BOOK";
  }
}
