import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RecurringTransaction {

    // Enum for recurrence frequency
    public enum Frequency {
        DAILY, WEEKLY, MONTHLY
    }

    // Recurring transaction properties
    private String description;
    private double amount;
    private LocalDate startDate;
    private Frequency frequency;
    private int maxOccurrences; // -1 for infinite
    private List<Transaction> transactionHistory;

    public RecurringTransaction(String description, double amount, LocalDate startDate,
                                Frequency frequency, int maxOccurrences) {
        this.description = description;
        this.amount = amount;
        this.startDate = startDate;
        this.frequency = frequency;
        this.maxOccurrences = maxOccurrences;
        this.transactionHistory = new ArrayList<>();
        generateInitialTransaction();
    }

    // Generate initial transaction
    private void generateInitialTransaction() {
        transactionHistory.add(new Transaction(description, amount, startDate));
    }

    // Generate transactions up to a specific date
    public void generateTransactionsUntil(LocalDate endDate) {
        LocalDate currentDate = startDate;
        int occurrences = transactionHistory.size();

        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            if (maxOccurrences != -1 && occurrences >= maxOccurrences) {
                break;
            }

            currentDate = getNextDate(currentDate);
            if (currentDate.isAfter(endDate)) {
                break;
            }

            transactionHistory.add(new Transaction(description, amount, currentDate));
            occurrences++;
        }
    }

    // Calculate next date based on frequency
    private LocalDate getNextDate(LocalDate currentDate) {
        switch (frequency) {
            case DAILY:
                return currentDate.plusDays(1);
            case WEEKLY:
                return currentDate.plusWeeks(1);
            case MONTHLY:
                return currentDate.plusMonths(1);
            default:
                throw new IllegalStateException("Unknown frequency");
        }
    }

    // Get transaction history
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }


}

