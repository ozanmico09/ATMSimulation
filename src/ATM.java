import java.math.BigDecimal;
import java.util.*;

public class ATM {

    private final Bank bank = Bank.getInstance();
    private final Scanner scanner = new Scanner(System.in);
    private Customer currentCustomer;

    private static final BigDecimal[] CASH_DENOMINATIONS = {
            BigDecimal.valueOf(200),
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(50),
            BigDecimal.valueOf(20)
    };

    private BigDecimal atmHardwareCashPool = new BigDecimal("50000");

    public void validateCashDispenser(BigDecimal amount) throws DispenserException{

        if (amount.compareTo(atmHardwareCashPool) > 0){
            throw new DispenserException("ATM temporarily out of cash. Maximum available: " + atmHardwareCashPool + " TL");
        }

        BigDecimal remaining = amount;
        for (BigDecimal denomination : CASH_DENOMINATIONS){
            BigDecimal[] result = remaining.divideAndRemainder(denomination);
            remaining = result[1];
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0){
            throw new DispenserException("The amount must be a multiple of 20, 50, 100 or 200 TL.");
        }
    }

    public void start(){
        System.out.println("====================================");
        System.out.println("      WELCOME TO ATM SIMULATION     ");
        System.out.println("====================================");

        while (true){
            try{
                if (!login()) continue;
                mainMenu();
            } catch (Exception e){
                System.out.println("\n[SYSTEM ERROR] An unexpected error occurred: " + e.getMessage());
                System.out.println("Please take your card and try again later.");
            } finally {
                logout();
            }
        }
    }

    private void logout(){
        this.currentCustomer = null;
        System.out.println("\n[SECURE] Card returned. Session cleared.");
        System.out.println("====================================\n");
    }

