import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.function.Consumer;

public class ReportsDashboardView {

    public static VBox create(Account account, Consumer<String> onNavigate) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        Label title = new Label("Custom Reports");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        // This VBox will hold the chart after it's generated
        VBox reportContainer = new VBox();
        reportContainer.setAlignment(Pos.CENTER);
        reportContainer.setPadding(new Insets(20, 0, 0, 0));

        // Build the controls for generating the report
        HBox controls = buildReportControls(account, reportContainer);

        layout.getChildren().addAll(
            buildButtonBar(onNavigate),
            title,
            controls,
            new Separator(),
            reportContainer
        );
        return layout;
    }

    private static HBox buildReportControls(Account account, VBox reportContainer) {
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));

        DatePicker startDatePicker = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        Button generateBtn = new Button("Generate Report");
        generateBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        generateBtn.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate != null && endDate != null && !startDate.isAfter(endDate)) {
                // Generate the chart and display it in the container
                Node reportChart = buildIncomeExpenseChartForPeriod(account, startDate, endDate);
                reportContainer.getChildren().setAll(reportChart);
            } else {
                // Clear previous results and show an error if dates are invalid
                reportContainer.getChildren().clear();
                Label errorLabel = new Label("Please select a valid start and end date.");
                errorLabel.setTextFill(Color.ORANGE);
                reportContainer.getChildren().add(errorLabel);
            }
        });

        controls.getChildren().addAll(
            new Label("Start Date:") {{ setTextFill(Color.WHITE); }},
            startDatePicker,
            new Label("End Date:") {{ setTextFill(Color.WHITE); }},
            endDatePicker,
            generateBtn
        );
        return controls;
    }

    // This reusable method builds the Income vs. Expense chart for any given period
    private static Node buildIncomeExpenseChartForPeriod(Account account, LocalDate startDate, LocalDate endDate) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10;");

        // Use the date-filtered methods from the Account class
        double income = account.getTotalIncome(startDate, endDate);
        double expense = account.getTotalExpenses(startDate, endDate);

        double max = Math.max(income, expense);
        double incomePercent = (max == 0) ? 0 : income / max;
        double expensePercent = (max == 0) ? 0 : expense / max;

        Label title = new Label("Summary for " + startDate + " to " + endDate);
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

    private static HBox buildButtonBar(Consumer<String> onNavigate) {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(0, 0, 10, 0));

        Button dashboardBtn = new Button("Dashboard");
        dashboardBtn.setOnAction(e -> onNavigate.accept("dashboard"));
        Button budgetBtn = new Button("Budget");
        budgetBtn.setOnAction(e -> onNavigate.accept("budget"));
        Button transactionBtn = new Button("Transactions");
        transactionBtn.setOnAction(e -> onNavigate.accept("transactions"));
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> onNavigate.accept("search"));
        Button recurringBtn = new Button("Recurring");
        recurringBtn.setOnAction(e -> onNavigate.accept("recurring"));
        
        Button reportsBtn = new Button("Reports");
        reportsBtn.setDisable(true); // Disable the current view's button

        buttons.getChildren().addAll(dashboardBtn, budgetBtn, transactionBtn, recurringBtn, searchBtn, reportsBtn);
        return buttons;
    }
}