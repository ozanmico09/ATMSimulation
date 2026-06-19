
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class Transaction {


    private final String id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(TransactionType type, BigDecimal amount, BigDecimal balanceAfter, String description){

        this.type = Objects.requireNonNull(type, "Transaction type cannot be empty.");
        this.amount = Objects.requireNonNull(amount, "Transaction amount cannot be empty.");

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative");
        }

        this.id = UUID.randomUUID().toString();
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
        this.description = description != null ? description : "";

    }

    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return String.format("[#%s] %-15s | %10.2f TL | Balance: %10.2f TL | %s | %s",
                id.substring(0, 8),
                type,
                amount.doubleValue(),
                balanceAfter.doubleValue(),
                timestamp.format(fmt),
                description);
    }

}
