import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Search {
	private List<Transaction> transactions;
	
	//Constructor
    public Search(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Search transactions by description (case-insensitive)
    public List<Transaction> searchByDescription(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return transactions.stream()
                .filter(t -> t.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    // Filter transactions by amount range
    public List<Transaction> filterByAmount(double minAmount, double maxAmount) {
        return transactions.stream()
                .filter(t -> t.getAmount() >= minAmount && t.getAmount() <= maxAmount)
                .collect(Collectors.toList());
    }

    // Filter transactions by date range
    public List<Transaction> filterByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    // Filter transactions by category
    public List<Transaction> filterByCategory(Category category) {
        return transactions.stream()
                //(updated Jul 10).filter(t -> t.getCategory().equalsIgnoreCase(category))
                .filter(t -> t.getCategory() == category)
                //===========================
				.collect(Collectors.toList());
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

}
