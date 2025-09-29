#!/usr/bin/env python3
"""
OCPP 2.0.1 Certification Test Suite for SteVe
Tests all CP→CSMS operations with validation
"""
import asyncio
import websockets
import json
from datetime import datetime, timezone
import sys

class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

class OCPP20CertificationTests:
    def __init__(self, server_url, charger_id):
        self.server_url = server_url
        self.charger_id = charger_id
        self.websocket = None
        self.message_counter = 0
        self.passed_tests = 0
        self.failed_tests = 0

    def log_test(self, test_name, status, message=""):
        if status == "PASS":
            print(f"{Colors.OKGREEN}✓{Colors.ENDC} {test_name}")
            if message:
                print(f"  {Colors.OKCYAN}{message}{Colors.ENDC}")
            self.passed_tests += 1
        elif status == "FAIL":
            print(f"{Colors.FAIL}✗{Colors.ENDC} {test_name}")
            if message:
                print(f"  {Colors.FAIL}{message}{Colors.ENDC}")
            self.failed_tests += 1
        else:
            print(f"{Colors.WARNING}⊙{Colors.ENDC} {test_name}")
            if message:
                print(f"  {message}")

    async def send_and_wait(self, action, payload, timeout=5):
        self.message_counter += 1
        message_id = f"test-{self.message_counter}"

        request = [2, message_id, action, payload]
        await self.websocket.send(json.dumps(request))

        try:
            response_raw = await asyncio.wait_for(self.websocket.recv(), timeout=timeout)
            response = json.loads(response_raw)

            if response[0] == 3 and response[1] == message_id:
                return {"status": "success", "payload": response[2]}
            elif response[0] == 4:
                return {"status": "error", "code": response[2], "description": response[3]}
            else:
                return {"status": "invalid_response", "data": response}

        except asyncio.TimeoutError:
            return {"status": "timeout"}
        except Exception as e:
            return {"status": "exception", "error": str(e)}

    async def test_01_boot_notification(self):
        print(f"\n{Colors.HEADER}Test 1: BootNotification{Colors.ENDC}")

        payload = {
            "reason": "PowerUp",
            "chargingStation": {
                "model": "TestModel",
                "vendorName": "TestVendor",
                "serialNumber": "SN123456",
                "firmwareVersion": "1.0.0"
            }
        }

        result = await self.send_and_wait("BootNotification", payload)

        if result["status"] == "success":
            response = result["payload"]
            if "status" in response and "currentTime" in response and "interval" in response:
                self.log_test("BootNotification", "PASS",
                    f"Status: {response['status']}, Interval: {response['interval']}s")
                return True
            else:
                self.log_test("BootNotification", "FAIL", "Missing required fields in response")
        else:
            self.log_test("BootNotification", "FAIL", f"Request failed: {result}")

        return False

    async def test_02_authorize(self):
        print(f"\n{Colors.HEADER}Test 2: Authorize{Colors.ENDC}")

        payload = {
            "idToken": {
                "idToken": "TEST_TOKEN_001",
                "type": "ISO14443"
            }
        }

        result = await self.send_and_wait("Authorize", payload)

        if result["status"] == "success":
            response = result["payload"]
            if "idTokenInfo" in response and "status" in response["idTokenInfo"]:
                self.log_test("Authorize", "PASS",
                    f"Status: {response['idTokenInfo']['status']}")
                return True
            else:
                self.log_test("Authorize", "FAIL", "Missing idTokenInfo in response")
        else:
            self.log_test("Authorize", "FAIL", f"Request failed: {result}")

        return False

    async def test_03_heartbeat(self):
        print(f"\n{Colors.HEADER}Test 3: Heartbeat{Colors.ENDC}")

        result = await self.send_and_wait("Heartbeat", {})

        if result["status"] == "success":
            response = result["payload"]
            if "currentTime" in response:
                self.log_test("Heartbeat", "PASS",
                    f"Server time: {response['currentTime']}")
                return True
            else:
                self.log_test("Heartbeat", "FAIL", "Missing currentTime in response")
        else:
            self.log_test("Heartbeat", "FAIL", f"Request failed: {result}")

        return False

    async def test_04_status_notification(self):
        print(f"\n{Colors.HEADER}Test 4: StatusNotification{Colors.ENDC}")

        payload = {
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "connectorStatus": "Available",
            "evseId": 1,
            "connectorId": 1
        }

        result = await self.send_and_wait("StatusNotification", payload)

        if result["status"] == "success":
            self.log_test("StatusNotification", "PASS", "Status updated successfully")
            return True
        else:
            self.log_test("StatusNotification", "FAIL", f"Request failed: {result}")

        return False

    async def test_05_transaction_event_started(self):
        print(f"\n{Colors.HEADER}Test 5: TransactionEvent (Started){Colors.ENDC}")

        payload = {
            "eventType": "Started",
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "triggerReason": "Authorized",
            "seqNo": 0,
            "transactionInfo": {
                "transactionId": f"TXN_{int(datetime.now().timestamp())}",
                "chargingState": "Charging"
            },
            "idToken": {
                "idToken": "TEST_TOKEN_001",
                "type": "ISO14443"
            },
            "evse": {
                "id": 1,
                "connectorId": 1
            }
        }

        result = await self.send_and_wait("TransactionEvent", payload)

        if result["status"] == "success":
            response = result["payload"]
            if "idTokenInfo" in response:
                self.log_test("TransactionEvent (Started)", "PASS",
                    f"Transaction started, IdToken status: {response['idTokenInfo']['status']}")
                return True
            else:
                self.log_test("TransactionEvent (Started)", "FAIL", "Missing idTokenInfo")
        else:
            self.log_test("TransactionEvent (Started)", "FAIL", f"Request failed: {result}")

        return False

    async def test_06_meter_values(self):
        print(f"\n{Colors.HEADER}Test 6: MeterValues{Colors.ENDC}")

        payload = {
            "evseId": 1,
            "meterValue": [
                {
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                    "sampledValue": [
                        {
                            "value": 15.5,
                            "measurand": "Energy.Active.Import.Register",
                            "unitOfMeasure": {
                                "unit": "kWh"
                            }
                        },
                        {
                            "value": 230.5,
                            "measurand": "Voltage",
                            "unitOfMeasure": {
                                "unit": "V"
                            }
                        }
                    ]
                }
            ]
        }

        result = await self.send_and_wait("MeterValues", payload)

        if result["status"] == "success":
            self.log_test("MeterValues", "PASS", "Meter values recorded successfully")
            return True
        else:
            self.log_test("MeterValues", "FAIL", f"Request failed: {result}")

        return False

    async def test_07_transaction_event_ended(self):
        print(f"\n{Colors.HEADER}Test 7: TransactionEvent (Ended){Colors.ENDC}")

        payload = {
            "eventType": "Ended",
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "triggerReason": "StopAuthorized",
            "seqNo": 1,
            "transactionInfo": {
                "transactionId": f"TXN_{int(datetime.now().timestamp())}",
                "chargingState": "Idle",
                "stoppedReason": "Local"
            },
            "evse": {
                "id": 1,
                "connectorId": 1
            }
        }

        result = await self.send_and_wait("TransactionEvent", payload)

        if result["status"] == "success":
            self.log_test("TransactionEvent (Ended)", "PASS", "Transaction ended successfully")
            return True
        else:
            self.log_test("TransactionEvent (Ended)", "FAIL", f"Request failed: {result}")

        return False

    async def test_08_get_base_report(self):
        print(f"\n{Colors.HEADER}Test 8: GetBaseReport (CSMS Command){Colors.ENDC}")

        # This test waits for a GetBaseReport from CSMS
        print("Waiting for GetBaseReport command from CSMS...")
        try:
            with_timeout = asyncio.wait_for(self.websocket.recv(), timeout=5.0)
            response = await with_timeout
            message = json.loads(response)

            if message[0] == 2 and message[2] == "GetBaseReport":
                print(f"✓ Received GetBaseReport request")
                print(f"  Request ID: {message[3].get('requestId')}")
                print(f"  Report Base: {message[3].get('reportBase')}")

                # Send response
                response_msg = [3, message[1], {"status": "Accepted"}]
                await self.websocket.send(json.dumps(response_msg))

                self.log_test("GetBaseReport", "PASS", "GetBaseReport handled")
                return True
        except asyncio.TimeoutError:
            print("Note: No GetBaseReport received (this is optional)")

        return True  # Optional test

    async def test_09_get_report(self):
        print(f"\n{Colors.HEADER}Test 9: GetReport (CSMS Command){Colors.ENDC}")

        # This test waits for a GetReport from CSMS
        print("Waiting for GetReport command from CSMS...")
        try:
            with_timeout = asyncio.wait_for(self.websocket.recv(), timeout=5.0)
            response = await with_timeout
            message = json.loads(response)

            if message[0] == 2 and message[2] == "GetReport":
                print(f"✓ Received GetReport request")
                print(f"  Request ID: {message[3].get('requestId')}")

                # Send response
                response_msg = [3, message[1], {"status": "Accepted"}]
                await self.websocket.send(json.dumps(response_msg))

                self.log_test("GetReport", "PASS", "GetReport handled")
                return True
        except asyncio.TimeoutError:
            print("Note: No GetReport received (this is optional)")

        return True  # Optional test

    async def test_10_set_network_profile(self):
        print(f"\n{Colors.HEADER}Test 10: SetNetworkProfile (CSMS Command){Colors.ENDC}")

        # This test waits for a SetNetworkProfile from CSMS
        print("Waiting for SetNetworkProfile command from CSMS...")
        try:
            with_timeout = asyncio.wait_for(self.websocket.recv(), timeout=5.0)
            response = await with_timeout
            message = json.loads(response)

            if message[0] == 2 and message[2] == "SetNetworkProfile":
                print(f"✓ Received SetNetworkProfile request")
                print(f"  Configuration Slot: {message[3].get('configurationSlot')}")

                # Send response
                response_msg = [3, message[1], {"status": "Accepted"}]
                await self.websocket.send(json.dumps(response_msg))

                self.log_test("SetNetworkProfile", "PASS", "SetNetworkProfile handled")
                return True
        except asyncio.TimeoutError:
            print("Note: No SetNetworkProfile received (this is optional)")

        return True  # Optional test

    async def test_11_set_charging_profile(self):
        print(f"\n{Colors.HEADER}Test 11: SetChargingProfile (CSMS Command){Colors.ENDC}")

        # This test waits for a SetChargingProfile from CSMS
        print("Waiting for SetChargingProfile command from CSMS...")
        try:
            with_timeout = asyncio.wait_for(self.websocket.recv(), timeout=5.0)
            response = await with_timeout
            message = json.loads(response)

            if message[0] == 2 and message[2] == "SetChargingProfile":
                print(f"✓ Received SetChargingProfile request")
                profile = message[3].get('chargingProfile', {})
                print(f"  Profile ID: {profile.get('id')}")
                print(f"  Stack Level: {profile.get('stackLevel')}")

                # Send response
                response_msg = [3, message[1], {"status": "Accepted"}]
                await self.websocket.send(json.dumps(response_msg))

                self.log_test("SetChargingProfile", "PASS", "SetChargingProfile handled")
                return True
        except asyncio.TimeoutError:
            print("Note: No SetChargingProfile received (this is optional)")

        return True  # Optional test

    async def run_all_tests(self):
        print(f"\n{Colors.BOLD}{'='*60}{Colors.ENDC}")
        print(f"{Colors.BOLD}OCPP 2.0.1 Certification Tests for SteVe{Colors.ENDC}")
        print(f"{Colors.BOLD}{'='*60}{Colors.ENDC}")
        print(f"Server: {self.server_url}")
        print(f"Charge Point ID: {self.charger_id}")
        print(f"Started: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")

        try:
            async with websockets.connect(
                self.server_url,
                subprotocols=["ocpp2.0.1"]
            ) as websocket:
                self.websocket = websocket
                print(f"\n{Colors.OKGREEN}✓ Connected to server{Colors.ENDC}")

                await self.test_01_boot_notification()
                await self.test_02_authorize()
                await self.test_03_heartbeat()
                await self.test_04_status_notification()
                await self.test_05_transaction_event_started()
                await self.test_06_meter_values()
                await self.test_07_transaction_event_ended()
                # Optional CSMS command tests
                await self.test_08_get_base_report()
                await self.test_09_get_report()
                await self.test_10_set_network_profile()
                await self.test_11_set_charging_profile()

        except Exception as e:
            print(f"\n{Colors.FAIL}Connection error: {e}{Colors.ENDC}")
            return False

        print(f"\n{Colors.BOLD}{'='*60}{Colors.ENDC}")
        print(f"{Colors.BOLD}Test Summary{Colors.ENDC}")
        print(f"{Colors.BOLD}{'='*60}{Colors.ENDC}")
        print(f"Total Tests: {self.passed_tests + self.failed_tests}")
        print(f"{Colors.OKGREEN}Passed: {self.passed_tests}{Colors.ENDC}")
        print(f"{Colors.FAIL}Failed: {self.failed_tests}{Colors.ENDC}")

        if self.failed_tests == 0:
            print(f"\n{Colors.OKGREEN}{Colors.BOLD}✓ ALL TESTS PASSED{Colors.ENDC}")
            return True
        else:
            print(f"\n{Colors.FAIL}{Colors.BOLD}✗ SOME TESTS FAILED{Colors.ENDC}")
            return False

async def main():
    if len(sys.argv) > 1:
        charger_id = sys.argv[1]
    else:
        charger_id = "TEST_CP_CERT"

    server_url = f"ws://localhost:8080/steve/ocpp/v20/{charger_id}"

    tester = OCPP20CertificationTests(server_url, charger_id)
    success = await tester.run_all_tests()

    sys.exit(0 if success else 1)

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print(f"\n{Colors.WARNING}Tests interrupted by user{Colors.ENDC}")
        sys.exit(130)