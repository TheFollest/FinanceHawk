import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class main2test extends Application {

    private Account account = new Account();
    private Stage primaryStage;
    private boolean wasMaximized = false;
    private double prevWidth = 800;
    private double prevHeight = 700;

    private List<RecurringTransaction> recurrRule = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        seedData();

        // Sync recurring transactions
        RecurrSync.syncRecurringTransactions(account, recurrRule);

        primaryStage.setTitle("Finance Hawk");
        primaryStage.setWidth(prevWidth);
        primaryStage.setHeight(prevHeight);
        primaryStage.setResizable(true);

        VBox root = DashboardView.create(account, recurrRule, this::navigate);
        Scene scene = new Scene(root, prevWidth, prevHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void seedData() {
        account.addBudget(new Budget("June Grocery Budget", 300.0, Category.GROCERIES, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30)));
        account.addBudget(new Budget("July Grocery Budget", 350.0, Category.GROCERIES, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31)));
        account.addBudget(new Budget("June Fun Budget", 150.0, Category.ENTERTAINMENT, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30)));

        account.addTransaction(new Transaction(Category.SALARY, 2500.00, LocalDate.of(2025, 6, 1), true, ""));
        account.addTransaction(new Transaction(Category.MEDICAL, 800.00, LocalDate.of(2025, 6, 3), false, ""));
        account.addTransaction(new Transaction(Category.GROCERIES, 230.00, LocalDate.of(2025, 6, 6), false, ""));
        account.addTransaction(new Transaction(Category.ENTERTAINMENT, 75.50, LocalDate.of(2025, 6, 9), false, ""));
        account.addTransaction(new Transaction(Category.OTHER, 100.00, LocalDate.of(2025, 6, 10), false, "Birthday gift for Alex"));

        recurrRule.add(new RecurringTransaction("Netflix", 15.99, LocalDate.of(2025, 1, 15), RecurringTransaction.Frequency.MONTHLY, -1));
    }

    private void showDashboard() {
        VBox root = DashboardView.create(account, recurrRule, this::navigate);
        updateScene(root, "Finance Hawk - Dashboard");
    }

    private void showBudgetDashboard() {
        VBox root = BudgetDashboardView.create(account, this::navigate, this::showBudgetDashboard);
        updateScene(root, "Finance Hawk - Budget");
    }

    private void showTransactionDashboard() {
        VBox root = TransactionDashboardView.create(account, this::navigate, this::showTransactionDashboard);
        updateScene(root, "Finance Hawk - Transactions");
    }

    private void showSearchDashboard() {
        VBox root = SearchDashboardView.create(account, this::navigate, this::showSearchDashboard);
        updateScene(root, "Finance Hawk - Search");
    }

    private void showRecurrDashboardView() {
        VBox root = RecurrDashboardView.create(recurrRule, this::navigate, this::showRecurrDashboardView);
        updateScene(root, "Finance Hawk - Recurring Rules");
    }

    private void updateScene(VBox root, String title) {
        Scene scene = new Scene(root, prevWidth, prevHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        if (wasMaximized) {
            primaryStage.setMaximized(true);
        } else {
            primaryStage.setWidth(prevWidth);
            primaryStage.setHeight(prevHeight);
        }
    }

    private void navigate(String page) {
        wasMaximized = primaryStage.isMaximized();
        prevWidth = primaryStage.getWidth();
        prevHeight = primaryStage.getHeight();

        switch (page) {
            case "budget" -> showBudgetDashboard();
            case "transactions" -> showTransactionDashboard();
            case "search" -> showSearchDashboard();
            case "recurring" -> showRecurrDashboardView();
            default -> showDashboard();
        }
    }
}
