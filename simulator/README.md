# OCPP 2.0.1 Testing & Certification Tools for SteVe

This directory contains simulators and test tools for OCPP 2.0.1 implementation in SteVe.

## 📁 Files

### Test Simulators
- **`ocpp20_certification_test.py`** - Automated certification test suite (CP→CSMS operations)
- **`ocpp20_csms_test.py`** - CSMS operations tester (CSMS→CP operations)
- **`ocpp20_charge_point_simulator.py`** - Interactive charge point simulator
- **`test_csms_all_operations.py`** - Demo of all 4 CSMS operations with examples

### Documentation
- **`README.md`** - This file

## 🚀 Quick Start

### Prerequisites
```bash
# Install Python dependencies
pip3 install websockets

# Ensure SteVe is running with OCPP 2.0 enabled
# In application.properties: ocpp.v20.enabled=true
```

### 1. Run Certification Tests (CP→CSMS)

Tests all charge point-initiated operations against SteVe:

```bash
cd simulator
chmod +x ocpp20_certification_test.py
./ocpp20_certification_test.py [CHARGE_POINT_ID]

# Example:
./ocpp20_certification_test.py MY_CHARGER_001
```

**Tests included:**
- ✓ BootNotification
- ✓ Authorize
- ✓ Heartbeat
- ✓ StatusNotification
- ✓ TransactionEvent (Started/Ended)
- ✓ MeterValues

**Expected output:**
```
============================================================
OCPP 2.0.1 Certification Tests for SteVe
============================================================
Server: ws://localhost:8080/steve/ocpp/v20/MY_CHARGER_001
Charge Point ID: MY_CHARGER_001

✓ Connected to server

Test 1: BootNotification
✓ BootNotification
  Status: Accepted, Interval: 300s

Test 2: Authorize
✓ Authorize
  Status: Accepted

...

============================================================
Test Summary
============================================================
Total Tests: 7
Passed: 7
Failed: 0

✓ ALL TESTS PASSED
```

### 2. Test CSMS Operations (CSMS→CP)

Tests server-initiated operations (RequestStartTransaction, Reset, etc.):

```bash
cd simulator
chmod +x ocpp20_csms_test.py
./ocpp20_csms_test.py [CHARGE_POINT_ID]

# Example:
./ocpp20_csms_test.py MY_CHARGER_002
```

The simulator will:
1. Connect to SteVe
2. Send BootNotification
3. Wait for CSMS operations from SteVe
4. Respond to RequestStartTransaction, Reset, UnlockConnector, etc.

**How to send CSMS operations:**

**Option A: Programmatic (Java)**
```java
// Using Ocpp20TaskService
@Autowired
private Ocpp20TaskService taskService;

// Create task
RequestStartTransactionTask task = new RequestStartTransactionTask(
    Arrays.asList("MY_CHARGER_002"),
    1,  // evseId
    "USER_TOKEN_123",
    "ISO14443"
);

// Execute
Map<String, String> results = taskService.executeTask(task);
```

**Option B: Future UI Integration**
- Navigate to Operations page
- Select OCPP 2.0 operations
- Choose RequestStartTransaction
- Fill in parameters
- Execute

### 3. Interactive Charge Point Simulator

For manual testing and debugging:

```bash
cd simulator
chmod +x ocpp20_charge_point_simulator.py
./ocpp20_charge_point_simulator.py
```

The simulator connects and waits for CSMS operations. Supports:
- ✅ RequestStartTransaction
- ✅ RequestStopTransaction
- ✅ Reset (Immediate/OnIdle)
- ✅ UnlockConnector

### 4. Complete CSMS Operations Demo

Demonstrates all 4 CSMS operations with usage examples:

```bash
cd simulator
./test_csms_all_operations.py [CHARGER_ID]

# Example:
./test_csms_all_operations.py DEMO_CHARGER
```

This demo:
- Shows all available CSMS operations
- Provides Java code examples
- Handles all 4 operations: RequestStart/Stop, Reset, UnlockConnector
- Perfect for learning and testing