    private boolean login() {
        try {
            System.out.println("--- SECURE LOGIN ---");
            System.out.print("Card Number (or 'Q' to exit): ");
            String cardNo = scanner.nextLine().trim();

            if (cardNo.equalsIgnoreCase("Q")) {
                System.out.println("Operation canceled. Have a nice day!");
                return false;
            }

            System.out.print("PIN: ");
            String pin = scanner.nextLine().trim();

            currentCustomer = bank.authenticate(cardNo, pin);
            System.out.println("Login successful. Welcome, " + currentCustomer.getName() + "!");
            return true;
        } catch (InvalidPinException | CardBlockedException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    private void mainMenu(){
        boolean logout = false;
        while (!logout){
            System.out.println("\n----- MAIN MENU -----");
            System.out.println("1. Balance Inquiry");
            System.out.println("2. Cash Deposit");
            System.out.println("3. Cash Withdrawal");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Change PIN");
            System.out.println("7. Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": balanceInquiry(); break;
                    case "2": deposit(); break;
                    case "3": withdraw(); break;
                    case "4": transfer(); break;
                    case "5": printHistory(); break;
                    case "6": changePin(); break;
                    case "7": logout = true; System.out.println("Logging out..."); break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Transaction error: " + e.getMessage());
            } catch (Exception e){
                System.out.println("\nSystem Error: " + e.getMessage());
            }

        }
    }

    private Account selectAccount() throws AccountNotFoundException{

        List<Account> accs = new ArrayList<>(currentCustomer.getAccounts().values());

        if (accs.isEmpty()) {
            throw new AccountNotFoundException("You do not have any registered accounts.");
        }

        System.out.println("Your Accounts:");

        for (int i = 0; i < accs.size(); i++) {
            Account a = accs.get(i);
            System.out.printf("%d. %s (%s) - Balance: %.2f TL%n",
                    i + 1, a.getAccountNumber(), a.getType(), a.getBalance());
        }

        System.out.print("Select account: ");

        try{
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (idx < 0 || idx >= accs.size()) {
                throw new IllegalArgumentException("Invalid account selection. Out of bounds.");
            }

            return accs.get(idx);

        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Invalid input. Please enter a valid menu number.");
        }


    }

    private void balanceInquiry() throws AccountNotFoundException{
        Account acc = selectAccount();
        System.out.printf("Available Balance: %.2f TL%n", acc.getBalance().doubleValue());
        acc.addTransaction(new Transaction(TransactionType.BALANCE_INQUIRY, BigDecimal.ZERO,
                acc.getBalance(), "Balance inquiry"));

    }

    private void deposit() throws AccountNotFoundException, InvalidAmountException {
        Account acc = selectAccount();

        System.out.print("Deposit amount: ");
        String input = scanner.nextLine().trim();

        try{
            BigDecimal amount = new BigDecimal(input);
            acc.deposit(amount);
            System.out.printf("✅ Deposit successful. New balance: %.2f TL%n", acc.getBalance().doubleValue());
        } catch (NumberFormatException e){
            System.out.println("❌ Transaction Denied: Invalid amount format. Please enter numbers only.");
        } catch (InvalidAmountException e){
            System.out.println("❌ Transaction Denied: " + e.getMessage());
        }
    }

    private void withdraw() throws AccountNotFoundException, InsufficientFundsException,
            InvalidAmountException, DailyLimitExceededException {

        Account acc = selectAccount();

        System.out.print("Withdrawal amount: ");
        String input = scanner.nextLine().trim();

        try{
            BigDecimal amount = new BigDecimal(input);
            validateCashDispenser(amount);
            acc.withdraw(amount);
            dispenseCash(amount);
            System.out.printf("✅ Withdrawal successful. New balance: %.2f TL%n", acc.getBalance().doubleValue());
        } catch (NumberFormatException e){
            System.out.println("❌ Transaction Denied: Invalid amount format. Please enter numbers only.");
        } catch (DispenserException e){
            System.out.println("❌ Hardware Denied: " + e.getMessage());
        } catch (InsufficientFundsException | InvalidAmountException | DailyLimitExceededException e){
            System.out.println("❌ Transaction Denied: " + e.getMessage());
        }
    }

    private void dispenseCash(BigDecimal amount) {

        System.out.println("Preparing banknotes:");

        Map<BigDecimal, Integer> notes = new LinkedHashMap<>();
        BigDecimal remaining = amount;

        for (BigDecimal denom : CASH_DENOMINATIONS) {
            BigDecimal[] result = remaining.divideAndRemainder(denom);
            int count = result[0].intValue();

            if (count > 0) {
                notes.put(denom, count);
                remaining = result[1];
            }
        }

        notes.forEach((denom, count) -> System.out.printf("  %s TL x %d pcs%n", denom, count));

        this.atmHardwareCashPool = this.atmHardwareCashPool.subtract(amount);
        System.out.println("⚠️ Please take your cash from the dispenser slot.");
    }

    private void transfer() throws AccountNotFoundException{

        System.out.println("Select source account:");
        Account from = selectAccount();

        System.out.print("Recipient account number: ");
        String toAccNo = scanner.nextLine().trim();

        try{
            Account to = bank.findAccountGlobally(toAccNo);

            System.out.print("👉 Enter transfer amount (TL): ");
            String amountInput = scanner.nextLine().trim();

            BigDecimal amount = new BigDecimal(amountInput);
            bank.transfer(from, to, amount);
            System.out.println("✅ Transfer successful! Money has been sent.");
        } catch (NumberFormatException e){
            System.out.println("❌ Transaction Denied: Invalid amount format. Please enter numbers only.");
        } catch (AccountNotFoundException e){
            System.out.println("❌ Transaction Denied: Recipient account not found.");
        } catch (InsufficientFundsException | InvalidAmountException | DailyLimitExceededException e){
            System.out.println("❌ Transaction Denied: " + e.getMessage());
        }
    }

    private void printHistory() throws AccountNotFoundException{
        Account acc = selectAccount();

        System.out.println("----- Transaction History -----");

        List<Transaction> history = acc.getHistory();

        if (history.isEmpty()){
            System.out.println("No transactions yet.");
            return;
        }

        int displayLimit = 5;
        int counter = 0;

        for (int i = history.size() - 1; i >= 0; i--) {
            if (counter >= displayLimit) {
                System.out.printf("👉 ... and %d more transaction(s) available.%n", history.size() - displayLimit);
                break;
            }

            System.out.println(history.get(i));
            counter++;
        }
    }

    private void changePin() {
        try {

            System.out.println("\n--- 🔒 Change PIN ---");
            System.out.print("Old PIN: ");
            String oldPin = scanner.nextLine().trim();

            System.out.print("New PIN: ");
            String newPin = scanner.nextLine().trim();

            currentCustomer.getCard().changePin(oldPin, newPin);
            System.out.println("PIN changed successfully.");
        } catch (InvalidPinException | CardBlockedException e) {
            System.out.println("❌ Security Denied: " + e.getMessage());
        } catch (IllegalArgumentException e){
            System.out.println("❌ Invalid Input: " + e.getMessage());
        }
    }
}
