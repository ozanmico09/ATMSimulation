import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Bank bank = Bank.getInstance();

        Card card1 = new Card("1111222233334444", "1234");
        Customer david = new Customer("David Bowie", "C001", card1);
        Account davidChecking = new CheckingAccount("TR001", new BigDecimal("1000"));
        Account davidSavings = new SavingsAccount("TR002", new BigDecimal("5000"));
        david.addAccount(davidChecking);
        david.addAccount(davidSavings);
        bank.registerCustomer(david);

        Card card2 = new Card("5555666677778888", "4321");
        Customer robert = new Customer("Robert De Niro", "C002", card2);
        Account robertChecking = new CheckingAccount("TR003", new BigDecimal("2500"));
        robert.addAccount(robertChecking);
        bank.registerCustomer(robert);

        new ATM().start();

        System.out.println("\n📥 [OPERATION] CASH DEPOSIT TRANSACTIONS");
        try {
            System.out.printf("David's Checking Initial Balance: %.2f TL%n", davidChecking.getBalance().doubleValue());

            BigDecimal depositAmount = new BigDecimal("500");
            System.out.printf("[Step 1] Depositing %.2f TL cash into David's checking account...%n", depositAmount.doubleValue());
            davidChecking.deposit(depositAmount);
            System.out.printf("  ✅ Success! New Balance: %.2f TL%n", davidChecking.getBalance().doubleValue());

            BigDecimal invalidDeposit = new BigDecimal("-100");
            System.out.printf("[Step 2] Validation Test: Attempting to deposit an invalid amount (%.2f TL)...%n", invalidDeposit.doubleValue());
            davidChecking.deposit(invalidDeposit);
        } catch (Exception e) {
            System.out.println("  ❌ Expected Validation Error Caught: " + e.getMessage());
        }

        System.out.println("\n📤 [OPERATION] CASH WITHDRAWAL TRANSACTIONS");
        try {
            System.out.printf("Robert's Checking Initial Balance: %.2f TL%n", robertChecking.getBalance().doubleValue());

            BigDecimal withdrawAmount1 = new BigDecimal("2000");
            System.out.printf("[Step 1] Robert is withdrawing %.2f TL cash...%n", withdrawAmount1.doubleValue());
            robertChecking.withdraw(withdrawAmount1);
            System.out.printf("  ✅ Success! Remaining Balance: %.2f TL%n", robertChecking.getBalance().doubleValue());

            BigDecimal withdrawAmount2 = new BigDecimal("1200");
            System.out.printf("[Step 2] Overdraft Flex Test: Tries to withdraw %.2f TL (Triggers Overdraft/KMH)...%n", withdrawAmount2.doubleValue());
            robertChecking.withdraw(withdrawAmount2);
            System.out.printf("  ✅ Success (Overdraft Used)! Remaining Balance: %.2f TL%n", robertChecking.getBalance().doubleValue());

            BigDecimal invalidWithdraw = new BigDecimal("6000");
            System.out.printf("[Step 3] Validation Test: Attempting to withdraw %.2f TL from Savings Account...%n", invalidWithdraw.doubleValue());
            davidSavings.withdraw(invalidWithdraw);
        } catch (Exception e) {
            System.out.println("  ❌ Expected Insufficient Funds Error Caught: " + e.getMessage());
        }

        System.out.println("\n💸 [OPERATION] INTER-ACCOUNT FUNDS TRANSFER");
        try {
            System.out.printf("David's Checking Current Balance: %.2f TL%n", davidChecking.getBalance().doubleValue());
            System.out.printf("Robert's Checking Current Balance: %.2f TL%n", robertChecking.getBalance().doubleValue());

            BigDecimal transferAmount = new BigDecimal("600");
            System.out.printf("[Step 1] David transfers %.2f TL to Robert's Checking Account (%s)...%n",
                    transferAmount.doubleValue(), robertChecking.getAccountNumber());

            bank.transfer(davidChecking, robertChecking, transferAmount);
            System.out.println("  ✅ Transfer Completed Successfully!");
            System.out.printf("  David's New Balance: %.2f TL%n", davidChecking.getBalance().doubleValue());
            System.out.printf("  Robert's New Balance: %.2f TL%n", robertChecking.getBalance().doubleValue());

            System.out.println("[Step 2] Security Test: Attempting a self-transfer to the same account...");
            bank.transfer(davidChecking, davidChecking, new BigDecimal("100"));
        } catch (Exception e) {
            System.out.println("  ❌ Expected Self-Transfer Error Caught: " + e.getMessage());
        }

        System.out.printf("\n📜 [AUDIT REPORT] %s - %s ACCOUNT LEDGER STATEMENT%n",
                david.getName().toUpperCase(), davidChecking.getAccountNumber());
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        if (davidChecking.getHistory().isEmpty()) {
            System.out.println("No secure transaction receipts tracked on ledger yet.");
        } else {
            davidChecking.getHistory().forEach(System.out::println);

        }
    }
}