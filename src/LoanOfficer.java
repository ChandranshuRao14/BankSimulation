
public class LoanOfficer implements Runnable {
	
	private int officerNum;
	
	public LoanOfficer(int i) {
		this.officerNum = i;
	}

	// Getter and Setter methods
	
	public int getOfficerNum() {
		return officerNum;
	}

	public void setOfficerNum(int officerNum) {
		this.officerNum = officerNum;
	}

	@Override
	public void run() {
		System.out.println("Loan Officer " + this.officerNum + " created");
		while(true){
			try {
				// Wait for the customer to be ready
				BankSimulation.readyForOfficer.acquire();
				
				// Pull customer from line 
				BankSimulation.waitInOfficerLine.acquire();
				Customer current = BankSimulation.officerLine.poll();
				System.out.println("Loan Officer " + this.officerNum + " begins serving customer " + current.getNum());
				BankSimulation.waitInOfficerLine.release();
				current.setCurrentEmployee(this.officerNum);
				
				// Signal to customer that we are ready for a request
				BankSimulation.assigned[current.getNum()].release();
				
				// Wait for the customer request
				BankSimulation.customerRequestOfficer.acquire();
				
				processLoan(current);
				
			} catch (InterruptedException e) {
			}
		}
	}

	private void processLoan(Customer curr) throws InterruptedException {
		// Process customer loan
		// Set customer balance
		int customerNum = curr.getNum();
		int balance = curr.getBalance();
		int newBalance = balance + curr.getCurrentTaskAmount();
		curr.setBalance(newBalance);
		
		// Set customer loan balance
		int loan = curr.getLoanAmount();
		int newLoan = loan + curr.getCurrentTaskAmount();
		curr.setLoanAmount(newLoan);
		
		Thread.sleep(400); // Sleep to simulate task time
		
		System.out.println("Loan Officer " + this.officerNum + " approves "
				+ "loan of $" + curr.getCurrentTaskAmount() + " for customer " + customerNum);
		BankSimulation.performTask[customerNum].release(); // Signal this customer that task is done
	}

}
