import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Bank {

    private static final Bank INSTANCE = new Bank();
    private final Map<String, Customer> customerByCard;

    private Bank(){
        this.customerByCard = new HashMap<>();
    }

    public static Bank getInstance(){
        return INSTANCE;
    }

    public void registerCustomer(Customer customer){

        Objects.requireNonNull(customer, "Customer cannot be null.");
        String cardNumber = customer.getCard().getCardNumber();

        if (customerByCard.containsKey(cardNumber)){
            throw new IllegalArgumentException("Card number " + cardNumber + " is already registered in the bank.");
        }

        customerByCard.put(cardNumber, customer);
    }

    public Customer authenticate(String cardNumber, String pin) throws InvalidPinException,
            CardBlockedException{

        Customer customer = customerByCard.get(cardNumber);

        if (customer == null){
            throw new InvalidPinException("Invalid card number or PIN");
        }

        customer.getCard().validatePin(pin);

        return customer;
    }

    public void transfer(Account from, Account to, BigDecimal amount) throws InsufficientFundsException,
            InvalidAmountException, DailyLimitExceededException, AccountNotFoundException{

        if (from == null) {
            throw new AccountNotFoundException("Source account cannot be found.");
        }

        if (to == null) {
            throw new AccountNotFoundException("Destination account cannot be found.");
        }

        if (from.getAccountNumber().equals(to.getAccountNumber())) {
            throw new InvalidAmountException("Cannot transfer money to the same account.");
        }

        from.withdraw(amount);

        try{
            to.deposit(amount);
        } catch (InvalidAmountException e){
            from.deposit(amount);
            throw e;
        }
    }

    public Account findAccountGlobally(String accountNumber) throws AccountNotFoundException{
        for (Customer customer : customerByCard.values()){
            if (customer.getAccounts().containsKey(accountNumber)){
                return customer.getAccounts().get(accountNumber);
            }
        }
        throw new AccountNotFoundException("Account number " + accountNumber + " does not exist in our bank.");
    }
}
