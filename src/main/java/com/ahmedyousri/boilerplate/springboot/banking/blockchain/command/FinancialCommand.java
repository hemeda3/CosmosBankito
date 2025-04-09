package com.ahmedyousri.boilerplate.springboot.banking.blockchain.command;

/**
 * Interface for financial commands that can be executed on the blockchain.
 * This interface defines the contract for all financial commands.
 */
public interface FinancialCommand {
    
    /**
     * Get the type of the command.
     *
     * @return The command type
     */
    CommandType getType();
    
    /**
     * Enum representing the types of financial commands.
     */
    enum CommandType {
        /**
         * A deposit command.
         */
        DEPOSIT,
        
        /**
         * A withdrawal command.
         */
        WITHDRAW,
        
        /**
         * A transfer command.
         */
        TRANSFER
    }
}
