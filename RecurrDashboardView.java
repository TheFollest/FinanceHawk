import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class RecurrDashboardView {

    public static VBox create(Account account, List<RecurringTransaction> definitions, Consumer<String> onNavigate, Runnable onRefresh) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        // UI Components
        Label title = new Label("Recurring Transactions");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        VBox ruleListView = buildRuleListView(definitions, onRefresh);
        VBox inputForm = buildInputForm(definitions, onRefresh);
		
		Button syncButton = new Button("Sync");
        syncButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        syncButton.setOnAction(e -> {
            RecurrSync.syncRecurringTransactions(account, definitions);
            onNavigate.accept("dashboard"); // Go to dashboard to see updated balance
        });
		
		HBox manageHeader = new HBox(
            new Label("Manage Recurring Rules") {{ setTextFill(Color.LIGHTGRAY); }},
            new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }}, // Spacer
            syncButton
        );
        manageHeader.setAlignment(Pos.CENTER_LEFT);

        layout.getChildren().addAll(
                buildButtonBar(onNavigate, "recurring"),
                title,
                manageHeader,
                ruleListView,
                new Separator(),
                new Label("Add New Rule") {{ setFont(Font.font("Segoe UI", FontWeight.BOLD, 18)); setTextFill(Color.WHITE); }},
                inputForm
        );
        return layout;
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
		
		Button reportsBtn = new Button("Reports");
		reportsBtn.setOnAction(e -> onNavigate.accept("reports"));
		reportsBtn.setDisable("reports".equals(currentPage));

        buttons.getChildren().addAll(dashboardBtn, budgetBtn, transactionBtn, recurringBtn, searchBtn, reportsBtn);
        return buttons;
    }

    // Displays the list of existing recurring rules
    private static VBox buildRuleListView(List<RecurringTransaction> definitions, Runnable onRefresh) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 10;");

        if (definitions.isEmpty()) {
            container.getChildren().add(new Label("No recurring rules defined.") {{ setTextFill(Color.WHITE); }});
        } else {
            for (RecurringTransaction rule : definitions) {
                String details = String.format("%s - $%.2f starting %s (%s)",
                        rule.getDescription(), rule.getAmount(), rule.getStartDate(), rule.getFrequency());
                Label ruleLabel = new Label(details);
                ruleLabel.setTextFill(Color.WHITE);

                Button deleteBtn = new Button("X");
                deleteBtn.setStyle("-fx-background-color: #E53935; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    definitions.remove(rule);
                    onRefresh.run(); // Refresh the entire view
                });

                HBox row = new HBox(10, ruleLabel, deleteBtn);
                row.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(ruleLabel, Priority.ALWAYS); // Make label take available space
                container.getChildren().add(row);
            }
        }
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return new VBox(scrollPane);
    }

    // Provides the form to add a new rule
    private static VBox buildInputForm(List<RecurringTransaction> definitions, Runnable onRefresh) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField descField = new TextField();
        descField.setPromptText("e.g., Monthly Rent");
		ComboBox<Category> categoryBox = new ComboBox<>();
		categoryBox.getItems().setAll(Category.values());
        TextField amountField = new TextField();
        amountField.setPromptText("e.g., 1200.00");
        DatePicker datePicker = new DatePicker(LocalDate.now());
		
        ComboBox<RecurringTransaction.Frequency> freqBox = new ComboBox<>();
        freqBox.getItems().setAll(RecurringTransaction.Frequency.values());
        freqBox.setValue(RecurringTransaction.Frequency.MONTHLY); // Default value
		
		CheckBox incomeCheck = new CheckBox("Is Income?");
		incomeCheck.setTextFill(Color.WHITE);

        grid.add(new Label("Description:") {{ setTextFill(Color.WHITE); }}, 0, 0); // Row 0
		grid.add(descField, 1, 0);

		grid.add(new Label("Category:") {{ setTextFill(Color.WHITE); }}, 0, 1);    // Row 1
		grid.add(categoryBox, 1, 1);

		grid.add(new Label("Amount:") {{ setTextFill(Color.WHITE); }}, 0, 2);      // Row 2
		grid.add(amountField, 1, 2);

		grid.add(new Label("Start Date:") {{ setTextFill(Color.WHITE); }}, 0, 3);  // Row 3
		grid.add(datePicker, 1, 3);

		grid.add(new Label("Frequency:") {{ setTextFill(Color.WHITE); }}, 0, 4); // Row 4
		grid.add(freqBox, 1, 4);

		grid.add(incomeCheck, 1, 5);        
		
		

        Button addButton = new Button("Add New Rule");
        addButton.setOnAction(e -> {
            try {
                String desc = descField.getText();
				Category category = categoryBox.getValue();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = datePicker.getValue();
                RecurringTransaction.Frequency freq = freqBox.getValue();
				
				boolean isIncome = incomeCheck.isSelected();

                if (!desc.isEmpty() && date != null && freq != null) {
                    definitions.add(new RecurringTransaction(desc, category, amount, date, freq, -1, isIncome));
                    onRefresh.run(); // Refresh the view to show the new rule
                }
            } catch (NumberFormatException ex) {
                // Handle invalid amount input if necessary
                System.out.println("Invalid amount entered.");
            }
        });

        VBox formContainer = new VBox(15, grid, addButton);
        formContainer.setAlignment(Pos.CENTER_LEFT);
        return formContainer;
    }
}