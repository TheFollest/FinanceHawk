import java.time.LocalDate;


public class Transaction {
    private Category category;
    private double amount;
    private LocalDate date;
    private boolean isIncome;
    private String description;

    public Transaction(Category category, double amount, LocalDate date, boolean isIncome, String description) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;

        if (category == Category.OTHER) {
            this.description = description; // allow custom description
        } else {
            this.description = category.toString(); // auto-generate based on category
        }
    }

    public Transaction(String description, double amount, LocalDate date) {
        this.category = Category.OTHER;
        this.amount = amount;
        this.date = date;
        this.isIncome = false;
        this.description = description;
    }

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

    public String getDescription() {
        return description;
    }

        //Make println(Transaction) work
        @Override
        public String toString() {
        	String type;
        	if (isIncome) 
        	  type = "Income";
        	else 
        	  type = "Expense";
            return type + ": " + description + " | $" + amount + " | " + date;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Transaction that = (Transaction) o;
            return Double.compare(that.amount, amount) == 0 &&
                   isIncome == that.isIncome &&
                   category == that.category &&
                   date.equals(that.date) &&
                   description.equals(that.description);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(category, amount, date, isIncome, description);
        }
    }