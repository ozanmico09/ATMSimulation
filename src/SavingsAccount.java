import java.math.BigDecimal;
import java.math.RoundingMode;

public class SavingsAccount extends Account{
    private static final BigDecimal DEFAULT_DAILY_LIMIT  = new BigDecimal("2000");
    private static final BigDecimal DEFAULT_MIN_BALANCE  = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_INTEREST_RATE  = new BigDecimal("0.02");

    private final BigDecimal customDailyLimit;
    private final BigDecimal interestRate;

    public SavingsAccount(String accountNumber, BigDecimal balance){
        this(accountNumber, balance, DEFAULT_DAILY_LIMIT, DEFAULT_INTEREST_RATE);
    }

    public SavingsAccount(String accountNumber, BigDecimal balance, BigDecimal customDailyLimit,
                          BigDecimal interestRate){
        super(accountNumber, balance, AccountType.SAVINGS);
        this.customDailyLimit = customDailyLimit != null ? customDailyLimit : DEFAULT_DAILY_LIMIT;
        this.interestRate = interestRate != null ? interestRate : DEFAULT_INTEREST_RATE;
    }


    public void applyInterest() throws InvalidAmountException {

        BigDecimal interest = this.balance.multiply(this.interestRate)
                .setScale(2, RoundingMode.HALF_UP);

        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            deposit(interest);
        }

        addTransaction(new Transaction(
                TransactionType.DEPOSIT,
                interest,
                this.balance,
                String.format("Interest Earned (Rate: %.2f%%)", this.interestRate.multiply(new BigDecimal("100")))
        ));
    }

    @Override
    public BigDecimal getDailyWithdrawLimit() {
        return customDailyLimit;
    }

    @Override
    public BigDecimal getMinBalance() {
        return DEFAULT_MIN_BALANCE;
    }

    public BigDecimal getInterestRate(){
        return interestRate;
    }

}
