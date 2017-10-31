import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BankSimulation {
	
	// Create all semaphores
	// Semaphores used to protect Queue data structure
	public static Semaphore waitInTellerLine = new Semaphore(1, true);
	public static Semaphore waitInOfficerLine = new Semaphore(1, true);
	
	// Semaphores used to signal employee that customer is ready
	public static Semaphore readyForTeller = new Semaphore(0,true);
	public static Semaphore readyForOfficer = new Semaphore(0,true);
	
	// Semaphore array for each customer; signal customer that task has been performed
	public static Semaphore[] performTask = {new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true)};
	
	// Semaphore array for each customer; signal customer that employee has been assigned to them
	public static Semaphore[] assigned = {new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true)};
	
	// Semaphore array for each teller; signal teller that customer has made a request
	public static Semaphore[] customerRequestTeller = {new Semaphore(0, true), new Semaphore(0, true)};
	
	// Semaphore to signal officer that customer has made a request
	public static Semaphore customerRequestOfficer = new Semaphore(0, true);
	
	// Use queues to represent the line for the teller and the line for the loan officer
	public static Queue<Customer> tellerLine = new LinkedList<Customer>();
	public static Queue<Customer> officerLine = new LinkedList<Customer>();
	
	// Program constants
	public final static int NUM_OF_CUSTOMERS = 5;
	public final static int NUM_OF_TELLERS = 2;
	
	public static void main(String[] args) {
		
		System.out.println("Starting bank simulation...");

		// Create 2 Bank Tellers w/ 2 threads
		Thread[] tellers = new Thread[NUM_OF_TELLERS];
		for(int i=0; i < NUM_OF_TELLERS; i++){
			tellers[i] = new Thread(new Teller(i));
			tellers[i].start();
		}
		
		// Create 1 Loan Officer w/ 1 thread
		Thread loanOfficer = new Thread(new LoanOfficer(0));
		loanOfficer.start();
		
		// Create 5 Customers w/ 5 threads
		Thread[] customers = new Thread[NUM_OF_CUSTOMERS];
		Customer[] customerValues = new Customer[NUM_OF_CUSTOMERS];
		for(int i=0; i < NUM_OF_CUSTOMERS; i++){
			Customer newCustomer = new Customer(i,0,1000);
			customerValues[i] = newCustomer;
			customers[i] = new Thread(newCustomer);
			customers[i].start();
		}
		
		// Join all customer threads to main
		for(int i=0; i < NUM_OF_CUSTOMERS; i++){
			try {
				customers[i].join();
			} catch (InterruptedException e) {
				System.out.println("Could not join thread.");
			}
			System.out.println("Customer " + i + " is joined by main");
			
		}
		
		// Print output summary
		System.out.println("\n\t\tBanking Simulation Summary");
		System.out.println("\tEnding Balance\tLoan Amount");
		int balanceTotal = 0;
		int loanTotal = 0;
		for(int i=0; i< NUM_OF_CUSTOMERS; i++){
			balanceTotal += customerValues[i].getBalance();
			loanTotal += customerValues[i].getLoanAmount();
			System.out.println("Customer " + i + "\t" + customerValues[i].getBalance()
					+ "\t" + customerValues[i].getLoanAmount());
		}
		System.out.println("\nTotals \t" + balanceTotal + "\t" + loanTotal);
		System.out.println("\nEnd of simulation!");
		System.exit(0);
	}

}
