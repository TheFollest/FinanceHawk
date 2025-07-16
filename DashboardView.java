import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

public class DashboardView {

public static VBox create(Account account, List<RecurringTransaction> recurringList, Consumer<String> onNavigate) {
    VBox dashboard = new VBox(30);
    dashboard.setPadding(new Insets(40));
    dashboard.setAlignment(Pos.TOP_CENTER);
    dashboard.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

    // Top summary row (Budgets, Income/Expense, Recurring)
    HBox budgetAndRecurringRow = new HBox(30);
    budgetAndRecurringRow.setAlignment(Pos.CENTER);
    budgetAndRecurringRow.setPadding(new Insets(20));

    // Set fixed width to all components for uniform layout
    VBox leftBox = buildBudgetBars(account);
    leftBox.setPrefWidth(500);

    VBox centerBox = buildIncomeExpenseSummary(account);
    centerBox.setPrefWidth(1200);

    VBox rightBox = buildRecurringSummary(recurringList);
    rightBox.setPrefWidth(500);

    budgetAndRecurringRow.getChildren().addAll(leftBox, centerBox, rightBox);

    // Bottom row (Recent Transactions & Alerts)
    HBox bottomRow = new HBox(300);
    bottomRow.setAlignment(Pos.CENTER);
    bottomRow.getChildren().addAll(buildRecentTransactions(account), buildAlerts(account));

    // Build the full dashboard
    dashboard.getChildren().addAll(
        buildButtonBar(onNavigate, "dashboard"),
        buildHeader(account),
        buildSpendingChart(account),
        budgetAndRecurringRow,
        bottomRow
    );

    return dashboard;
}




    private static HBox buildButtonBar(Consumer<String> onNavigate, String currentPage) {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(0, 0, 10, 0));

        Button dashboardBtn = new Button("Dashboard");
        dashboardBtn.setOnAction(e -> onNavigate.accept("dashboard"));
        dashboardBtn.setDisable("dashboard".equals(currentPage));

        Button budgetBtn = new Button("Budget");
        budgetBtn.setOnAction(e -> onNavigate.accept("budget"));
        budgetBtn.setDisable("budget".equals(currentPage));

