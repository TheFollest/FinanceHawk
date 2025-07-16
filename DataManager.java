import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataManager {
	
	//saves EVERY transaction in a list
	public static void saveTransactions(List<Transaction> t, File tFile) throws IOException
	{
		FileWriter fw = new FileWriter(tFile, true);
		PrintWriter pw = new PrintWriter(fw);
		for (int i = 0; i < t.size(); i++)
		{
			pw.println(t.get(i));
		}
		pw.close();
	}
	
	//saves EVERY budget in a list
	public static void saveBudget(List<Budget> t, File tFile) throws IOException
	{
		FileWriter fw = new FileWriter(tFile, true);
		PrintWriter pw = new PrintWriter(fw);
		for (int i = 0; i < t.size(); i++)
		{
			pw.println(t.get(i));
		}
		pw.close();
	}

	//adds one transaction to the transaction file
	public static void addTrans(Transaction t, File tFile)throws IOException
	{
		FileWriter fw = new FileWriter(tFile, true);
		PrintWriter pw = new PrintWriter(fw);
		pw.println(t);
		pw.close();
	}
	
	//adds one budget to the budget file
	public static void addBudg(Budget t, File tFile)throws IOException
	{
		FileWriter fw = new FileWriter(tFile);
		PrintWriter pw = new PrintWriter(fw);
		pw.println(t);
		pw.close();
	}
	
	//loads all transactions currently saved into the txt file into a list
	public static List<Transaction> loadTransactions(File f) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String l = reader.readLine();
		List<Transaction> t = new ArrayList<>();
		Scanner scan = new Scanner(f);
		String[] x = scan.nextLine().split(" ");
		boolean income = true;
		String des = "";
		double y = -1;
		Category z = null;
		
		while (l !=	null)
		{
			if (String.valueOf(x[0].charAt(0)).equals("I"))
			{
				income = true;
			}
			else
			{
				income = false;
			}
			for (Category i : Category.values())
			{
				try 
				{
					if (i == (Category.valueOf(x[1])))
					{
						z = Category.valueOf(x[1]);
						break;
					}
				}
				catch (Exception IllegalArgumentException)
				{
					z = Category.OTHER;
					for (int j = 1; j < x.length -4; j++)
					{
						des = des + x[j] + " ";
					}
					break;
				}
			}
			String[] date = x[x.length-1].split("-");
			y = Double.parseDouble(x[x.length-3].substring(1));
			if (scan.hasNextLine())
			{
				x = scan.nextLine().split(" ");
			}
			l = reader.readLine();
			Transaction t1 = new Transaction(z, y, LocalDate.of(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2])), income, des);
			t.add(t1);
			des = "";
		}
		scan.close();
		reader.close();
		return t;
	}
	
	//Loads all budgets currently saved into a text file into the list
	public static List<Budget> loadBudgets(File f) throws IOException
	{
		List<Budget> b = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String l = reader.readLine();
		Scanner scan = new Scanner(f);
		String[] x = scan.nextLine().split(" ");
		String des = "";
		double y = -1;
		Category z = null;
		while (l != null)
		{
			for (Category i : Category.values())
			{
					if (i == (Category.valueOf(x[x.length-4].substring(1, x[x.length-4].length()-2))))
					{
						z = i;
						break;
					}
			}
			for (int i = 0; i < x.length-4; i++)
			{
				des = des + x[i] + " ";
			}
			y = Double.parseDouble(x[x.length-1].substring(1));
			Budget b1 = new Budget(des, y, z);
			b.add(b1);
			if (scan.hasNextLine())
			{
				x = scan.nextLine().split(" ");
			}
			
			des = "";
			l = reader.readLine();

		}
		scan.close();
		reader.close();
		return b;
		
	}
	
	//clears the text in a file
	public static void clearFile(File f) throws FileNotFoundException
	{
		PrintWriter writer = new PrintWriter(f);
		writer.print("");
		writer.close();
	}
	
}