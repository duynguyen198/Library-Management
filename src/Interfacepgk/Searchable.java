package interfacepkg;

import java.util.List;

public interface Searchable<T> {
  List<T> search(String keyword);

  default String normalize(String keyword) {
    return keyword == null ? "" : keyword.trim().toLowerCase();
  }
}
