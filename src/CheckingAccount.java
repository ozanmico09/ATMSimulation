import java.math.BigDecimal;

public class CheckingAccount extends Account{
    private static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal("5000");
    private static final BigDecimal DEFAULT_MIN_BALANCE  = new BigDecimal("-1000");

    private final BigDecimal customDailyLimit;
    private final BigDecimal customMinBalance;

    public CheckingAccount(String accountNumber, BigDecimal balance){
        this(accountNumber, balance, DEFAULT_DAILY_LIMIT, DEFAULT_MIN_BALANCE);
    }

    public CheckingAccount(String accountNumber, BigDecimal balance, BigDecimal customDailyLimit,
                           BigDecimal customMinBalance){
        super(accountNumber, balance, AccountType.CHECKING);
        this.customDailyLimit = customDailyLimit != null ? customDailyLimit : DEFAULT_DAILY_LIMIT;
        this.customMinBalance = customMinBalance != null ? customMinBalance : DEFAULT_MIN_BALANCE;
    }

    @Override
    public BigDecimal getDailyWithdrawLimit() {
        return customDailyLimit;
    }

    @Override
    public BigDecimal getMinBalance() {
        return customMinBalance;
    }
}
