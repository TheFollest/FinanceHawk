import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String SEPARATOR = "|";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
	private static final String BUDGETS_FILE = "budgets.txt";
    private static final String RECURRING_RULES_FILE = "recurring_rules.txt";

    // Main method to save all application data
    public static void saveAllData(Account account, List<RecurringTransaction> recurrRules) {
        saveTransactions(account.getTransactions());
        saveBudgets(account.getBudgets()); 
        saveRecurringRules(recurrRules);
    }
	
	//Remove addTrans and addBudg class


    // Saves ALL transactions currently in the account to a file
    private static void saveTransactions(List<Transaction> transactions) {
		/*Put into try catch block. Also, use for loops with list of transactions
		FileWriter fw = new FileWriter(tFile, true);
		PrintWriter pw = new PrintWriter(fw);
		for (int i = 0; i < t.size(); i++)
		{
			pw.println(t.get(i));
		}
		pw.close();*/
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction t : transactions) {
                
                String line = String.join(SEPARATOR,
                        t.getCategory().toString(),
                        String.valueOf(t.getAmount()),
                        t.getDate().toString(),
                        String.valueOf(t.isIncome()),
                        t.getDescription());
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }
	
    //Saves budget data
	//Same way with saves all Transactions
	private static void saveBudgets(List<Budget> budgets) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BUDGETS_FILE))) {
            for (Budget b : budgets) {
                String line = String.join(SEPARATOR, b.getName(), String.valueOf(b.getLimit()),
                        b.getCategory().toString(), b.getStartDate().toString(), b.getEndDate().toString());
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error saving budgets: " + e.getMessage());
        }
    }	

    // Saves the definitions for recurring transactions
    private static void saveRecurringRules(List<RecurringTransaction> rules) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RECURRING_RULES_FILE))) {
            for (RecurringTransaction r : rules) {
                String line = String.join(SEPARATOR,
                    r.getDescription(), r.getCategory().toString(), String.valueOf(r.getAmount()), 
                    r.getStartDate().toString(), r.getFrequency().toString(), 
                    String.valueOf(r.getMaxOccurrences()), String.valueOf(r.isIncome()));
				pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error saving recurring rules: " + e.getMessage());
        }
    }
	
	
	// Main method to load all data into the application
    public static void loadAllData(Account account, List<RecurringTransaction> recurrRules) {
        loadTransactions(account);
        loadBudgets(account); // Now loads budgets into the account
        loadRecurringRules(recurrRules);
    }

    // Loads transactions from the file into the account
    public static void loadTransactions(Account account) {
        File f = new File(TRANSACTIONS_FILE);
        if (!f.exists()) return;
        
		/* Combine 3 lines. Also, put codes into try/catch block
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String l = reader.readLine();
		while (l !=	null)
		*/
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
				//Reach each line and split into parts, Separator is |
                String[] parts = line.split("\\" + SEPARATOR);
                if (parts.length == 5) {
                    //Put each part into value of a transaction
					Transaction t = new Transaction(Category.valueOf(parts[0]), Double.parseDouble(parts[1]),
                            LocalDate.parse(parts[2]), Boolean.parseBoolean(parts[3]), parts[4]);
                    account.addTransaction(t);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }
	
	// Loads budget data
    public static void loadBudgets(Account account) {
        File f = new File(BUDGETS_FILE);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + SEPARATOR);
                if (parts.length == 5) {
                    Budget b = new Budget(parts[0], Double.parseDouble(parts[1]), Category.valueOf(parts[2]),
                            LocalDate.parse(parts[3]), LocalDate.parse(parts[4]));
                    account.addBudget(b); // Add loaded budget to the account
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading budgets: " + e.getMessage());
        }
    }	

    // Loads recurring transaction rules
    public static void loadRecurringRules(List<RecurringTransaction> rules) {
        File f = new File(RECURRING_RULES_FILE);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + SEPARATOR);
                if (parts.length == 7) {
                    rules.add(new RecurringTransaction(parts[0], Category.valueOf(parts[1]), 
                        Double.parseDouble(parts[2]), LocalDate.parse(parts[3]), 
                        RecurringTransaction.Frequency.valueOf(parts[4]), Integer.parseInt(parts[5]), 
                        Boolean.parseBoolean(parts[6])));
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading recurring rules: " + e.getMessage());
        }
    }
}