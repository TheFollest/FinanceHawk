import java.util.ArrayList;
import java.util.List;

public class Account {
    private List<Transaction> transactions;
    
    public Account() {
        transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
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
            if (t.getCategory() == category && t.isIncome() == isIncome) 
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
}