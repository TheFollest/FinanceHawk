package com.financehawk.main;
import com.financehawk.data.DataManager;
import com.financehawk.logic.RecurrSync;
import com.financehawk.model.*;
import com.financehawk.ui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

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
        

        // Load the RULES for budgets and recurring transactions first.
        DataManager.loadBudgets(account);
        DataManager.loadRecurringRules(recurrRule);
		
		// If no rules were loaded, seed with default data (for first-time run)
        if (recurrRule.isEmpty() && account.getTransactions().isEmpty() && account.getBudgets().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Welcome!");
            alert.setHeaderText("Welcome to Finance Hawk");
            alert.setContentText("We've loaded some sample data to help you get started.");
            alert.showAndWait();
			seedData();
        }
		
		// Load all transactions
		DataManager.loadTransactions(account);
		
		//Sync and generate NEW recurring transactions
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
	
	@Override
    public void stop() {
        //Save all recurring rules when the application closes      
        DataManager.saveAllData(account, recurrRule);     
    }

    private void seedData() {
        account.addBudget(new Budget("June Grocery Budget", 300.0, Category.GROCERIES, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30)));
        account.addBudget(new Budget("July Grocery Budget", 350.0, Category.GROCERIES, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31)));
        account.addBudget(new Budget("June Fun Budget", 150.0, Category.ENTERTAINMENT, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30)));
        account.addTransaction(new Transaction(Category.GROCERIES, 120.00, LocalDate.of(2025, 6, 10), false, ""));
        account.addTransaction(new Transaction(Category.FREELANCE, 1500.00, LocalDate.of(2025, 6, 20), true, ""));
        account.addTransaction(new Transaction(Category.OTHER, 100.00, LocalDate.of(2025, 6, 10), false, "Birthday gift for Alex"));
		recurrRule.add(new RecurringTransaction("Monthly Salary", Category.SALARY, 3000.00, LocalDate.of(2025, 6, 25), RecurringTransaction.Frequency.MONTHLY, -1, true));
		recurrRule.add(new RecurringTransaction("Netflix", Category.SUBSCRIPTION, 15.99, LocalDate.of(2025, 6, 15), RecurringTransaction.Frequency.MONTHLY, -1, false));
		recurrRule.add(new RecurringTransaction("Monthly Rent", Category.RENT, 1200.00, LocalDate.of(2025, 6, 1), RecurringTransaction.Frequency.MONTHLY, -1, false));
    }

    private void showDashboard() {
        VBox root = DashboardView.create(account, recurrRule, this::navigate);
        updateScene(root, "Finance Hawk - Dashboard");
    }

    private void showBudgetDashboard() {
        VBox root = BudgetDashboardView.create(account, this::navigate);
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
        VBox root = RecurrDashboardView.create(account, recurrRule, this::navigate, this::showRecurrDashboardView);
        updateScene(root, "Finance Hawk - Recurring Rules");
    }
	
	private void showReportsDashboard() {
    VBox root = ReportsDashboardView.create(account, this::navigate);
    updateScene(root, "Finance Hawk - Reports");
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
			case "reports" -> showReportsDashboard();
            default -> showDashboard();
        }
    }
}
