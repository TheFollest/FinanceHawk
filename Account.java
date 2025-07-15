import java.util.ArrayList;
import java.util.List;

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
    
    public void addTransaction(Transaction t) {
        transactions.add(t);
        for (Budget budget : this.budgets) {
        	// Check status BEFORE adding the transaction
        	boolean wasClose = budget.isCloseLimit();
        	boolean wasOver = budget.isOverBudget();
        	
            budget.addTransaction(t); 
            
            // Check status AFTER adding the transaction
            boolean isClose = budget.isCloseLimit();
        	boolean isOver = budget.isOverBudget();
        	
        	//Notify
        	if (isOver && !wasOver)
        		System.out.println("You have exceeded your " + budget.getName());
        	else if (isClose && !wasClose)
        		System.out.println("You have reached 80% of your " + budget.getName());    
        }
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
    
    
    //For testing
    public void clearTransactions() {
        transactions.clear();
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
}