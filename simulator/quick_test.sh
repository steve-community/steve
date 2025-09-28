#!/bin/bash
# Quick test script for OCPP 2.0.1 SteVe implementation

set -e

echo "=============================================="
echo "OCPP 2.0.1 Quick Test Suite for SteVe"
echo "=============================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if SteVe is running
echo "Checking if SteVe is running..."
if ! curl -s http://localhost:8080/steve/ > /dev/null 2>&1; then
    echo -e "${YELLOW}Warning: SteVe may not be running at localhost:8080${NC}"
    echo "Please ensure SteVe is started with OCPP 2.0 enabled"
    echo ""
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ""
echo "=============================================="
echo "Test 1: CP→CSMS Operations (Certification)"
echo "=============================================="
echo ""

python3 ocpp20_certification_test.py QUICK_TEST_CP

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ All CP→CSMS tests passed!${NC}"
else
    echo ""
    echo -e "${YELLOW}⚠ Some tests failed. Check output above.${NC}"
fi

echo ""
echo "=============================================="
echo "Test Summary"
echo "=============================================="
echo ""
echo "For CSMS→CP operations testing:"
echo "  ./ocpp20_csms_test.py YOUR_CHARGER_ID"
echo ""
echo "For interactive testing:"
echo "  ./ocpp20_charge_point_simulator.py"
echo ""
echo "Documentation: README.md"
echo ""

exit $EXIT_CODE