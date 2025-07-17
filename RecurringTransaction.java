import java.util.Objects;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecurringTransaction {

    // Enum for recurrence frequency
    public enum Frequency {
        DAILY, WEEKLY, MONTHLY
    }

    // Recurring transaction properties
    private String description;
    private double amount;
    private LocalDate startDate;
    private Frequency frequency;
    private int maxOccurrences; // -1 for infinite
	private boolean isIncome;
	private Category category;
    private List<Transaction> transactionHistory;

    public RecurringTransaction(String description, Category category, double amount, LocalDate startDate,
                                Frequency frequency, int maxOccurrences, boolean isIncome) {
        this.description = description;
		this.category = category;
        this.amount = amount;
        this.startDate = startDate;
        this.frequency = frequency;
        this.maxOccurrences = maxOccurrences;
        this.isIncome = isIncome;
		this.transactionHistory = new ArrayList<>();
        //generateInitialTransaction();
    }

    // Generate initial transaction
    /*private void generateInitialTransaction() {
		//(Updated Jul 10)transactionHistory.add(new Transaction(description, amount, startDate));
		LocalDate date = startDate;
        for (int i = 0; i < maxOccurrences; i++) {
            transactionHistory.add(new Transaction(Category.OTHER, amount, date, false, description));
            date = getNextDate(date);
		}
        //====================================
    }
    */

    // Generate transactions up to a specific date
    public void generateTransactionsUntil(LocalDate endDate) {
        /*(Updated Jul 10) LocalDate currentDate = startDate;
          int occurrences = transactionHistory.size();
        
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            if (maxOccurrences != -1 && occurrences >= maxOccurrences) {
                break;
            }
         */
        LocalDate currentDate;
        if (transactionHistory.isEmpty()) {
            currentDate = startDate;
        } 
        else {
        	LocalDate lastDate = transactionHistory.get(transactionHistory.size() - 1).getDate();
            currentDate = getNextDate(lastDate);
        }
        
        while (!currentDate.isAfter(endDate)) {
        	if (maxOccurrences != -1 && transactionHistory.size() >= maxOccurrences) {
                break; 
            }

        	transactionHistory.add(new Transaction(this.category, this.amount, currentDate, this.isIncome, this.description));
        	currentDate = getNextDate(currentDate);
        	// occurrences++; 
       //=============================================================
        }
    }

    // Calculate next date based on frequency
    private LocalDate getNextDate(LocalDate currentDate) {
        switch (frequency) {
            case DAILY:
                return currentDate.plusDays(1);
            case WEEKLY:
                return currentDate.plusWeeks(1);
            case MONTHLY:
                return currentDate.plusMonths(1);
            default:
                throw new IllegalStateException("Unknown frequency");
        }
    }

    // Get transaction history
    public List<Transaction> getTransactionHistory() {
		//(Updated Jul 10)return new ArrayList<>(transactionHistory);
		return transactionHistory;
		//==============================
    }
    
    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public int getMaxOccurrences() {
        return maxOccurrences;
    }
	
	public boolean isIncome() { 
		return isIncome; 
	} 	
	
	public Category getCategory() { 
		return category; 
	} 

	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecurringTransaction that = (RecurringTransaction) o;
        return Double.compare(that.amount, amount) == 0 && 
			   maxOccurrences == that.maxOccurrences && 
			   isIncome == that.isIncome && 
			   Objects.equals(description, that.description) && 
			   Objects.equals(startDate, that.startDate) && 
			   frequency == that.frequency &&
			   category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, category, amount, startDate, frequency, maxOccurrences, isIncome);
    }
}

