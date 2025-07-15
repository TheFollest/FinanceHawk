import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class main2test extends Application {

    private Account account = new Account();
    private Stage primaryStage;
    private boolean wasMaximized = false;
    private double prevWidth = 800;
    private double prevHeight = 700;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        seedData();

        primaryStage.setTitle("Finance Hawk");
        primaryStage.setWidth(prevWidth);
        primaryStage.setHeight(prevHeight);
        primaryStage.setResizable(true);

        VBox root = DashboardView.create(account, this::navigate);
        Scene scene = new Scene(root, prevWidth, prevHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void seedData() {
        account.addBudget(new Budget("June Grocery Budget", 300.0, Category.GROCERIES));
        account.addBudget(new Budget("June Fun Budget", 150.0, Category.ENTERTAINMENT));

        account.addTransaction(new Transaction(Category.SALARY, 2500.00, LocalDate.of(2025, 6, 1), true, ""));
        account.addTransaction(new Transaction(Category.MEDICAL, 800.00, LocalDate.of(2025, 6, 3), false, ""));
        account.addTransaction(new Transaction(Category.GROCERIES, 230.00, LocalDate.of(2025, 6, 6), false, ""));
        account.addTransaction(new Transaction(Category.ENTERTAINMENT, 75.50, LocalDate.of(2025, 6, 9), false, ""));
        account.addTransaction(new Transaction(Category.OTHER, 100.00, LocalDate.of(2025, 6, 10), false, "Birthday gift for Alex"));
    }

    private void showDashboard() {
        VBox root = DashboardView.create(account, this::navigate);
        updateScene(root, "Finance Hawk - Dashboard");
    }

    private void showBudgetDashboard() {
        VBox root = BudgetDashboardView.create(account, this::navigate, this::showDashboard);
        updateScene(root, "Finance Hawk - Budget");
    }

    private void showTransactionDashboard() {
        VBox root = TransactionDashboardView.create(account, this::navigate, this::showDashboard);
        updateScene(root, "Finance Hawk - Transactions");
    }

    private void showSearchDashboard() {
        VBox root = SearchDashboardView.create(account, this::navigate, this::showDashboard);
        updateScene(root, "Finance Hawk - Search");
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
            default -> showDashboard();
        }
    }
}
