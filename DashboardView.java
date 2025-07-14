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
import java.util.List;
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
            buildRecurringList(),
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

        buttons.getChildren().addAll(budgetBtn, transactionBtn);
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

        for (Category cat : Category.values()) {
            double amount = account.FilterCategory(cat, false);
            if (amount > 0) {
                PieChart.Data data = new PieChart.Data(cat.toString(), amount);
                chart.getData().add(data);
            }
        }

        chart.setLabelsVisible(true);
        chart.setLegendVisible(false);  // We'll draw our own

        // Apply custom colors per category
        for (PieChart.Data data : chart.getData()) {
            String color = switch (data.getName().toUpperCase()) {
                case "GROCERIES" -> "#ff704d";
                case "ENTERTAINMENT" -> "#ffa726";
                case "MEDICAL" -> "#66bb6a";
                case "OTHER" -> "#42a5f5";
                case "SALARY" -> "#ffee58";
                default -> "#bdbdbd";
            };
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        }

        chart.applyCss();
        chart.layout();
        chart.lookupAll(".chart-pie-label").forEach(node -> {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill: white;");
            }
        });

        // Custom Legend
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

    private static VBox buildRecurringList() {
        VBox recurringBox = new VBox(10);
        recurringBox.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("Recurring Transactions");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        header.setTextFill(Color.WHITE);

        Label placeholder = new Label("(Recurring transactions will be shown here.)");
        placeholder.setTextFill(Color.WHITE);

        recurringBox.getChildren().addAll(header, placeholder);
        return recurringBox;
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
