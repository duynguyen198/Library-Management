package strategy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import model.Loan;

public class StandardFineStrategy implements FineCalculationStrategy {
  public static final long DAILY_FINE = 5_000;

  @Override
  public long calculate(Loan loan, LocalDate returnDate) {
    return loan.getDetails().stream()
        .mapToLong(
            detail ->
                Math.max(0, ChronoUnit.DAYS.between(detail.getDueDate(), returnDate)) * DAILY_FINE)
        .sum();
  }
}
