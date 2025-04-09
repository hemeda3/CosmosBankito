#!/bin/bash

# Script to test the blockchain API endpoints
# Usage: ./test-blockchain-api.sh [host] [port]

HOST=${1:-localhost}
PORT=${2:-8080}
BASE_URL="http://$HOST:$PORT"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Testing Blockchain API on $BASE_URL${NC}"
echo "=================================================="

# Function to make API calls
call_api() {
    local endpoint=$1
    local method=${2:-GET}
    local data=$3
    
    echo -e "${YELLOW}Calling $method $endpoint${NC}"
    
    if [ "$method" = "GET" ]; then
        curl -s -X $method "$BASE_URL$endpoint" | jq .
    else
        curl -s -X $method "$BASE_URL$endpoint" -H "Content-Type: application/json" -d "$data" | jq .
    fi
    
    echo ""
}

# Test blockchain status
echo -e "${GREEN}Testing blockchain status...${NC}"
call_api "/api/v1/blockchain/status"

# Create a random account ID
ACCOUNT_ID=$(uuidgen)
echo -e "${GREEN}Using account ID: $ACCOUNT_ID${NC}"

# Test deposit
echo -e "${GREEN}Testing deposit...${NC}"
DEPOSIT_DATA="{\"accountId\":\"$ACCOUNT_ID\",\"amount\":100.00,\"description\":\"Test deposit\",\"currencyCode\":\"ATOM\"}"
call_api "/api/v1/blockchain/deposit" "POST" "$DEPOSIT_DATA"

# Test get balance
echo -e "${GREEN}Testing get balance...${NC}"
call_api "/api/v1/blockchain/accounts/$ACCOUNT_ID/balance"

# Test multiple deposits
echo -e "${GREEN}Testing multiple deposits...${NC}"
DEPOSIT_DATA="{\"accountId\":\"$ACCOUNT_ID\",\"amount\":50.00,\"description\":\"Second test deposit\",\"currencyCode\":\"ATOM\"}"
call_api "/api/v1/blockchain/deposit" "POST" "$DEPOSIT_DATA"

# Test get balance again
echo -e "${GREEN}Testing get balance after multiple deposits...${NC}"
call_api "/api/v1/blockchain/accounts/$ACCOUNT_ID/balance"

# Test get transaction history
echo -e "${GREEN}Testing get transaction history...${NC}"
call_api "/api/v1/blockchain/accounts/$ACCOUNT_ID/transactions"

echo -e "${GREEN}All tests completed!${NC}"
