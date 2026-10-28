package strategy;

import java.time.LocalDate;
import model.Loan;

public interface FineCalculationStrategy {
  long calculate(Loan loan, LocalDate returnDate);
}
