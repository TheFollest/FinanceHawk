import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Account {
    private List<Transaction> transactions;
    private List<Budget> budgets;


    public Account() {
        transactions = new ArrayList<>();
        budgets = new ArrayList<>();
    }

    public void addBudget(Budget budget) {
        this.budgets.add(budget);
    }
    
    public List<String> addTransaction(Transaction t) {
        this.transactions.add(t);
		List<String> notifications = new ArrayList<>(); 
        for (Budget budget : this.budgets) {
        	// Check status BEFORE adding the transaction
            double spentBefore = budget.getTotalSpent(); // Check amount before adding to budget
			budget.addTransaction(t); 
			double spentAfter = budget.getTotalSpent(); // Check amount after adding to budget
            
            // Check status AFTER adding the transaction
            if (spentAfter > spentBefore) {
            boolean wasOver = spentBefore > budget.getLimit();
            boolean isOver = spentAfter > budget.getLimit();

            boolean wasClose = spentBefore >= (budget.getLimit() * 0.8);
            boolean isClose = spentAfter >= (budget.getLimit() * 0.8);
        	
        	//Notify
        	if (isOver && !wasOver)
        		notifications.add("You have exceeded your " + budget.getName());
        	else if (isClose && !wasClose)
        		notifications.add("You have reached 80% of your " + budget.getName());    
			}
		}
		return notifications;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double getBalance() {
        double total = 0;
        for (Transaction t : transactions) {
        	//If the transaction is income, add to balance
        	if (t.isIncome())
        		total = total + t.getAmount();
        	//Else subtract from balance
        	else
        		total = total - t.getAmount();
        }
        return total;
    }
    
    
    //Filter by Category, also check if that is income or expense
    public double FilterCategory(Category category, boolean isIncome) {
        double total = 0;
        for (Transaction t : transactions) {
            if ((category == null || t.getCategory() == category) && t.isIncome() == isIncome) // added null for UI chart
                total = total + t.getAmount();      
        }
        return total;
    }
    
    // Get total income for a specific period
    public double getTotalIncome(LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> t.isIncome() && !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    //Get total expenses for a specific period
    public double getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> !t.isIncome() && !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
	
    
    public void deleteTransaction(Transaction t) {
        // Remove from the account's main list
        this.transactions.remove(t);

        // Also remove from any associated budgets
        for (Budget budget : this.budgets) {
            budget.removeTransaction(t);
        }
    }

    //Make println(Account) work
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("All Transactions:\n");
        for (Transaction t : transactions) {
            sb.append(t).append("\n");
        }
        sb.append("Current Balance: $").append(String.format("%.2f", getBalance()));
        return sb.toString();
    }
    // Adding class for UI expense vs income chart
    public double getTotalIncome() {
        return FilterCategory(null, true);
    }

    public double getTotalExpenses() {
        return FilterCategory(null, false);
    }
	
	public List<Budget> getBudgets() {
		return this.budgets;
	}
	
}