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
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class DashboardView {

    public static VBox create(Account account, Consumer<String> onNavigate) {
        VBox dashboard = new VBox(30);
        dashboard.setPadding(new Insets(40));
        dashboard.setAlignment(Pos.TOP_CENTER);
        dashboard.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        dashboard.getChildren().addAll(
            buildButtonBar(onNavigate),
            buildHeader(account),
            buildSpendingChart(account),
            buildBudgetBars(account),
            buildIncomeExpenseSummary(account),
            buildRecentTransactions(account),
            buildAlerts(account)
        );

        return dashboard;
    }

    private static HBox buildButtonBar(Consumer<String> onNavigate) {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button budgetBtn = new Button("Budget");
        budgetBtn.setOnAction(e -> onNavigate.accept("budget"));

        Button transactionBtn = new Button("Transactions");
        transactionBtn.setOnAction(e -> onNavigate.accept("transactions"));

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> onNavigate.accept("search"));

        buttons.getChildren().addAll(budgetBtn, transactionBtn, searchBtn);
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

        PieChart chart = new PieChart();
        chart.setTitle("Expenses by Category");

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
            String color = categoryColors.getOrDefault(data.getName().toUpperCase(), "#bdbdbd");
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        }

        Platform.runLater(() -> {
            chart.lookupAll(".chart-pie-label").forEach(node -> {
                if (node instanceof Labeled labeled) {
                    labeled.setTextFill(Color.WHITE);
                }
            });
        });

        HBox legend = new HBox(15);
        legend.setPadding(new Insets(10));
        legend.setAlignment(Pos.CENTER);
        legend.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        for (PieChart.Data data : chart.getData()) {
            Label label = new Label(data.getName());
            label.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
            Region colorBox = new Region();
            colorBox.setPrefSize(10, 10);

            String nodeStyle = data.getNode().getStyle();
            String fillColor = nodeStyle.substring(nodeStyle.indexOf("#")).replace(";", "");
            colorBox.setStyle("-fx-background-color: " + fillColor + ";");

            HBox entry = new HBox(5, colorBox, label);
            entry.setAlignment(Pos.CENTER);
            legend.getChildren().add(entry);
        }

        chartContainer.getChildren().addAll(chart, legend);
        return chartContainer;
    }

    private static VBox buildIncomeExpenseSummary(Account account) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));

        double income = account.getTotalIncome();
        double expense = account.getTotalExpenses();

        double max = Math.max(income, expense);
        double incomePercent = max == 0 ? 0 : income / max;
        double expensePercent = max == 0 ? 0 : expense / max;

        Label title = new Label("Income vs Expense");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        ProgressBar incomeBar = new ProgressBar(incomePercent);
        incomeBar.setStyle("-fx-accent: #4CAF50;");
        incomeBar.setPrefWidth(400);

        Label incomeLabel = new Label("Income: $" + String.format("%.2f", income));
        incomeLabel.setTextFill(Color.LIGHTGREEN);

        ProgressBar expenseBar = new ProgressBar(expensePercent);
        expenseBar.setStyle("-fx-accent: #E53935;");
        expenseBar.setPrefWidth(400);

        Label expenseLabel = new Label("Expense: $" + String.format("%.2f", expense));
        expenseLabel.setTextFill(Color.SALMON);

        container.getChildren().addAll(title, incomeLabel, incomeBar, expenseLabel, expenseBar);
        return container;
    }

    private static ScrollPane buildBudgetBars(Account account) {
        VBox budgetBox = new VBox(10);
        budgetBox.setAlignment(Pos.CENTER_LEFT);

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
                bar.setPrefWidth(400);

                Label label = new Label(budget.getName() + ": $" + String.format("%.2f", spent) + " / $" + limit);
                label.setTextFill(Color.WHITE);

                VBox entry = new VBox(label, bar);
                entry.setPadding(new Insets(5));
                budgetBox.getChildren().add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ScrollPane scrollPane = new ScrollPane(budgetBox);
        scrollPane.setPrefHeight(220);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scrollPane;
    }

    private static VBox buildRecentTransactions(Account account) {
        VBox recentBox = new VBox(10);
        recentBox.setAlignment(Pos.CENTER_LEFT);

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
            for (int i = transactions.size() - 1; i >= 0 && count < 5; i--, count++) {
                Transaction t = transactions.get(i);
                Label l = new Label(t.toString());
                l.setTextFill(t.isIncome() ? Color.LIGHTGREEN : Color.SALMON);
                items.getChildren().add(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        recentBox.getChildren().addAll(header, items);
        return recentBox;
    }

    private static VBox buildAlerts(Account account) {
        VBox alerts = new VBox(10);
        alerts.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("Alerts");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        header.setTextFill(Color.RED);

        try {
            Field field = Account.class.getDeclaredField("budgets");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Budget> budgets = (List<Budget>) field.get(account);

            for (Budget budget : budgets) {
                if (budget.isOverBudget()) {
                    Label l = new Label("You are OVER budget in: " + budget.getName());
                    l.setTextFill(Color.RED);
                    alerts.getChildren().add(l);
                } else if (budget.isCloseLimit()) {
                    Label l = new Label("Warning: 80% used in: " + budget.getName());
                    l.setTextFill(Color.ORANGE);
                    alerts.getChildren().add(l);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alerts.getChildren().size() == 1) {
            Label none = new Label("No alerts at the moment.");
            none.setTextFill(Color.LIGHTGREEN);
            alerts.getChildren().add(none);
        }

        return alerts;
    }
}
