import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Budget {
    private String name;
    private double limit;
    private Category category;
    private List<Transaction> transactions;
    private LocalDate startDate; 
    private LocalDate endDate;   

    public Budget(String name, double limit, Category category, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.limit = limit;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate; 
        this.transactions = new ArrayList<>();
    }
    
    public String getName() {
        return this.name;
    }

    public void addTransaction(Transaction t) {   
    	boolean withinTime = !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate);
    	//(updated Jul 10)  if (t.getCategory() == category) {
		if (t.getCategory() == this.category && !t.isIncome() && withinTime) {
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
	
    public void removeTransaction(Transaction t) {
        this.transactions.remove(t);
    }
	
	public double getLimit() {
		return this.limit;
	}

	public LocalDate getStartDate() {
		return this.startDate;
	}

	public LocalDate getEndDate() {
		return this.endDate;
	}
	
	public Category getCategory() { 
		return this.category; 
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
