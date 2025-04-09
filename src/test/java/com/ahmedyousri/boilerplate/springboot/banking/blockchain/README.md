# Blockchain Layer Tests

This directory contains tests for the blockchain layer of the banking system. The blockchain layer provides enhanced security, transparency, and auditability for financial transactions.

## Test Structure

The tests are organized into the following categories:

1. **Command Tests**: Tests for the financial commands (deposit, withdraw, transfer) that can be executed on the blockchain.
2. **Service Tests**: Tests for the blockchain service that provides a high-level API for interacting with the blockchain.
3. **Controller Tests**: Tests for the blockchain controller that exposes REST endpoints for interacting with the blockchain.
4. **Integration Tests**: Tests that verify the flow from controller to service to models to mocks.

## Test Classes

### Command Tests

- `DepositCommandTest`: Tests for the `DepositCommand` class, which represents a deposit operation on the blockchain.

### Service Tests

- `MockBlockchainServiceTest`: Tests for the `MockBlockchainService` class, which implements the `BlockchainService` interface using a mock blockchain state.

### Controller Tests

- `MockBlockchainControllerTest`: Tests for the `MockBlockchainController` class, which exposes REST endpoints for interacting with the mock blockchain.

### Integration Tests

- `BlockchainIntegrationTest`: Tests that verify the flow from controller to service to models to mocks for deposit operations.

## Test Suite

The `BlockchainTestSuite` class is a JUnit 5 test suite that runs all the blockchain tests together. It uses the `@Suite` and `@SelectClasses` annotations to specify which test classes to include in the suite.

## Running the Tests

### Running Individual Tests

To run an individual test class, you can use the following command:

```bash
./mvnw test -Dtest=DepositCommandTest
```

Replace `DepositCommandTest` with the name of the test class you want to run.

### Running the Test Suite

To run the entire test suite, you can use the following command:

```bash
./mvnw test -Dtest=BlockchainTestSuite
```

### Running All Tests

To run all tests in the project, you can use the following command:

```bash
./mvnw test
```

## Test Coverage

The tests cover the following aspects of the blockchain layer:

1. **Command Creation and Validation**: Tests that commands can be created with valid parameters and that validation errors are thrown for invalid parameters.
2. **Service Operations**: Tests that the blockchain service can execute commands and retrieve account balances and transactions.
3. **Controller Endpoints**: Tests that the blockchain controller endpoints return the expected responses.
4. **Integration Flow**: Tests that the entire flow from controller to service to models to mocks works correctly.

## Mock Implementation

The tests use a mock implementation of the blockchain layer to simulate blockchain operations without requiring an actual blockchain network. The mock implementation includes:

- `MockBlockchainState`: Maintains the state of the mock blockchain.
- `MockBlockchainService`: Implements the `BlockchainService` interface using the mock blockchain state.
- `MockBlockchainController`: Exposes REST endpoints for interacting with the mock blockchain.

## Adding New Tests

When adding new tests, follow these guidelines:

1. **Test Organization**: Add new tests to the appropriate category (command, service, controller, integration).
2. **Test Coverage**: Ensure that new tests cover all aspects of the functionality being tested.
3. **Test Independence**: Make sure that tests are independent and do not rely on the state of other tests.
4. **Test Suite**: Update the `BlockchainTestSuite` class to include new test classes.
