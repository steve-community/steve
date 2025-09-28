#!/usr/bin/env python3
"""
OCPP 2.0.1 Certification Test Suite
Comprehensive testing for OCPP 2.0.1 CSMS implementation
Based on Open Charge Alliance certification requirements
"""
import asyncio
import websockets
import json
import sys
from datetime import datetime, timezone
from typing import Dict, List, Optional, Tuple
import time

# Configuration
STEVE_URL = "ws://127.0.0.1:8080/steve/ocpp/v20/CERT_TEST_CP"
MESSAGE_ID_COUNTER = 1
TEST_RESULTS = []

class TestResult:
    def __init__(self, test_name: str, category: str, passed: bool, error: str = None):
        self.test_name = test_name
        self.category = category
        self.passed = passed
        self.error = error
        self.timestamp = datetime.now(timezone.utc)

def get_message_id() -> str:
    global MESSAGE_ID_COUNTER
    msg_id = str(MESSAGE_ID_COUNTER)
    MESSAGE_ID_COUNTER += 1
    return msg_id

def ocpp_call(action: str, payload: Dict) -> List:
    return [2, get_message_id(), action, payload]

def log_test(test_name: str, category: str, passed: bool, error: str = None):
    result = TestResult(test_name, category, passed, error)
    TEST_RESULTS.append(result)
    status = "✓ PASS" if passed else "✗ FAIL"
    print(f"[{status}] {category}/{test_name}")
    if error:
        print(f"  Error: {error}")

