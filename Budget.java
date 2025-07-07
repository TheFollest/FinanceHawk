import java.util.*;

public class Budget {
    private String name;
    private double limit;
    private Category category;
    private List<Transaction> transactions;

    public Budget(String name, double limit, Category category) {
        this.name = name;
        this.limit = limit;
        this.category = category;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction t) {
        if (t.getCategory() == category) {
            transactions.add(t);
        }
    }

    public double getTotalSpent() {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    public boolean isOverBudget() {
        return getTotalSpent() > limit;
    }

    public double getRemaining() {
        return limit - getTotalSpent();
    }

    public String toString() {
        return name + " [" + category + "]: $" + getTotalSpent() + " / $" + limit +
               (isOverBudget() ? " (Over Budget!)" : "");
    }
}