## 📊 Test Scenarios

### Scenario 1: Full Transaction Lifecycle

```bash
# Terminal 1: Start certification tests
./ocpp20_certification_test.py TEST_CHARGER

# Expected: All tests pass, transaction created in database
```

**Verify in database:**
```sql
-- Check boot notification
SELECT * FROM ocpp20_boot_notification
WHERE charge_box_id = 'TEST_CHARGER'
ORDER BY last_seen DESC LIMIT 1;

-- Check authorization
SELECT * FROM ocpp20_authorization
WHERE charge_box_id = 'TEST_CHARGER'
ORDER BY last_used DESC LIMIT 1;

-- Check transactions
SELECT * FROM ocpp20_transaction
WHERE charge_box_id = 'TEST_CHARGER'
ORDER BY start_timestamp DESC LIMIT 1;

-- Check transaction events
SELECT * FROM ocpp20_transaction_event
WHERE charge_box_id = 'TEST_CHARGER'
ORDER BY event_timestamp DESC LIMIT 5;
```

### Scenario 2: All CSMS Operations Test

```bash
# Terminal 1: Start demo simulator
./test_csms_all_operations.py CSMS_TEST_CHARGER

# Terminal 2: Execute operations via Java
# See OCPP20_IMPLEMENTATION_COMPLETE.md for code examples
```

**Test each operation:**

1. **RequestStartTransaction**:
```java
RequestStartTransactionTask task = new RequestStartTransactionTask(
    Arrays.asList("CSMS_TEST_CHARGER"), 1, "TOKEN_123", "ISO14443"
);
taskService.executeTask(task);
```

2. **RequestStopTransaction**:
```java
RequestStopTransactionTask task = new RequestStopTransactionTask(
    Arrays.asList("CSMS_TEST_CHARGER"), "TXN_1234567890"
);
taskService.executeTask(task);
```

3. **Reset**:
```java
ResetTask task = new ResetTask(
    Arrays.asList("CSMS_TEST_CHARGER"), ResetEnum.IMMEDIATE
);
taskService.executeTask(task);
```

4. **UnlockConnector**:
```java
UnlockConnectorTask task = new UnlockConnectorTask(
    Arrays.asList("CSMS_TEST_CHARGER"), 1, 1
);
taskService.executeTask(task);
```

**Expected simulator output for each:**
```
📱 Remote Start Transaction Request
  EVSE ID: 1
  ID Token: TOKEN_123
  Token Type: ISO14443
  Remote Start ID: 1234567890
✅ Sent acceptance response

🛑 Remote Stop Transaction Request
  Transaction ID: TXN_1234567890
✅ Sent acceptance response

🔄 Reset Request
  Type: Immediate
✅ Sent acceptance response

🔓 Unlock Connector Request
  EVSE ID: 1
  Connector ID: 1
✅ Sent unlock response
```

### Scenario 3: Stress Testing

Run multiple simultaneous charge points:

```bash
# Terminal 1
./ocpp20_certification_test.py CHARGER_001 &

# Terminal 2
./ocpp20_certification_test.py CHARGER_002 &

# Terminal 3
./ocpp20_certification_test.py CHARGER_003 &

# Wait for all to complete
wait
```

## 🔍 Debugging

### Enable Verbose Logging

In SteVe's `application.properties`:
```properties
logging.level.de.rwth.idsg.steve.ocpp20=DEBUG
```

### Check SteVe Logs

```bash
# Real-time log monitoring
tail -f target/logs/steve.log | grep "OCPP 2.0"

# Check for errors
grep -i "error\|exception" target/logs/steve.log | grep "ocpp20"
```

### Common Issues

**1. Connection Refused**
```
Error: Multiple exceptions: [Errno 61] Connect call failed
```
**Solution:** Ensure SteVe is running and OCPP 2.0 is enabled:
```bash
# Check SteVe is running
curl http://localhost:8080/steve/

# Check OCPP 2.0 endpoint
curl http://localhost:8080/steve/ocpp/v20/
```