class OCPP20CertificationTests:
    def __init__(self, websocket):
        self.websocket = websocket
        self.transaction_id = None

    async def send_and_receive(self, action: str, payload: Dict) -> Tuple[bool, Dict]:
        """Send OCPP message and receive response"""
        request = ocpp_call(action, payload)
        print(f"\n[SEND] {action}")
        await self.websocket.send(json.dumps(request))

        response = await self.websocket.recv()
        response_data = json.loads(response)

        # Check if it's a CallResult (type 3)
        if response_data[0] != 3:
            return False, {"error": f"Expected CallResult, got message type {response_data[0]}"}

        return True, response_data[2]

    # ====================
    # A. Provisioning Tests
    # ====================

    async def test_boot_notification_power_up(self):
        """A01: BootNotification with PowerUp reason"""
        try:
            success, response = await self.send_and_receive("BootNotification", {
                "reason": "PowerUp",
                "chargingStation": {
                    "model": "CertTestCharger",
                    "vendorName": "TestVendor",
                    "serialNumber": "CERT-001",
                    "firmwareVersion": "2.0.1"
                }
            })

            assert success, "Message failed"
            assert response["status"] == "Accepted", f"Expected Accepted, got {response['status']}"
            assert "currentTime" in response, "Missing currentTime"
            assert "interval" in response, "Missing interval"

            log_test("boot_notification_power_up", "A.Provisioning", True)
        except Exception as e:
            log_test("boot_notification_power_up", "A.Provisioning", False, str(e))

    async def test_boot_notification_firmware_updated(self):
        """A02: BootNotification with FirmwareUpdate reason"""
        try:
            success, response = await self.send_and_receive("BootNotification", {
                "reason": "FirmwareUpdate",
                "chargingStation": {
                    "model": "CertTestCharger",
                    "vendorName": "TestVendor",
                    "firmwareVersion": "2.0.2"
                }
            })

            assert success, "Message failed"
            assert response["status"] in ["Accepted", "Pending"], f"Unexpected status: {response['status']}"

            log_test("boot_notification_firmware_updated", "A.Provisioning", True)
        except Exception as e:
            log_test("boot_notification_firmware_updated", "A.Provisioning", False, str(e))

    async def test_heartbeat(self):
        """A03: Heartbeat message"""
        try:
            success, response = await self.send_and_receive("Heartbeat", {})

            assert success, "Message failed"
            assert "currentTime" in response, "Missing currentTime"

            log_test("heartbeat", "A.Provisioning", True)
        except Exception as e:
            log_test("heartbeat", "A.Provisioning", False, str(e))

    async def test_status_notification_available(self):
        """A04: StatusNotification - Available"""
        try:
            success, response = await self.send_and_receive("StatusNotification", {
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "connectorStatus": "Available",
                "evseId": 1,
                "connectorId": 1
            })

            assert success, "Message failed"
            log_test("status_notification_available", "A.Provisioning", True)
        except Exception as e:
            log_test("status_notification_available", "A.Provisioning", False, str(e))

    # ====================
    # B. Authorization Tests
    # ====================

    async def test_authorize_valid_token(self):
        """B01: Authorize with valid RFID token"""
        try:
            success, response = await self.send_and_receive("Authorize", {
                "idToken": {
                    "idToken": "VALID_RFID_001",
                    "type": "ISO14443"
                }
            })

            assert success, "Message failed"
            assert "idTokenInfo" in response, "Missing idTokenInfo"
            assert response["idTokenInfo"]["status"] == "Accepted", "Token not accepted"

            log_test("authorize_valid_token", "B.Authorization", True)
        except Exception as e:
            log_test("authorize_valid_token", "B.Authorization", False, str(e))

    async def test_authorize_with_certificate(self):
        """B02: Authorize with ISO15118 certificate"""
        try:
            success, response = await self.send_and_receive("Authorize", {
                "idToken": {
                    "idToken": "CERT_TOKEN_001",
                    "type": "eMAID"
                },
                "certificate": "-----BEGIN CERTIFICATE-----\nMIICert...\n-----END CERTIFICATE-----"
            })

            assert success, "Message failed"
            assert "idTokenInfo" in response, "Missing idTokenInfo"

            log_test("authorize_with_certificate", "B.Authorization", True)
        except Exception as e:
            log_test("authorize_with_certificate", "B.Authorization", False, str(e))

    # ====================
    # C. Transaction Tests
    # ====================

    async def test_transaction_event_started(self):
        """C01: TransactionEvent - Started"""
        try:
            self.transaction_id = f"TX-{int(time.time() * 1000)}"

            success, response = await self.send_and_receive("TransactionEvent", {
                "eventType": "Started",
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "triggerReason": "Authorized",
                "seqNo": 0,
                "transactionInfo": {
                    "transactionId": self.transaction_id
                },
                "idToken": {
                    "idToken": "RFID_TX_001",
                    "type": "ISO14443"
                },
                "evse": {
                    "id": 1,
                    "connectorId": 1
                }
            })

            assert success, "Message failed"
            assert "idTokenInfo" in response, "Missing idTokenInfo"

            log_test("transaction_event_started", "C.Transactions", True)
        except Exception as e:
            log_test("transaction_event_started", "C.Transactions", False, str(e))

    async def test_transaction_event_updated(self):
        """C02: TransactionEvent - Updated with meter values"""
        try:
            success, response = await self.send_and_receive("TransactionEvent", {
                "eventType": "Updated",
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "triggerReason": "MeterValuePeriodic",
                "seqNo": 1,
                "transactionInfo": {
                    "transactionId": self.transaction_id
                },
                "meterValue": [{
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                    "sampledValue": [{
                        "value": 1234.5,
                        "context": "Transaction.Begin",
                        "measurand": "Energy.Active.Import.Register",
                        "unitOfMeasure": {"unit": "Wh"}
                    }]
                }]
            })

            assert success, "Message failed"
            log_test("transaction_event_updated", "C.Transactions", True)
        except Exception as e:
            log_test("transaction_event_updated", "C.Transactions", False, str(e))

    async def test_transaction_event_ended(self):
        """C03: TransactionEvent - Ended"""
        try:
            success, response = await self.send_and_receive("TransactionEvent", {
                "eventType": "Ended",
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "triggerReason": "EVDeparted",
                "seqNo": 2,
                "transactionInfo": {
                    "transactionId": self.transaction_id,
                    "stoppedReason": "EVDisconnected"
                },
                "meterValue": [{
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                    "sampledValue": [{
                        "value": 5678.9,
                        "context": "Transaction.End",
                        "measurand": "Energy.Active.Import.Register",
                        "unitOfMeasure": {"unit": "Wh"}
                    }]
                }]
            })

            assert success, "Message failed"
            log_test("transaction_event_ended", "C.Transactions", True)
        except Exception as e:
            log_test("transaction_event_ended", "C.Transactions", False, str(e))

    # ====================
    # D. Meter Values Tests
    # ====================

    async def test_meter_values_energy(self):
        """D01: MeterValues with energy readings"""
        try:
            success, response = await self.send_and_receive("MeterValues", {
                "evseId": 1,
                "meterValue": [{
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                    "sampledValue": [
                        {
                            "value": 1234.56,
                            "measurand": "Energy.Active.Import.Register",
                            "unitOfMeasure": {"unit": "Wh"}
                        },
                        {
                            "value": 16.5,
                            "measurand": "Current.Import",
                            "unitOfMeasure": {"unit": "A"},
                            "phase": "L1"
                        }
                    ]
                }]
            })

            assert success, "Message failed"
            log_test("meter_values_energy", "D.MeterValues", True)
        except Exception as e:
            log_test("meter_values_energy", "D.MeterValues", False, str(e))

    # ====================
    # E. Security Tests
    # ====================

    async def test_sign_certificate(self):
        """E01: SignCertificate request"""
        try:
            success, response = await self.send_and_receive("SignCertificate", {
                "csr": "-----BEGIN CERTIFICATE REQUEST-----\nMIICsr...\n-----END CERTIFICATE REQUEST-----",
                "certificateType": "ChargingStationCertificate"
            })

            assert success, "Message failed"
            assert "status" in response, "Missing status"

            log_test("sign_certificate", "E.Security", True)
        except Exception as e:
            log_test("sign_certificate", "E.Security", False, str(e))

    async def test_security_event_notification(self):
        """E02: SecurityEventNotification"""
        try:
            success, response = await self.send_and_receive("SecurityEventNotification", {
                "type": "FirmwareUpdated",
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "techInfo": "Firmware updated from v1.0 to v2.0"
            })

            assert success, "Message failed"
            log_test("security_event_notification", "E.Security", True)
        except Exception as e:
            log_test("security_event_notification", "E.Security", False, str(e))

    # ====================
    # F. Device Model Tests
    # ====================

    async def test_notify_report(self):
        """F01: NotifyReport with configuration"""
        try:
            success, response = await self.send_and_receive("NotifyReport", {
                "requestId": 1,
                "generatedAt": datetime.now(timezone.utc).isoformat(),
                "seqNo": 0,
                "reportData": [{
                    "component": {
                        "name": "ChargingStation"
                    },
                    "variable": {
                        "name": "Available"
                    },
                    "variableAttribute": [{
                        "type": "Actual",
                        "value": "true"
                    }]
                }]
            })

            assert success, "Message failed"
            log_test("notify_report", "F.DeviceModel", True)
        except Exception as e:
            log_test("notify_report", "F.DeviceModel", False, str(e))

    async def test_notify_event(self):
        """F02: NotifyEvent"""
        try:
            success, response = await self.send_and_receive("NotifyEvent", {
                "generatedAt": datetime.now(timezone.utc).isoformat(),
                "seqNo": 0,
                "eventData": [{
                    "eventId": 1,
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                    "trigger": "Alerting",
                    "actualValue": "true",
                    "component": {
                        "name": "Connector",
                        "evse": {
                            "id": 1,
                            "connectorId": 1
                        }
                    },
                    "variable": {
                        "name": "Available"
                    }
                }]
            })

            assert success, "Message failed"
            log_test("notify_event", "F.DeviceModel", True)
        except Exception as e:
            log_test("notify_event", "F.DeviceModel", False, str(e))

    # ====================
    # G. Firmware Management Tests
    # ====================

    async def test_firmware_status_notification(self):
        """G01: FirmwareStatusNotification"""
        try:
            success, response = await self.send_and_receive("FirmwareStatusNotification", {
                "status": "Downloaded",
                "requestId": 1
            })

            assert success, "Message failed"
            log_test("firmware_status_notification", "G.Firmware", True)
        except Exception as e:
            log_test("firmware_status_notification", "G.Firmware", False, str(e))

    # ====================
    # H. Diagnostics Tests
    # ====================

    async def test_log_status_notification(self):
        """H01: LogStatusNotification"""
        try:
            success, response = await self.send_and_receive("LogStatusNotification", {
                "status": "Uploaded",
                "requestId": 1
            })

            assert success, "Message failed"
            log_test("log_status_notification", "H.Diagnostics", True)
        except Exception as e:
            log_test("log_status_notification", "H.Diagnostics", False, str(e))

    # ====================
    # I. Smart Charging Tests
    # ====================

    async def test_notify_ev_charging_needs(self):
        """I01: NotifyEVChargingNeeds"""
        try:
            success, response = await self.send_and_receive("NotifyEVChargingNeeds", {
                "evseId": 1,
                "chargingNeeds": {
                    "requestedEnergyTransfer": "DC",
                    "departureTime": datetime.now(timezone.utc).isoformat(),
                    "acChargingParameters": {
                        "energyAmount": 50000,
                        "evMinCurrent": 6,
                        "evMaxCurrent": 32,
                        "evMaxVoltage": 230
                    }
                }
            })

            assert success, "Message failed"
            assert "status" in response, "Missing status"

            log_test("notify_ev_charging_needs", "I.SmartCharging", True)
        except Exception as e:
            log_test("notify_ev_charging_needs", "I.SmartCharging", False, str(e))

    async def test_report_charging_profiles(self):
        """I02: ReportChargingProfiles"""
        try:
            success, response = await self.send_and_receive("ReportChargingProfiles", {
                "requestId": 1,
                "chargingLimitSource": "CSO",
                "evseId": 1,
                "chargingProfile": [{
                    "id": 1,
                    "stackLevel": 1,
                    "chargingProfilePurpose": "TxDefaultProfile",
                    "chargingProfileKind": "Absolute",
                    "chargingSchedule": [{
                        "id": 1,
                        "chargingRateUnit": "W",
                        "chargingSchedulePeriod": [{
                            "startPeriod": 0,
                            "limit": 11000.0
                        }]
                    }]
                }]
            })

            assert success, "Message failed"
            log_test("report_charging_profiles", "I.SmartCharging", True)
        except Exception as e:
            log_test("report_charging_profiles", "I.SmartCharging", False, str(e))

    # ====================
    # J. Reservation Tests
    # ====================

    async def test_reservation_status_update(self):
        """J01: ReservationStatusUpdate"""
        try:
            success, response = await self.send_and_receive("ReservationStatusUpdate", {
                "reservationId": 1,
                "reservationUpdateStatus": "Expired"
            })

            assert success, "Message failed"
            log_test("reservation_status_update", "J.Reservation", True)
        except Exception as e:
            log_test("reservation_status_update", "J.Reservation", False, str(e))

