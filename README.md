# Secure & Polymorphic ATM Simulation System

A comprehensive, production-ready banking and ATM simulation system built entirely with **Core Java**. This project goes beyond a basic console application by enforcing enterprise software architecture, siber-security principles, financial data integrity, and fault-tolerant transaction management.

## Key Architectural & Technical Features

* **Polymorphic Account Architecture:** Abstract parent class `Account` managing common properties, with specialized child classes `CheckingAccount` (featuring dynamic overdraft/KMH limits) and `SavingsAccount` (featuring interest yield engines).
* **Financial Accuracy (`BigDecimal`):** Standardized all currency and financial computations with `BigDecimal` using `RoundingMode.HALF_UP` to strictly eliminate float/double rounding errors.
* **Thread-Safe Singleton Pattern:** Enforced a single, centralized database structure for the `Bank` manager using a thread-safe Singleton design pattern.
* **Data Immutability & Tamper-Proof Ledger:** Guarded the banking audit trails against external modification by utilizing `Collections.unmodifiableList` and `Collections.unmodifiableMap` wrappers.
* **Siber-Security & Authentication Guard:** Integrated defensive coding practices to prevent **User Enumeration** attacks. Automated security thresholds that lock the `CardStatus` to `BLOCKED` after 3 consecutive failed PIN attempts.
* **Atomic Transfer with Rollback Simulation:** Designed a robust intra-bank fund transfer method backed by manual transaction rollback routines to guarantee that funds are never lost mid-operation.
* **Greedy Cash Dispensation Algorithm:** Programmed an ATM cash dispenser that computes the optimal mix of official banknotes (200, 100, 50, 20 TL) for any requested withdrawal amount.

## Tech Stack & Concepts
* **Language:** Java (JDK 16+)
* **Data Structures:** LinkedHashMap (for O(1) complexity and insertion-order preservation)
* **Design Patterns:** Singleton Pattern, Constructor Chaining, Template Method Design
* **Robust Exception Handling:** Customized Checked/Unchecked Exception hierarchy (`CardBlockedException`, `InvalidPinException`, `InsufficientFundsException`, etc.)

## Included Integration Tests
The `Main` class acts as an automated integration suite that dynamically boots up mock banking customers (David Bowie & Robert De Niro) to stress-test:
1. Failed login/authentication card locking.
2. Checking account overdraft capabilities (-1000 TL flex).
3. Inter-account transfer validations and automated failure rollbacks.
4. Beautifully aligned transaction history auditing layouts.
