import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.function.Consumer;

public class SearchDashboardView {

    public static VBox create(Account account, Consumer<String> onNavigate, Runnable onBack) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        layout.getChildren().addAll(
            buildButtonBar(onNavigate, "search"),
            buildSearchInstructions(),
            buildSearchArea(account)
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

        buttons.getChildren().addAll(dashboardBtn, budgetBtn, transactionBtn, searchBtn, recurringBtn);
        return buttons;
    }

    private static Label buildSearchInstructions() {
        Label instructions = new Label("Search your transactions by typing any of the following: category, description, or date (e.g. 'groceries', 'gift', '2025-06-09')");
        instructions.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 20));
        instructions.setTextFill(Color.LIGHTGRAY);
        instructions.setWrapText(true);
        instructions.setMaxWidth(800);
        return instructions;
    }

    private static VBox buildSearchArea(Account account) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search keyword...");
        searchField.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        searchField.setPrefWidth(1200);

        Button searchBtn = new Button("Search");
        HBox inputRow = new HBox(10, searchField, searchBtn);
        inputRow.setAlignment(Pos.CENTER);

        VBox resultsBox = new VBox(5);
        resultsBox.setAlignment(Pos.CENTER_LEFT);
        searchBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        searchBtn.setOnAction(e -> {
            String query = searchField.getText().trim();
            resultsBox.getChildren().clear();

            if (query.isEmpty()) {
                Label noInput = new Label("Please enter a keyword to search.");
                noInput.setTextFill(Color.ORANGE);
                resultsBox.getChildren().add(noInput);
                return;
            }

            Search search = new Search(account.getTransactions());
            List<Transaction> results = search.searchByDescription(query);
            

            if (results.isEmpty()) {
                Label none = new Label("No transactions found matching: '" + query + "'");
                none.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
                none.setTextFill(Color.SALMON);
                resultsBox.getChildren().add(none);
            } else {
                for (Transaction t : results) {
                    Label result = new Label(t.toString());
                    result.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 20));
                    result.setTextFill(t.isIncome() ? Color.LIGHTGREEN : Color.SALMON);
                    resultsBox.getChildren().add(result);
                }
            }
        });

        container.getChildren().addAll(inputRow, resultsBox);
        return container;
    }
}
