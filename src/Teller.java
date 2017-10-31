
public class Teller implements Runnable {
	
	private int tellerNum;
	
	public Teller(int i) {
		this.tellerNum = i;
	}

	// Getter and Setter methods 
	
	public int getTellerNum() {
		return tellerNum;
	}

	public void setTellerNum(int tellerNum) {
		this.tellerNum = tellerNum;
	}

	@Override
	public void run() {
		System.out.println("Teller " + this.tellerNum + " created");
		while(true){
			try {
				// Wait for the customer to be ready
				BankSimulation.readyForTeller.acquire();
				
				// Pull customer from line 
				BankSimulation.waitInTellerLine.acquire();
				Customer current = BankSimulation.tellerLine.poll();
				System.out.println("Teller " + tellerNum + " begins serving customer " + current.getNum());
				BankSimulation.waitInTellerLine.release();
				current.setCurrentEmployee(this.tellerNum);
				
				// Signal to customer that we are ready for a request
				BankSimulation.assigned[current.getNum()].release();
				
				// Wait for the customer request
				BankSimulation.customerRequestTeller[this.tellerNum].acquire();
				
				// Now we can perform task
				if(current.getCurrentTask() == 0){
					// This is a deposit
					processDeposit(current);
				}
				else{
					// This is a withdrawal
					processWithdrawal(current);
				}
				
			} catch (InterruptedException e) {
				System.out.println("An error occurred. The system will now exit.");
			}
		}
	}

	private void processDeposit(Customer curr) throws InterruptedException {
		// Process customer deposit
		int customerNum = curr.getNum();
		int balance = curr.getBalance();
		int newBalance = balance + curr.getCurrentTaskAmount();
		curr.setBalance(newBalance);
		
		Thread.sleep(400); // Sleep to simulate task time
		
		System.out.println("Teller " + this.tellerNum + " processes "
				+ "deposit of $" + curr.getCurrentTaskAmount() + " for customer " + customerNum);
		BankSimulation.performTask[customerNum].release(); // Signal this customer that task is done
	}

	private void processWithdrawal(Customer curr) throws InterruptedException {
		// Process customer withdrawal
		int customerNum = curr.getNum();
		int balance = curr.getBalance();
		int newBalance = balance - curr.getCurrentTaskAmount();
		curr.setBalance(newBalance);
		
		Thread.sleep(400); // Sleep to simulate task time
		
		System.out.println("Teller " + this.tellerNum + " processes "
				+ "withdrawal of $" + curr.getCurrentTaskAmount() + " for customer " + customerNum);
		BankSimulation.performTask[customerNum].release(); // Signal this customer that task is done
	}
}
