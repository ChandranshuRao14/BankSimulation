import java.util.Random;

public class Customer implements Runnable{
	
	private int num;
	private int visits;
	private int balance;
	private int currentTask;
	private int currentTaskAmount;
	private int loanAmount;
	private int currentEmployee;
	
	public Customer(int n, int v, int bal){
		this.num = n;
		this.visits = v;
		this.balance = bal;
		this.loanAmount = 0;
	}
	
	// Getter and Setter methods
	
	public int getNum(){
		return num;
	}
	
	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getVisits() {
		return visits;
	}

	public void setVisits(int visits) {
		this.visits = visits;
	}
	
	public int getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(int currentTask) {
		this.currentTask = currentTask;
	}

	public int getCurrentTaskAmount() {
		return currentTaskAmount;
	}

	public void setCurrentTaskAmount(int currentTaskAmount) {
		this.currentTaskAmount = currentTaskAmount;
	}

	public int getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(int loanAmount) {
		this.loanAmount = loanAmount;
	}

	public int getCurrentEmployee() {
		return currentEmployee;
	}

	public void setCurrentEmployee(int currentEmployee) {
		this.currentEmployee = currentEmployee;
	}

	@Override
	public void run() {
		// A Customer has been created
		System.out.println("Customer " + this.num + " created");
		while(this.visits != 3){
			// Customer is making one of his/her visits
			
			this.currentTask = getTask();
			this.currentTaskAmount = getTaskAmount();
	    	
	    	if(this.currentTask == 0 || this.currentTask == 1){
				try {
					// Customer waits in line
					BankSimulation.waitInTellerLine.acquire();
					BankSimulation.tellerLine.add(this);
					BankSimulation.waitInTellerLine.release();
					
					// Customer is ready for teller
					BankSimulation.readyForTeller.release();
					
					// Customer waits until they are assigned a teller
					BankSimulation.assigned[this.num].acquire();
					
					// Customer can now request a task
					Thread.sleep(100);
					if(this.currentTask == 0){
						System.out.println("Customer " + this.getNum() + " "
							+ "requests of teller " + this.currentEmployee + " to make a deposit of $" + this.getCurrentTaskAmount());
					}
					else{
						System.out.println("Customer " + this.getNum() + " "
							+ "requests of teller " + this.currentEmployee + " to make a withdrawal of $" + this.getCurrentTaskAmount());
					}
					BankSimulation.customerRequestTeller[this.currentEmployee].release(); // Signal the teller
					
					// Customer waits for teller to perform task
					BankSimulation.performTask[this.num].acquire();
					
					// Customer gets receipt
					Thread.sleep(100); // Sleep to simulate task time
					if(this.currentTask == 0){
						System.out.println("Customer " + this.num + " gets receipt from teller " + this.currentEmployee);
					}
					else{
						System.out.println("Customer " + this.num + " gets cash and receipt from teller " + this.currentEmployee);
					}
				} catch (InterruptedException e) {
					System.out.println("An error occurred.");
				}	
	    	}
	    	else if(this.currentTask == 2){
				try {
					// Customer waits in line
					BankSimulation.waitInOfficerLine.acquire();
					BankSimulation.officerLine.add(this);
					BankSimulation.waitInOfficerLine.release();
					
					// Customer is ready for officer
					BankSimulation.readyForOfficer.release();
					
					// Customer waits until they are assigned an officer
					BankSimulation.assigned[this.num].acquire();
					
					// Customer can now request a task
					Thread.sleep(100);
					System.out.println("Customer " + this.getNum() + " "
							+ "requests of teller " + this.currentEmployee + " to apply for a loan of $" + this.getCurrentTaskAmount());
					BankSimulation.customerRequestOfficer.release(); // Signal the officer
					
					// Customer waits for officer to perform task
					BankSimulation.performTask[this.num].acquire();
					
					// Customer gets receipt
					Thread.sleep(100); // Sleep to simulate task time
					System.out.println("Customer " + this.num + " gets loan from loan officer " + this.currentEmployee);
					
				} catch (InterruptedException e) {
					System.out.println("An error occurred.");
				}	
	    	}
	    	
			this.visits++; // Make another visit
		}
		// Customer has made his/her three visits it's time to dip
		System.out.println("Customer " + this.num + " departs the bank");
	}

	// Get a random task for the customer
	private int getTask() {
		Random randomNum = new Random();
    	int randomTask = randomNum.nextInt(3);
    	
		return randomTask;
	}
	
	// Get a random amount for the customer task
	private int getTaskAmount() {
		Random randomNum = new Random();
		int randomAmount = (randomNum.nextInt(5) + 1) * 100;
		
		return randomAmount;
	}
}
