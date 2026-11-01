package service;

import interfacepkg.Searchable;
import java.util.ArrayList;
import java.util.List;
import model.Member;

public class MemberService implements Searchable<Member> {
  private final List<Member> members = new ArrayList<>();

  public List<Member> findAll() {
    return List.copyOf(members);
  }

  public void restore(List<Member> values) {
    members.clear();
    members.addAll(values);
  }

  public Member findById(String id) {
    return members.stream()
        .filter(m -> m.getId().equalsIgnoreCase(id))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy độc giả."));
  }

  public Member add(String id, String name, String phone, String email) {
    if (members.stream().anyMatch(m -> m.getId().equalsIgnoreCase(id)))
      throw new IllegalArgumentException("Mã độc giả đã tồn tại.");
    Member member = new Member(id, name, phone, email);
    members.add(member);
    return member;
  }

  public void update(String id, String name, String phone, String email) {
    Member member = findById(id);
    member.setFullName(name);
    member.setPhone(phone);
    member.setEmail(email);
  }

  public void delete(String id, LoanService loanService) {
    if (loanService.hasActiveLoanForMember(id))
      throw new IllegalStateException("Không thể xóa độc giả đang mượn tài liệu.");
    members.remove(findById(id));
  }

  @Override
  public List<Member> search(String keyword) {
    String key = normalize(keyword);
    return members.stream()
        .filter(
            m ->
                m.getId().toLowerCase().contains(key)
                    || m.getFullName().toLowerCase().contains(key))
        .toList();
  }
}
