import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // Create an Account object
        Account myAccount = new Account();

        // Add some transactions
        myAccount.addTransaction(new Transaction(Category.SALARY, 2000.00, LocalDate.of(2025, 6, 1), true));
        myAccount.addTransaction(new Transaction(Category.RENT, 850.00, LocalDate.of(2025, 6, 2), false));
        myAccount.addTransaction(new Transaction(Category.GROCERIES, 120.45, LocalDate.of(2025, 6, 5), false));
        myAccount.addTransaction(new Transaction(Category.ENTERTAINMENT, 50.00, LocalDate.of(2025, 6, 10), false));
        myAccount.addTransaction(new Transaction(Category.FREELANCE, 300.00, LocalDate.of(2025, 6, 15), true));

        // Print all transactions
        System.out.println(myAccount);

        // Test total by category
        Category testCategory = Category.GROCERIES;
        double spentOnGroceries = myAccount.FilterCategory(testCategory, false);
        System.out.println("Total spent on " + testCategory + ": $" + spentOnGroceries);

        // Test balance
        double currentBalance = myAccount.getBalance();
        System.out.println("Current Balance: $" + currentBalance);
    }
}