**2. Charge Box Not Found**
```
WARN: ChargeBox 'TEST_CP' not found in database
```
**Solution:** This is expected for test charge points. Operations will work, but persistence is skipped. To fix:
- Register charge point in SteVe UI (Data Management > Charge Points > Add)
- Or create manually in database

**3. WebSocket Upgrade Failed**
```
Error: Invalid status code 404
```
**Solution:** Check WebSocket path configuration in `Ocpp20WebSocketConfiguration`:
```java
// Should be: /ocpp/v20/*
@Bean
public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
}
```

## 📈 Performance Benchmarks

### Expected Response Times (localhost)

| Operation | Expected Time | Acceptable |
|-----------|--------------|------------|
| BootNotification | < 50ms | < 200ms |
| Authorize | < 30ms | < 100ms |
| Heartbeat | < 20ms | < 50ms |
| TransactionEvent | < 100ms | < 300ms |
| MeterValues | < 50ms | < 150ms |

### Concurrent Connections

SteVe should handle:
- ✓ 100+ simultaneous charge points
- ✓ 1000+ transactions per hour
- ✓ < 1% message loss under normal load

**Test concurrent connections:**
```bash
for i in {1..50}; do
    ./ocpp20_certification_test.py "CHARGER_$i" &
done
wait
```

## 🎯 Certification Checklist

### OCPP 2.0.1 Core Profile

**Charge Point → CSMS (Implemented)**
- [x] BootNotification
- [x] Authorize
- [x] Heartbeat
- [x] StatusNotification
- [x] TransactionEvent
- [x] MeterValues
- [x] NotifyReport
- [x] NotifyEvent
- [x] SecurityEventNotification
- [x] SignCertificate
- [x] FirmwareStatusNotification
- [x] LogStatusNotification

**CSMS → Charge Point (Implemented)**
- [x] RequestStartTransaction
- [x] RequestStopTransaction
- [x] Reset
- [x] UnlockConnector
- [ ] GetVariables (Add following same pattern)
- [ ] SetVariables (Add following same pattern)
- [ ] TriggerMessage (Add following same pattern)

### Advanced Features
- [x] NotifyEVChargingNeeds
- [x] ReportChargingProfiles
- [x] NotifyChargingLimit
- [x] ReservationStatusUpdate
- [x] NotifyCustomerInformation
- [x] NotifyDisplayMessages

## 🔧 Extending Tests

### Add New Test Case

1. Edit `ocpp20_certification_test.py`
2. Add new test method:

```python
async def test_08_your_new_test(self):
    print(f"\n{Colors.HEADER}Test 8: YourNewTest{Colors.ENDC}")

    payload = {
        "yourField": "yourValue"
    }

    result = await self.send_and_wait("YourAction", payload)

    if result["status"] == "success":
        self.log_test("YourNewTest", "PASS")
        return True
    else:
        self.log_test("YourNewTest", "FAIL", f"Request failed: {result}")
        return False
```

3. Call in `run_all_tests()`:
```python
await self.test_08_your_new_test()
```

### Add CSMS Operation Support

1. Edit `ocpp20_csms_test.py`
2. Add handler in `handle_csms_request()`:

```python
elif action == "YourNewAction":
    print(f"\n{Colors.BOLD}🎯 Your New Action{Colors.ENDC}")
    return {
        "status": "Accepted"
    }
```

## 📚 Additional Resources

- OCPP 2.0.1 Specification: https://www.openchargealliance.org/protocols/ocpp-201/
- SteVe Documentation: https://github.com/steve-community/steve
- WebSocket Protocol: https://datatracker.ietf.org/doc/html/rfc6455

## 🤝 Contributing

To add new tests:
1. Follow existing pattern in `ocpp20_certification_test.py`
2. Ensure tests are idempotent (can run multiple times)
3. Add clear success/failure messages
4. Update this README with new test documentation

## 📝 License

Same as SteVe - GNU General Public License v3.0