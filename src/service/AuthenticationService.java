package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.UserAccount;

public class AuthenticationService {
  private final List<UserAccount> accounts = new ArrayList<>();

  public void addAccount(UserAccount account) {
    accounts.add(account);
  }

  public Optional<UserAccount> login(String username, String password) {
    return accounts.stream()
        .filter(a -> a.getUsername().equalsIgnoreCase(username) && a.matchesPassword(password))
        .findFirst();
  }
}
