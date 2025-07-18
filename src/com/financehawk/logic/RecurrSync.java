package com.financehawk.logic;
import java.time.LocalDate;
import java.util.List;
import com.financehawk.model.Account;
import com.financehawk.model.RecurringTransaction;
import com.financehawk.model.Transaction;

public class RecurrSync {
	
	//Add recurring transaction from Recurring list
    public static void syncRecurringTransactions(Account account, List<RecurringTransaction> RecurrList) {
        LocalDate today = LocalDate.now();
        for (RecurringTransaction Recurr : RecurrList) {
            
            Recurr.generateTransactionsUntil(today); 
            for (Transaction newTx : Recurr.getTransactionHistory()) { 
                if (!CheckDuplicate(account, newTx)) 
                    account.addTransaction(newTx);              
            }
        }
    }
    
    //Check duplicate transactions
    private static boolean CheckDuplicate (Account account, Transaction t) {
        for (Transaction oldT : account.getTransactions()) {  
            if (oldT.getDate().equals(t.getDate()) &&
                oldT.getAmount() == t.getAmount() &&
                oldT.getDescription().equals(t.getDescription())) { 
                return true; // Found a duplicate
            }
        }
        return false; // No duplicate found
    }
}