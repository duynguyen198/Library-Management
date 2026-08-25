package model;

public class Thesis extends LibraryItem {
  public Thesis(String id, String title, String author, int year, int quantity) {
    super(id, title, author, year, quantity);
  }

  @Override
  public int getLoanDays() {
    return 7;
  }

  @Override
  public String getItemType() {
    return "THESIS";
  }
}
