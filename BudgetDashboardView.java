import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.lang.reflect.Field;
import java.time.LocalDate; // IMPORT ADDED
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

public class BudgetDashboardView {

    public static VBox create(Account account, Consumer<String> onNavigate, Runnable onBack) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        layout.getChildren().add(buildButtonBar(onNavigate, "budget"));

        Label title = new Label("Budget Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        Label totalLabel = new Label("Total Budget: $" + String.format("%.2f", getTotalBudget(account)));
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 20));
        totalLabel.setTextFill(Color.LIGHTGREEN);

        VBox budgetBoxContainer = new VBox();
        refreshBudgetBars(account, budgetBoxContainer, totalLabel);

        VBox inputForm = buildInputForm(account, () -> refreshBudgetBars(account, budgetBoxContainer, totalLabel));

        layout.getChildren().addAll(title, totalLabel, budgetBoxContainer, inputForm);
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

        buttons.getChildren().addAll(dashboardBtn, budgetBtn, transactionBtn, searchBtn, recurringBtn);
        return buttons;
    }


    private static void refreshBudgetBars(Account account, VBox container, Label totalLabel) {
        container.getChildren().clear();
        totalLabel.setText("Total Budget: $" + String.format("%.2f", getTotalBudget(account)));

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

                Button deleteBtn = new Button("X");
                deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                	budgets.remove(budget);
                    refreshBudgetBars(account, container, totalLabel);
                });

                HBox row = new HBox(10, label, deleteBtn);
                row.setAlignment(Pos.CENTER_LEFT);

                VBox entry = new VBox(row, bar);
                container.getChildren().add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static VBox buildInputForm(Account account, Runnable onRefresh) {
        VBox inputForm = new VBox(10);
        inputForm.setAlignment(Pos.CENTER_LEFT);
        inputForm.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Budget Name:");
        TextField nameField = new TextField();
        nameLabel.setTextFill(Color.WHITE);

        Label amountLabel = new Label("Limit Amount:");
        TextField amountField = new TextField();
        amountLabel.setTextFill(Color.WHITE);

        Label categoryLabel = new Label("Category:");
        categoryLabel.setTextFill(Color.WHITE);
        ComboBox<Category> categoryBox = new ComboBox<>();
        categoryBox.getItems().setAll(Category.values());
        
        // --- NEW Date Fields ---
        Label startLabel = new Label("Start Date:");
        startLabel.setTextFill(Color.WHITE);
        DatePicker startDatePicker = new DatePicker(LocalDate.now().withDayOfMonth(1));

        Label endLabel = new Label("End Date:");
        endLabel.setTextFill(Color.WHITE);
        DatePicker endDatePicker = new DatePicker(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(amountLabel, 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(categoryLabel, 0, 2);
        grid.add(categoryBox, 1, 2);
        //New fields
        grid.add(startLabel, 0, 3);    
        grid.add(startDatePicker, 1, 3); 
        grid.add(endLabel, 0, 4);      
        grid.add(endDatePicker, 1, 4);   

        Button submit = new Button("Add Budget");
        submit.setOnAction(e -> {
            String name = nameField.getText();
            String amountText = amountField.getText();
            Category category = categoryBox.getValue();
            LocalDate startDate = startDatePicker.getValue(); 
            LocalDate endDate = endDatePicker.getValue();     

            if (!name.isEmpty() && amountText.matches("\\d+(\\.\\d+)?") && category != null) {
                double limit = Double.parseDouble(amountText);
                Budget newBudget = new Budget(name, limit, category, startDate, endDate);
                account.addBudget(newBudget);
                onRefresh.run();
                // Clear fields
                nameField.clear();
                amountField.clear();
                categoryBox.setValue(null);
            }
        });

        inputForm.getChildren().addAll(new Separator(), grid, submit);
        return inputForm;
    }

    private static double getTotalBudget(Account account) {
        double total = 0.0;
        try {
            Field field = Account.class.getDeclaredField("budgets");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Budget> budgets = (List<Budget>) field.get(account);
            for (Budget b : budgets) {
                Field limitField = Budget.class.getDeclaredField("limit");
                limitField.setAccessible(true);
                total += limitField.getDouble(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
}
