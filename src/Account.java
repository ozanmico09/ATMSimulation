
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Account {
    private final String accountNumber;
    private final AccountType type;
    private final List<Transaction> history;

    protected BigDecimal balance;
    protected BigDecimal dailyWithdrawn;
    protected LocalDateTime lastWithdrawDate;

    public Account(String accountNumber, BigDecimal balance, AccountType type){
        this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be empty.");
        this.type = Objects.requireNonNull(type, "Account type cannot be empty.");

        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }

        this.balance = balance;
        this.history = new ArrayList<>();
        this.dailyWithdrawn = BigDecimal.ZERO;
        this.lastWithdrawDate = LocalDateTime.now();
    }

    public abstract BigDecimal getDailyWithdrawLimit();
    public abstract BigDecimal getMinBalance();

    protected void addTransaction(Transaction transaction){
        this.history.add(Objects.requireNonNull(transaction, "Transaction cannot be null."));
    }

    public void deposit(BigDecimal amount) throws InvalidAmountException {

        if (amount == null) {
            throw new InvalidAmountException("The deposit amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("The deposit amount must be positive");
        }

        balance = balance.add(amount);

        addTransaction(new Transaction(TransactionType.DEPOSIT, amount, this.balance, "Cash Deposit"));
    }

    public void withdraw(BigDecimal amount) throws InsufficientFundsException, InvalidAmountException,
            DailyLimitExceededException {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("The withdrawal amount must be positive");
        }

        resetDailyLimitIfNeeded();

        BigDecimal totalDailyWithdrawn = this.dailyWithdrawn.add(amount);

        if (totalDailyWithdrawn.compareTo(getDailyWithdrawLimit()) > 0) {
            throw new DailyLimitExceededException("Daily withdrawal limit exceeded. Limit: " + getDailyWithdrawLimit());
        }

        BigDecimal potentialBalance = this.balance.subtract(amount);

        if (potentialBalance.compareTo(getMinBalance()) < 0) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + this.balance);
        }

        this.balance = potentialBalance;
        this.dailyWithdrawn = totalDailyWithdrawn;
        this.lastWithdrawDate = LocalDateTime.now();

        addTransaction(new Transaction(TransactionType.WITHDRAW, amount, this.balance, "Cash Withdrawal"));
    }


    private void resetDailyLimitIfNeeded(){

        ZoneId bankZone = ZoneId.of("Europe/Istanbul");
        
        if (lastWithdrawDate.atZone(ZoneId.systemDefault()).withZoneSameInstant(bankZone).toLocalDate()
                .isBefore(LocalDateTime.now().toLocalDate())){
            dailyWithdrawn = BigDecimal.ZERO;
            lastWithdrawDate = LocalDateTime.now(bankZone);
        }
    }

    public BigDecimal getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public AccountType getType() { return type; }
    public List<Transaction> getHistory(){
        return Collections.unmodifiableList(history);
    }

}
