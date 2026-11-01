package repository;

import factory.LibraryItemFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.LibraryItem;
import model.Loan;
import model.LoanDetail;
import model.LoanStatus;
import model.Member;
import service.ItemService;
import service.LoanService;
import service.MemberService;

public final class CsvDataManager {
  private static final CsvDataManager INSTANCE = new CsvDataManager();
  private static final Path DATA_DIRECTORY = Path.of("data");
  private static final Path ITEMS_FILE = DATA_DIRECTORY.resolve("items.csv");
  private static final Path MEMBERS_FILE = DATA_DIRECTORY.resolve("members.csv");
  private static final Path LOANS_FILE = DATA_DIRECTORY.resolve("loans.csv");

  private CsvDataManager() {}

  public static CsvDataManager getInstance() {
    return INSTANCE;
  }

  public void save(ItemService items, MemberService members, LoanService loans) {
    try {
      Files.createDirectories(DATA_DIRECTORY);
      saveItems(items.findAll());
      saveMembers(members.findAll());
      saveLoans(loans.findAll());
    } catch (IOException exception) {
      throw new IllegalStateException("Không thể lưu dữ liệu CSV.", exception);
    }
  }

  public boolean load(ItemService items, MemberService members, LoanService loans) {
    if (!Files.exists(ITEMS_FILE) || !Files.exists(MEMBERS_FILE) || !Files.exists(LOANS_FILE)) {
      return false;
    }

    try {
      items.restore(loadItems());
      members.restore(loadMembers());
      loans.restore(loadLoans());
      return true;
    } catch (IOException | RuntimeException exception) {
      throw new IllegalStateException("Không thể đọc dữ liệu CSV.", exception);
    }
  }

  private void saveItems(List<LibraryItem> items) throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("loai,ma,tieuDe,tacGia,nam,tongSoLuong,soLuongCon,soLuotMuon");
    for (LibraryItem item : items) {
      lines.add(
          String.join(
              ",",
              csv(item.getItemType()),
              csv(item.getId()),
              csv(item.getTitle()),
              csv(item.getAuthor()),
              String.valueOf(item.getPublicationYear()),
              String.valueOf(item.getTotalQuantity()),
              String.valueOf(item.getAvailableQuantity()),
              String.valueOf(item.getBorrowCount())));
    }
    Files.write(ITEMS_FILE, lines, StandardCharsets.UTF_8);
  }

  private List<LibraryItem> loadItems() throws IOException {
    List<LibraryItem> result = new ArrayList<>();
    for (String line : dataLines(ITEMS_FILE)) {
      List<String> cells = parse(line);
      LibraryItem item =
          LibraryItemFactory.create(
              cells.get(0),
              cells.get(1),
              cells.get(2),
              cells.get(3),
              Integer.parseInt(cells.get(4)),
              Integer.parseInt(cells.get(5)));
      item.restoreInventory(Integer.parseInt(cells.get(6)), Integer.parseInt(cells.get(7)));
      result.add(item);
    }
    return result;
  }

  private void saveMembers(List<Member> members) throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("ma,hoTen,soDienThoai,email");
    for (Member member : members) {
      lines.add(
          String.join(
              ",",
              csv(member.getId()),
              csv(member.getFullName()),
              csv(member.getPhone()),
              csv(member.getEmail())));
    }
    Files.write(MEMBERS_FILE, lines, StandardCharsets.UTF_8);
  }

  private List<Member> loadMembers() throws IOException {
    List<Member> result = new ArrayList<>();
    for (String line : dataLines(MEMBERS_FILE)) {
      List<String> cells = parse(line);
      result.add(new Member(cells.get(0), cells.get(1), cells.get(2), cells.get(3)));
    }
    return result;
  }

  private void saveLoans(List<Loan> loans) throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("maPhieu,maDocGia,ngayMuon,trangThai,ngayTra,tienPhat,chiTiet");
    for (Loan loan : loans) {
      String details =
          loan.getDetails().stream()
              .map(detail -> detail.getItemId() + "@" + detail.getDueDate())
              .reduce((left, right) -> left + ";" + right)
              .orElse("");
      lines.add(
          String.join(
              ",",
              csv(loan.getId()),
              csv(loan.getMemberId()),
              loan.getBorrowDate().toString(),
              loan.getStatus().name(),
              loan.getReturnDate() == null ? "" : loan.getReturnDate().toString(),
              String.valueOf(loan.getFine()),
              csv(details)));
    }
    Files.write(LOANS_FILE, lines, StandardCharsets.UTF_8);
  }

  private List<Loan> loadLoans() throws IOException {
    List<Loan> result = new ArrayList<>();
    for (String line : dataLines(LOANS_FILE)) {
      List<String> cells = parse(line);
      Loan loan = new Loan(cells.get(0), cells.get(1), LocalDate.parse(cells.get(2)));
      if (!cells.get(6).isBlank()) {
        for (String detail : cells.get(6).split(";")) {
          String[] values = detail.split("@", 2);
          loan.addDetail(new LoanDetail(values[0], LocalDate.parse(values[1])));
        }
      }
      if (LoanStatus.valueOf(cells.get(3)) == LoanStatus.RETURNED) {
        loan.markReturned(LocalDate.parse(cells.get(4)), Long.parseLong(cells.get(5)));
      }
      result.add(loan);
    }
    return result;
  }

  private List<String> dataLines(Path file) throws IOException {
    return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
        .skip(1)
        .filter(line -> !line.isBlank())
        .toList();
  }

  private String csv(String value) {
    return '"' + value.replace("\"", "\"\"") + '"';
  }

  private List<String> parse(String line) {
    List<String> cells = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean quoted = false;
    for (int index = 0; index < line.length(); index++) {
      char character = line.charAt(index);
      if (character == '"') {
        if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
          current.append('"');
          index++;
        } else {
          quoted = !quoted;
        }
      } else if (character == ',' && !quoted) {
        cells.add(current.toString());
        current.setLength(0);
      } else {
        current.append(character);
      }
    }
    cells.add(current.toString());
    return cells;
  }
}
