import javafx.geometry.Insets;
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

    public static VBox create(Account account, Consumer<String> onNavigate, Runnable onRefresh) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        Label title = new Label("Budget Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        Label totalLabel = new Label("Total Budget: $" + String.format("%.2f", getTotalBudget(account)));
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 20));
        totalLabel.setTextFill(Color.LIGHTGREEN);

        VBox budgetBars = buildBudgetBars(account);

        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> onNavigate.accept("main"));

        VBox inputForm = buildInputForm(account, onRefresh);

        layout.getChildren().addAll(title, totalLabel, budgetBars, inputForm, backButton);
        return layout;
    }

    private static VBox buildBudgetBars(Account account) {
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
                budgetBox.getChildren().add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return budgetBox;
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
        ComboBox<Category> categoryBox = new ComboBox<>();
        categoryBox.getItems().setAll(Category.values());

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(amountLabel, 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(categoryLabel, 0, 2);
        grid.add(categoryBox, 1, 2);

        Button submit = new Button("Add Budget");
        submit.setOnAction(e -> {
            String name = nameField.getText();
            String amountText = amountField.getText();
            Category category = categoryBox.getValue();

            if (!name.isEmpty() && amountText.matches("\\d+(\\.\\d+)?") && category != null) {
                double limit = Double.parseDouble(amountText);
                Budget newBudget = new Budget(name, limit, category);
                account.addBudget(newBudget);
                onRefresh.run();
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
