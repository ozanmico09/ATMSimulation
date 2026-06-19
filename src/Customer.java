import java.util.*;

public class Customer {

    private final String name;
    private final String customerId;
    private final Card card;
    private final Map<String, Account> accounts;

    public Customer(String name, String customerId, Card card){
        this.name = Objects.requireNonNull(name, "Customer name cannot be empty.");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be empty.");
        this.card = Objects.requireNonNull(card, "Customer card cannot be null.");
        this.accounts = new LinkedHashMap<>();
    }

    public void addAccount(Account account){

        Objects.requireNonNull(account, "Account cannot be null.");

        if (accounts.containsKey(account.getAccountNumber())){
            throw new IllegalArgumentException("Account number " + account.getAccountNumber() + " already exists for this customer.");
        }

        accounts.put(account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) throws AccountNotFoundException{
        Account acc = accounts.get(accountNumber);
        if (acc == null) throw new AccountNotFoundException("Account not found: " + accountNumber);
        return acc;
    }

    public List<Account> getAccountsByType(AccountType type){
      return accounts.values().stream()
              .filter(account -> account.getType() == type)
              .toList();
    }

    public Map<String, Account> getAccounts() { return Collections.unmodifiableMap(accounts);}
    public String getName() { return name; }
    public String getCustomerId() { return customerId; }
    public Card getCard() { return card; }
}
