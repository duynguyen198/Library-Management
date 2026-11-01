package interfacepkg;

public interface Borrowable {
  int getLoanDays();

  int getAvailableQuantity();

  default boolean canBorrow() {
    return getAvailableQuantity() > 0;
  }
}
