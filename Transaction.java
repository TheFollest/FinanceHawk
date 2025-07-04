import java.time.LocalDate;


public class Transaction {
        private Category category;
        private double amount;
        private LocalDate date;
        private boolean isIncome;
        
        public Transaction(Category category, double amount, LocalDate date, boolean isIncome) {
            this.category = category;
            this.amount = amount;
            this.date = date;
            this.isIncome = isIncome;
        }

        //return attributes for external uses
        public Category getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }

        public LocalDate getDate() {
            return date;
        }

        public boolean isIncome() {
            return isIncome;
        }

        //Make println(Transaction) work
        @Override
        public String toString() {
        	String type;
        	if (isIncome) 
        	  type = "Income";
        	else 
        	  type = "Expense";
            return type + ": " + category + " - $" + amount + " on " + date;
        }
    }