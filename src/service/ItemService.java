package service;

import factory.LibraryItemFactory;
import interfacepkg.Searchable;
import java.util.ArrayList;
import java.util.List;
import model.LibraryItem;

public class ItemService implements Searchable<LibraryItem> {
  private final List<LibraryItem> items = new ArrayList<>();

  public List<LibraryItem> findAll() {
    return List.copyOf(items);
  }

  public void restore(List<LibraryItem> values) {
    items.clear();
    items.addAll(values);
  }

  public LibraryItem findById(String id) {
    return items.stream()
        .filter(i -> i.getId().equalsIgnoreCase(id))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài liệu."));
  }

  public LibraryItem add(
      String type, String id, String title, String author, int year, int quantity) {
    if (items.stream().anyMatch(i -> i.getId().equalsIgnoreCase(id)))
      throw new IllegalArgumentException("Mã tài liệu đã tồn tại.");
    LibraryItem item = LibraryItemFactory.create(type, id, title, author, year, quantity);
    items.add(item);
    return item;
  }

  public void update(String id, String title, String author, int year, int quantity) {
    LibraryItem item = findById(id);
    item.setTitle(title);
    item.setAuthor(author);
    item.setPublicationYear(year);
    item.setTotalQuantity(quantity);
  }

  public void delete(String id, LoanService loanService) {
    if (loanService.hasActiveLoanForItem(id))
      throw new IllegalStateException("Không thể xóa tài liệu đang được mượn.");
    items.remove(findById(id));
  }

  @Override
  public List<LibraryItem> search(String keyword) {
    return search(keyword, null);
  }

  public List<LibraryItem> search(String keyword, String type) {
    String key = normalize(keyword);
    String wantedType = type == null || type.isBlank() ? null : type.trim().toUpperCase();
    return items.stream()
        .filter(i -> wantedType == null || i.getItemType().equals(wantedType))
        .filter(
            i ->
                i.getId().toLowerCase().contains(key)
                    || i.getTitle().toLowerCase().contains(key)
                    || i.getAuthor().toLowerCase().contains(key))
        .toList();
  }
}
