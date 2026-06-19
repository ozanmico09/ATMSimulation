import java.util.Map;
import java.util.Objects;

public class Card {
    private final String cardNumber;
    private String pinHash;
    private CardStatus status;
    private int failedAttempts;

    private static final int MAX_ATTEMPTS = 3;

    public Card(String cardNumber, String rawPin) {

        this.cardNumber = Objects.requireNonNull(cardNumber, "Card number cannot be empty.");
        this.pinHash = hashPin(Objects.requireNonNull(rawPin, "PIN cannot be empty."));
        this.status = CardStatus.ACTIVE;
        this.failedAttempts = 0;
    }

    private String hashPin(String rawPin){
        return "HASH_" + rawPin.hashCode() + "_SECURE";
    }

    public void validatePin(String inputPin) throws InvalidPinException, CardBlockedException{
        if (status == CardStatus.BLOCKED){
            throw new CardBlockedException("Card is blocked. Please contact your bank.");
        }

        if (!pinHash.equals(inputPin)){
            failedAttempts++;
            if (failedAttempts >= MAX_ATTEMPTS){
                status = CardStatus.BLOCKED;
                throw new CardBlockedException("3 incorrect attempts. Card has been blocked");
            }
            throw new InvalidPinException("Incorrect PIN. Remaining attempts: " + (MAX_ATTEMPTS - failedAttempts));
        }

        failedAttempts = 0;
    }

    public void changePin(String oldPin, String newPin) throws InvalidPinException, CardBlockedException{
        if (newPin == null || newPin.trim().isEmpty()) {
            throw new InvalidPinException("New PIN cannot be empty.");
        }

        if (!newPin.matches("\\d{4}")) {
            throw new InvalidPinException("New PIN must be exactly 4 digits.");
        }

        if (oldPin.equals(newPin)) {
            throw new InvalidPinException("New PIN cannot be the same as the old PIN.");
        }

        this.pinHash = hashPin(newPin);
        this.failedAttempts = 0;
    }

    public String getCardNumber() { return cardNumber; }
    public CardStatus getStatus() { return status; }
}
