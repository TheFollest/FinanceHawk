import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Main {
	
	//Helper method to print transaction
    public static void printTransactions(List<Transaction> transactions) {
    	
		if (transactions.isEmpty()) {
			System.out.println("No transactions to display.");
		}
		for (Transaction t : transactions) {
			System.out.println(t);
		}
		
		return;
	}
    
    //Helper method to print Search result
    public static void printSearch(List<Transaction> results) {
        if (results.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction t : results) {
                System.out.println(t);
            }
        }
    }
    
    public static void main(String[] args) {
 
    	 // ===1.Test Account and Transaction===
        Account account = new Account();
        
        //Create and add budget
        Budget groceryBudget = new Budget("June Grocery Budget", 300.0, Category.GROCERIES, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31));
        Budget entertainmentBudget = new Budget("June Fun Budget", 150.0, Category.ENTERTAINMENT, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31));        
        account.addBudget(groceryBudget);
        account.addBudget(entertainmentBudget);

        Transaction t1 = new Transaction(Category.SALARY, 2500.00, LocalDate.of(2025, 6, 1), true, "");
        Transaction t2 = new Transaction(Category.MEDICAL, 800.00, LocalDate.of(2025, 6, 3), false, "");
        Transaction t3 = new Transaction(Category.GROCERIES, 230.00, LocalDate.of(2025, 6, 6), false, "");
        Transaction t4 = new Transaction(Category.ENTERTAINMENT, 75.50, LocalDate.of(2025, 6, 9), false, "");
        Transaction t5 = new Transaction(Category.OTHER, 100.00, LocalDate.of(2025, 6, 10), false, "Birthday gift for Alex");
        Transaction t6 = new Transaction(Category.GROCERIES, 100.00, LocalDate.of(2025, 6, 7), false, "");
        Transaction t7 = new Transaction(Category.ENTERTAINMENT, 55.00, LocalDate.of(2025, 6, 9), false, "");
        

        account.addTransaction(t1);
        account.addTransaction(t2);
        account.addTransaction(t3);
        account.addTransaction(t4);
        account.addTransaction(t5);

        System.out.println("=== All Transactions in Account as of June 6 ===");
        System.out.println(account);
        System.out.println("Balance: $" + account.getBalance());

        // ===2.Test Budget===
        System.out.println("\n===Add transaction making over budget===");
        account.addTransaction(t6);
        
        System.out.println("\n===Add transaction making 80% budget===");
        account.addTransaction(t7);
        
        System.out.println("\n=== Budget Tracking ===");
        System.out.println(groceryBudget);
        System.out.println(entertainmentBudget);

       
        
     	// ===3.Test RecurringTransaction===
        List<RecurringTransaction> RecurrList= new ArrayList<>();
        RecurrList.add(new RecurringTransaction("Monthly Rent", 1200.00, LocalDate.of(2025, 1, 1),RecurringTransaction.Frequency.MONTHLY, -1));
        RecurrList.add(new RecurringTransaction("Netflix", 15.99, LocalDate.of(2025, 1, 15), RecurringTransaction.Frequency.MONTHLY, -1));
        
        //Update all recurring transactions. Supposed to run at the beginning.
        RecurrSync.syncRecurringTransactions(account, RecurrList);
        
        System.out.println("\n=== All Transactions in Account as of as of today ====");
        System.out.println(account);

        
        
        // ===4.Test Search===
        
        Search search = new Search(account.getTransactions());

        System.out.println("\n===Search by Description: 'gift'===");
        List<Transaction> wordSearch = search.searchByDescription("gift");
        printSearch(wordSearch);
        
        System.out.println("\n===Search by Keyword 'groceries' (should match category name)===");
        List<Transaction> cateSearch = search.searchByDescription("groceries");
        printSearch(cateSearch);

        System.out.println("\n===Filter by Amount: $50 - $300===");
        List<Transaction> amountFilter = search.filterByAmount(50, 300);
        printSearch(amountFilter);

        System.out.println("\n===Filter by Date: June 1 to June 30===");
        List<Transaction> dateFilter = search.filterByDateRange(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));
        printSearch(dateFilter);

        System.out.println("\n===Filter by Category: ENTERTAINMENT===");
        List<Transaction> cateFilter = search.filterByCategory(Category.ENTERTAINMENT);
        printSearch(cateFilter);
        
        System.out.println("\n===Search Monthly and then filter by Date: March 1 to May 30===");
        //Chain search, Search for keyword first and then filter those search results
        List<Transaction> rent = search.searchByDescription("Rent");
        Search filteredSearch = new Search(rent);
        List<Transaction> rentinDate = filteredSearch.filterByDateRange(LocalDate.of(2025, 3, 1), LocalDate.of(2025, 5, 30));
        printSearch(rentinDate);

    }
}