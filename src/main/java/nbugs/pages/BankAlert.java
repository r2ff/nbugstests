package nbugs.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY("Name must contain two words with letters only"),
    PLEASE_ENTER_VALID_NAME("❌ Please enter a valid name."),
    SUCCESSFULLY_DEPOSITED_TO_ACCOUNT("✅ Successfully deposited $%s to account"),
    PLEASE_DEPOSIT_LESS_OR_EQUAL_TO_5000("❌ Please deposit less or equal to 5000$."),
    PLEASE_ENTER_VALID_AMOUNT("❌ Please enter a valid amount."),
    PLEASE_SELECT_ACCOUNT("❌ Please select an account."),
    SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT("✅ Successfully transferred $%s to account %s!"),
    PLEASE_FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    TRANSFER_AMOUNT_CANNOT_EXCEED_10000("❌ Error: Transfer amount cannot exceed 10000"),
    TRANSFER_AMOUNT_MUST_BE_AT_LEAST_001("❌ Error: Transfer amount must be at least 0.01"),
    INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER("❌ No user found with this account number."),
    ;

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}
