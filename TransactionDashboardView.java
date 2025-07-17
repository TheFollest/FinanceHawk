import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class TransactionDashboardView {

    public static VBox create(Account account, Consumer<String> onNavigate, Runnable onBack) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        layout.getChildren().add(buildButtonBar(onNavigate,"transactions"));
		

        Label title = new Label("Transaction Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER_LEFT);

        Label amountLabel = new Label("Amount:");
        amountLabel.setTextFill(Color.WHITE);
        amountLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        TextField amountField = new TextField();

        Label categoryLabel = new Label("Category:");
        categoryLabel.setTextFill(Color.WHITE);
        categoryLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(Category.values());

        Label dateLabel = new Label("Date:");
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        DatePicker datePicker = new DatePicker(LocalDate.now());

        CheckBox incomeCheck = new CheckBox("Is Income?");
        incomeCheck.setTextFill(Color.WHITE);
        incomeCheck.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));

        Label noteLabel = new Label("Note:");
        noteLabel.setTextFill(Color.WHITE);
        noteLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        TextField noteField = new TextField();
        HBox noteRow = new HBox(10, noteLabel, noteField);
        noteRow.setVisible(false);
        noteRow.setManaged(false);
        

        // Show Note field only if "OTHER" category is selected
        categoryCombo.setOnAction(e -> {
            Category selected = categoryCombo.getValue();
            boolean showNote = selected == Category.OTHER;
            noteRow.setVisible(showNote);
            noteRow.setManaged(showNote);
        });

        Button addBtn = new Button("Add Transaction");
        Label feedback = new Label();
        feedback.setTextFill(Color.LIGHTGREEN);
        feedback.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));

        addBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                Category category = categoryCombo.getValue();
                LocalDate date = datePicker.getValue();
                boolean isIncome = incomeCheck.isSelected();
                String note = noteField.getText();

                if (category == null || date == null) {
                    feedback.setTextFill(Color.ORANGE);
                    feedback.setText("Please select category and date.");
                    return;
                }

                if (category == Category.OTHER && note.isEmpty()) {
                    feedback.setTextFill(Color.ORANGE);
                    feedback.setText("Please provide a note for 'Other' category.");
                    return;
                }

                Transaction txn = new Transaction(category, amount, date, isIncome, note);
                List<String> notifications = account.addTransaction(txn);
                feedback.setTextFill(Color.LIGHTGREEN);
                feedback.setText("Transaction added!");
				
				//Notification
				if (notifications != null && !notifications.isEmpty()) {
					for (String message : notifications) {
						Alert alert = new Alert(Alert.AlertType.WARNING);
						alert.setTitle("Budget Alert");
						alert.setHeaderText(null);
						alert.setContentText(message);
						alert.showAndWait();
					}
				}
				onNavigate.accept("transactions");
				

                amountField.clear();
                noteField.clear();
                incomeCheck.setSelected(false);
                categoryCombo.setValue(null);
                datePicker.setValue(LocalDate.now());
                noteRow.setVisible(false);
                noteRow.setManaged(false);
                onNavigate.accept("transactions");

            } catch (Exception ex) {
                feedback.setTextFill(Color.RED);
                feedback.setText("Invalid input.");
            }
        });

        form.getChildren().addAll(
                new HBox(10, amountLabel, amountField),
                new HBox(10, categoryLabel, categoryCombo),
                new HBox(10, dateLabel, datePicker),
                incomeCheck,
                noteRow,
                addBtn,
                feedback
        );

        Label recentLabel = new Label("Recent Transactions");
        recentLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        recentLabel.setTextFill(Color.WHITE);

        VBox listBox = new VBox(5);
        listBox.setPadding(new Insets(10));
        listBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10;");

        try {
            Field txnField = Account.class.getDeclaredField("transactions");
            txnField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Transaction> txns = (List<Transaction>) txnField.get(account);

            for (Transaction txn : txns) {
                Label entry = new Label(txn.toString());
                entry.setTextFill(txn.isIncome() ? Color.DARKGREEN : Color.DARKRED);
                entry.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
                Button deleteBtn = new Button("X");
                deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    account.deleteTransaction(txn);
                    onNavigate.accept("transactions"); // refresh
                });

                HBox row = new HBox(10, entry, deleteBtn);
                row.setAlignment(Pos.CENTER_LEFT);
                listBox.getChildren().add(row);
            }
        } catch (Exception e) {
            Label error = new Label("Failed to load transactions.");
            error.setTextFill(Color.RED);
            listBox.getChildren().add(error);
            e.printStackTrace();
        }

        ScrollPane scroll = new ScrollPane(listBox);
        scroll.setPrefHeight(600);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        layout.getChildren().addAll(title, form, recentLabel, scroll);
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
}
