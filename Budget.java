import java.util.ArrayList;
import java.util.List;

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
    
    public String getName() {
        return this.name;
    }

    public void addTransaction(Transaction t) {    
	//(updated Jul 10)  if (t.getCategory() == category) {
		if (t.getCategory() == this.category && !t.isIncome()) {
	//=======================
			transactions.add(t);
        }
    }

    public double getTotalSpent() {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    public boolean isOverBudget() {
        return getTotalSpent() > limit;
    }
    
    public boolean isCloseLimit() {
        return getTotalSpent() >= (this.limit * 0.8);
    }
    
    public double getRemaining() {
        return limit - getTotalSpent();
    }
	
	@Override
    public String toString() {
        /*(updated Jul 10)return name + " [" + category + "]: $" + getTotalSpent() + " / $" + limit +
               (isOverBudget() ? " (Over Budget!)" : "");
		*/
		return name + " [" + category + "]: $" + String.format("%.2f", getTotalSpent()) + " / $" + limit +
               (isOverBudget() ? " (Over Budget!)" : (isCloseLimit() ? " (80% of Limit!)" :""));
		//==================================
    }
}