        Button transactionBtn = new Button("Transactions");
        transactionBtn.setOnAction(e -> onNavigate.accept("transactions"));
        transactionBtn.setDisable("transactions".equals(currentPage));

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> onNavigate.accept("search"));
        searchBtn.setDisable("search".equals(currentPage));

        Button recurringBtn = new Button("Recurring");
        recurringBtn.setOnAction(e -> onNavigate.accept("recurring"));
        recurringBtn.setDisable("recurring".equals(currentPage));

        buttons.getChildren().addAll(dashboardBtn, budgetBtn, transactionBtn, searchBtn, recurringBtn);
        return buttons;
    }



    private static HBox buildHeader(Account account) {
        double balance = account.getBalance();
        Label header = new Label("Finance Hawk - Dashboard\nBalance: $" + String.format("%.2f", balance));
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        header.setTextFill(Color.WHITE);
        header.setEffect(new DropShadow());

        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        return headerBox;
    }

  private static VBox buildSpendingChart(Account account) {
    VBox chartContainer = new VBox(10);
    chartContainer.setAlignment(Pos.CENTER);
    chartContainer.setPadding(new Insets(20));
    chartContainer.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.08);
        -fx-background-radius: 15;
        -fx-border-radius: 15;
        -fx-border-color: white;
        -fx-border-width: 1;
    """);

    PieChart chart = new PieChart();
    chart.setTitle("Expenses by Category (Click a slice to filter)");
	
	/*Put aside as a separate class
    Map<String, String> categoryColors = new HashMap<>();
    categoryColors.put("SALARY", "#ffee58");
    categoryColors.put("FREELANCE", "#c0ca33");
    categoryColors.put("RENT", "#d32f2f");
    categoryColors.put("GROCERIES", "#ff704d");
    categoryColors.put("UTILITIES", "#8e24aa");
    categoryColors.put("ENTERTAINMENT", "#ffa726");
    categoryColors.put("DINING", "#5c6bc0");
    categoryColors.put("SUBSCRIPTION", "#26c6da");
    categoryColors.put("TRANSPORT", "#7cb342");
    categoryColors.put("MEDICAL", "#66bb6a");
    categoryColors.put("OTHER", "#42a5f5");
	*/

    for (Category cat : Category.values()) {
        double amount = account.FilterCategory(cat, false);
        if (amount > 0) {
            PieChart.Data data = new PieChart.Data(cat.toString(), amount);
            chart.getData().add(data);
        }
    }

    chart.setLabelsVisible(true);
    chart.setLegendVisible(false);
    chart.applyCss();
    chart.layout();

    for (PieChart.Data data : chart.getData()) {
        //String color = categoryColors.getOrDefault(data.getName().toUpperCase(), "#bdbdbd");
		String color = getCategoryColor(data.getName()); 
        data.getNode().setStyle("-fx-pie-color: " + color + ";");
		
        // Updated Jul 16: Click listener to show filtered transactions
        data.getNode().setOnMouseClicked(event -> {
            showFilteredTransactions(account, data.getName());
        });
    }
	
	/*
    Platform.runLater(() -> {
        chart.lookupAll(".chart-pie-label").forEach(node -> {
            if (node instanceof Labeled labeled) {
                labeled.setTextFill(Color.WHITE);
            }
        });
    });
	*/
	
	Platform.runLater(() -> {
        // Find the title label by its style class and set its color
        Label titleNode = (Label) chart.lookup(".chart-title");
        if (titleNode != null) {
            titleNode.setStyle("-fx-text-fill: white;");
        }

        // Also ensure the pie slice labels are white
        chart.lookupAll(".chart-pie-label").forEach(node -> {
            node.setStyle("-fx-fill: white;"); // Force to set color to white
        });
    });
	


    /* Put aside as a separate class
	HBox legend = new HBox(15);
    legend.setPadding(new Insets(10));
    legend.setAlignment(Pos.CENTER);
    legend.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 10;");

    for (PieChart.Data data : chart.getData()) {
        Label label = new Label(data.getName());
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Region colorBox = new Region();
        colorBox.setPrefSize(10, 10);

        String nodeStyle = data.getNode().getStyle();
        String fillColor = nodeStyle.substring(nodeStyle.indexOf("#")).replace(";", "");
        colorBox.setStyle("-fx-background-color: " + fillColor + ";");

        HBox entry = new HBox(5, colorBox, label);
        entry.setAlignment(Pos.CENTER);
        legend.getChildren().add(entry);
    }
	*/
	
	// Build the custom legend
    HBox legend = buildChartLegend(chart);
    chartContainer.getChildren().addAll(chart, legend);
    return chartContainer;
	
	
  }
	//Helper method to get category color
	private static String getCategoryColor(String categoryName) {
		Map<String, String> categoryColors = new HashMap<>();
		categoryColors.put("SALARY", "#ffee58");
		categoryColors.put("RENT", "#d32f2f");
		categoryColors.put("GROCERIES", "#ff704d");
		categoryColors.put("UTILITIES", "#8e24aa");
		categoryColors.put("ENTERTAINMENT", "#ffa726");
		categoryColors.put("DINING", "#5c6bc0");
		categoryColors.put("SUBSCRIPTION", "#26c6da");
		categoryColors.put("TRANSPORT", "#7cb342");
		categoryColors.put("MEDICAL", "#66bb6a");
		categoryColors.put("OTHER", "#42a5f5");
		return categoryColors.getOrDefault(categoryName.toUpperCase(), "#bdbdbd");
	}
	
	// Helper method to build the legend
	private static HBox buildChartLegend(PieChart chart) {
		HBox legend = new HBox(15);
		legend.setPadding(new Insets(10));
		legend.setAlignment(Pos.CENTER);
		legend.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 10;");

		for (PieChart.Data data : chart.getData()) {
			Label label = new Label(data.getName());
			label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
			Region colorBox = new Region();
			colorBox.setPrefSize(10, 10);
			colorBox.setStyle("-fx-background-color: " + getCategoryColor(data.getName()) + ";");
			HBox entry = new HBox(5, colorBox, label);
			entry.setAlignment(Pos.CENTER);
			legend.getChildren().add(entry);
		}
		return legend;
	}
    
	private static VBox buildIncomeExpenseSummary(Account account) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10;");

        double income = account.getTotalIncome();
        double expense = account.getTotalExpenses();

        double max = Math.max(income, expense);
        double incomePercent = max == 0 ? 0 : income / max;
        double expensePercent = max == 0 ? 0 : expense / max;

        Label title = new Label("Income vs Expense");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        ProgressBar incomeBar = new ProgressBar(incomePercent);
        incomeBar.setStyle("-fx-accent: #4CAF50;");
        incomeBar.setPrefWidth(800);

        Label incomeLabel = new Label("Income: $" + String.format("%.2f", income));
        incomeLabel.setTextFill(Color.LIGHTGREEN);
        incomeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        ProgressBar expenseBar = new ProgressBar(expensePercent);
        expenseBar.setStyle("-fx-accent: #E53935;");
        expenseBar.setPrefWidth(800);
        
        Label expenseLabel = new Label("Expense: $" + String.format("%.2f", expense));
        expenseLabel.setTextFill(Color.SALMON);
        expenseLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));

        container.getChildren().addAll(title, incomeLabel, incomeBar, expenseLabel, expenseBar);
        return container;
    }
	
	//show the filter results in a new window
	private static void showFilteredTransactions(Account account, String categoryName) {
		Category category = Category.valueOf(categoryName.toUpperCase());
		Search search = new Search(account.getTransactions());
		List<Transaction> results = search.filterByCategory(category);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Filtered Transactions");
		alert.setHeaderText("Transactions for category: " + categoryName);

		ListView<String> listView = new ListView<>();
		if (results.isEmpty()) 
			listView.getItems().add("No transactions found for this category.");
		else {
			for (Transaction t : results) 
				listView.getItems().add(t.toString());
		}

    alert.getDialogPane().setContent(listView);
    alert.showAndWait();
	}

// Change method signature to match:
private static VBox buildBudgetBars(Account account) {
    VBox box = new VBox(10);
    box.setPadding(new Insets(10));
    box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10;");
    box.setPrefWidth(750); // Or your desired width
    box.setPrefHeight(250); // Match recurring

    VBox content = new VBox(5);
    content.setAlignment(Pos.CENTER_LEFT);

    try {
        Field field = Account.class.getDeclaredField("budgets");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Budget> budgets = (List<Budget>) field.get(account);

        for (Budget budget : budgets) {
            double spent = budget.getTotalSpent();

            Field limitField = Budget.class.getDeclaredField("limit");
            limitField.setAccessible(true);
            double limit = limitField.getDouble(budget);

            double percent = Math.min(spent / limit, 1.0);
            ProgressBar bar = new ProgressBar(percent);
            bar.setPrefWidth(750);

            Label label = new Label(budget.getName() + ": $" + String.format("%.2f", spent) + " / $" + limit);
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));

            VBox entry = new VBox(label, bar);
            entry.setPadding(new Insets(5));
            content.getChildren().add(entry);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setPrefHeight(220);  // height within wrapper
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

    box.getChildren().add(scrollPane);
    return box;
}


    private static VBox buildRecentTransactions(Account account) {
    VBox recentBox = new VBox(10);
    recentBox.setAlignment(Pos.TOP_LEFT);
    recentBox.setPrefWidth(700);
    recentBox.setPadding(new Insets(10));
    recentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10;");
    Label header = new Label("Recent Transactions");
    header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
    header.setTextFill(Color.WHITE);

    VBox items = new VBox(5);

    try {
        Field field = Account.class.getDeclaredField("transactions");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Transaction> transactions = (List<Transaction>) field.get(account);

        int count = 0;
        for (int i = transactions.size() - 1; i >= 0 && count < 10; i--, count++) {
            Transaction t = transactions.get(i);
            Label l = new Label(t.toString());
            l.setTextFill(t.isIncome() ? Color.LIGHTGREEN : Color.SALMON);
            l.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
            items.getChildren().add(l);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    ScrollPane scrollPane = new ScrollPane(items);
    scrollPane.setPrefHeight(120);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

    recentBox.getChildren().addAll(header, scrollPane);
    return recentBox;
}

    private static VBox buildAlerts(Account account) {
        VBox alerts = new VBox(10);
        alerts.setAlignment(Pos.CENTER_LEFT);
        alerts.setPrefWidth(700);
        Label header = new Label("Alerts");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        header.setTextFill(Color.RED);
        alerts.setPadding(new Insets(10));
        alerts.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10;");
        try {
            Field field = Account.class.getDeclaredField("budgets");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Budget> budgets = (List<Budget>) field.get(account);

            for (Budget budget : budgets) {
                if (budget.isOverBudget()) {
                    Label l = new Label("You are OVER budget in: " + budget.getName());
                    l.setTextFill(Color.RED);
                    l.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
                    alerts.getChildren().add(l);
                } else if (budget.isCloseLimit()) {
                    Label l = new Label("Warning: 80% used in: " + budget.getName());
                    l.setTextFill(Color.ORANGE);
                    l.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
                    alerts.getChildren().add(l);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alerts.getChildren().size() == 0) {
            Label none = new Label("No alerts at the moment.");
            none.setTextFill(Color.LIGHTGREEN);
            none.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            alerts.getChildren().add(none);
        }

        return alerts;
    }
private static VBox buildRecurringSummary(List<RecurringTransaction> recurringList) {
    VBox box = new VBox(10);
    box.setPadding(new Insets(10));
    box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10;");
    box.setPrefWidth(750);

    Label title = new Label("Recurring Transactions");
    title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
    title.setTextFill(Color.WHITE);

    VBox content = new VBox(5);
    if (recurringList.isEmpty()) {
        Label none = new Label("No recurring rules.");
        none.setTextFill(Color.LIGHTGRAY);
        content.getChildren().add(none);
    } else {
        for (RecurringTransaction rt : recurringList) {
            Label item = new Label(rt.getDescription() + ": $" + String.format("%.2f", rt.getAmount()));
            item.setTextFill(Color.WHITE);
            item.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
            content.getChildren().add(item);
        }
    }

    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setPrefHeight(220); // Matches the budget bar height
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

    box.getChildren().addAll(title, scrollPane);
    return box;
}




}
