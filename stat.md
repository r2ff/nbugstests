# Test Execution Statistics

**Parallel execution enabled**
**Number of threads:** 2

## Test Results

| Test Name | Duration (ms) |
|-----------|---------------|
| ChangeProfileTest.[1] Text 123, Name must contain two words with letters only | 300 |
| ChangeProfileTest.[2] Texttext, Name must contain two words with letters only | 303 |
| ChangeProfileTest.[3]  Texttext, Name must contain two words with letters only | 315 |
| ChangeProfileTest.[4]   Texttext, Name must contain two words with letters only | 307 |
| ChangeProfileTest.[5] Test Test Test, Name must contain two words with letters only | 317 |
| ChangeProfileTest.changeProfileWithValidData() | 973 |
| CreateAccountTest.userCanCreateAccountTest() | 587 |
| CreateUserTest.[1]    , Password33$, USER, username, [Username cannot be blank, Username must contain only letters, digits, dashes, underscores, and dots] | 68 |
| CreateUserTest.[2] ab, Password33$, USER, username, [Username must be between 3 and 15 characters] | 224 |
| CreateUserTest.[3] abc$, Password33$, USER, username, [Username must contain only letters, digits, dashes, underscores, and dots] | 68 |
| CreateUserTest.[4] abc%, Password33$, USER, username, [Username must contain only letters, digits, dashes, underscores, and dots] | 66 |
| CreateUserTest.adminCanCreateUserWithCorrectData() | 124 |
| DepositTest.[1] 5000.01, Deposit amount cannot exceed 5000 | 307 |
| DepositTest.[1] null, Unauthorized access to account | 540 |
| DepositTest.[2] 0.0, Deposit amount must be at least 0.01 | 308 |
| DepositTest.[2] 9223372036854775807, Unauthorized access to account | 301 |
| DepositTest.[3] -0.1, Deposit amount must be at least 0.01 | 305 |
| DepositTest.changeDepositWithValidData() | 426 |
| LoginUserTest.adminCanGenerateAuthTokenTest() | 63 |
| LoginUserTest.userCanGenerateAuthTokenTest() | 186 |
| TransferTest.[1] 10000.1, 3, Transfer amount cannot exceed 10000 | 553 |
| TransferTest.[1] 9223372036854775807, 9223372036854775807, Unauthorized access to account | 1021 |
| TransferTest.[2] 0.0, 1, Transfer amount must be at least 0.01 | 424 |
| TransferTest.[2] null, 9223372036854775807, Unauthorized access to account | 507 |
| TransferTest.[3] -0.1, 1, Transfer amount must be at least 0.01 | 430 |
| TransferTest.[4] 6000.0, 1, Invalid transfer: insufficient funds or invalid accounts | 422 |
| TransferTest.makerValidBetweenDifferentUserAccountTransfer() | 752 |
| TransferTest.makerValidBetweenOwnAccountTransfer() | 489 |

## Summary

**Total execution time:** 10686 ms