async def run_certification_tests():
    """Run all OCPP 2.0.1 certification tests"""
    print("=" * 80)
    print("OCPP 2.0.1 CSMS Certification Test Suite")
    print("=" * 80)
    print(f"Target: {STEVE_URL}")
    print(f"Started: {datetime.now(timezone.utc).isoformat()}")
    print("=" * 80)

    try:
        async with websockets.connect(STEVE_URL, subprotocols=["ocpp2.0.1"]) as websocket:
            print(f"\n✓ WebSocket connection established")

            tests = OCPP20CertificationTests(websocket)

            # Run all test categories
            print("\n" + "=" * 80)
            print("A. PROVISIONING TESTS")
            print("=" * 80)
            await tests.test_boot_notification_power_up()
            await tests.test_boot_notification_firmware_updated()
            await tests.test_heartbeat()
            await tests.test_status_notification_available()

            print("\n" + "=" * 80)
            print("B. AUTHORIZATION TESTS")
            print("=" * 80)
            await tests.test_authorize_valid_token()
            await tests.test_authorize_with_certificate()

            print("\n" + "=" * 80)
            print("C. TRANSACTION TESTS")
            print("=" * 80)
            await tests.test_transaction_event_started()
            await tests.test_transaction_event_updated()
            await tests.test_transaction_event_ended()

            print("\n" + "=" * 80)
            print("D. METER VALUES TESTS")
            print("=" * 80)
            await tests.test_meter_values_energy()

            print("\n" + "=" * 80)
            print("E. SECURITY TESTS")
            print("=" * 80)
            await tests.test_sign_certificate()
            await tests.test_security_event_notification()

            print("\n" + "=" * 80)
            print("F. DEVICE MODEL TESTS")
            print("=" * 80)
            await tests.test_notify_report()
            await tests.test_notify_event()

            print("\n" + "=" * 80)
            print("G. FIRMWARE MANAGEMENT TESTS")
            print("=" * 80)
            await tests.test_firmware_status_notification()

            print("\n" + "=" * 80)
            print("H. DIAGNOSTICS TESTS")
            print("=" * 80)
            await tests.test_log_status_notification()

            print("\n" + "=" * 80)
            print("I. SMART CHARGING TESTS")
            print("=" * 80)
            await tests.test_notify_ev_charging_needs()
            await tests.test_report_charging_profiles()

            print("\n" + "=" * 80)
            print("J. RESERVATION TESTS")
            print("=" * 80)
            await tests.test_reservation_status_update()

    except Exception as e:
        print(f"\n✗ Connection failed: {e}")
        return False

    # Print summary
    print("\n" + "=" * 80)
    print("TEST SUMMARY")
    print("=" * 80)

    total = len(TEST_RESULTS)
    passed = sum(1 for r in TEST_RESULTS if r.passed)
    failed = total - passed

    print(f"Total Tests: {total}")
    print(f"Passed: {passed} ({100*passed//total if total > 0 else 0}%)")
    print(f"Failed: {failed}")

    # Group by category
    categories = {}
    for result in TEST_RESULTS:
        if result.category not in categories:
            categories[result.category] = {"passed": 0, "failed": 0}
        if result.passed:
            categories[result.category]["passed"] += 1
        else:
            categories[result.category]["failed"] += 1

    print("\nResults by Category:")
    for category, stats in sorted(categories.items()):
        total_cat = stats["passed"] + stats["failed"]
        print(f"  {category}: {stats['passed']}/{total_cat} passed")

    if failed > 0:
        print("\nFailed Tests:")
        for result in TEST_RESULTS:
            if not result.passed:
                print(f"  - {result.category}/{result.test_name}: {result.error}")

    print("=" * 80)

    return failed == 0

if __name__ == "__main__":
    success = asyncio.run(run_certification_tests())
    sys.exit(0 if success else 1)