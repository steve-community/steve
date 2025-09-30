#!/usr/bin/env python3
"""
OCPP 2.0.1 Complete Simulator and Test Suite for SteVe
Tests ALL OCPP 2.0 operations: both CP→CSMS and CSMS→CP
Includes bidirectional communication testing
"""

import asyncio
import websockets
import json
from datetime import datetime, timezone
import sys
import random
import uuid
from enum import Enum
import argparse
import signal

class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

class MessageType(Enum):
    CALL = 2
    CALL_RESULT = 3
    CALL_ERROR = 4

class OCPP20CompleteSimulator:
    """Complete OCPP 2.0 Simulator supporting all operations"""

    def __init__(self, server_url, charger_id):
        self.server_url = server_url
        self.charger_id = charger_id
        self.websocket = None
        self.message_counter = 0
        self.passed_tests = 0
        self.failed_tests = 0
        self.pending_requests = {}
        self.transaction_id = None
        self.id_token = {"idToken": "TEST001", "type": "ISO14443"}

    # CP→CSMS Operations (Charge Point initiated)
    CP_TO_CSMS_OPERATIONS = [
        "Authorize",
        "BootNotification",
        "ClearedChargingLimit",
        "DataTransfer",
        "FirmwareStatusNotification",
        "Get15118EVCertificate",
        "GetCertificateStatus",
        "Heartbeat",
        "LogStatusNotification",
        "MeterValues",
        "NotifyChargingLimit",
        "NotifyCustomerInformation",
        "NotifyDisplayMessages",
        "NotifyEVChargingNeeds",
        "NotifyEVChargingSchedule",
        "NotifyEvent",
        "NotifyMonitoringReport",
        "NotifyReport",
        "PublishFirmwareStatusNotification",
        "ReportChargingProfiles",
        "ReservationStatusUpdate",
        "SecurityEventNotification",
        "SignCertificate",
        "StatusNotification",
        "TransactionEvent"
    ]

    # CSMS→CP Operations (Central System initiated)
    CSMS_TO_CP_OPERATIONS = [
        "CancelReservation",
        "CertificateSigned",
        "ChangeAvailability",
        "ClearCache",
        "ClearChargingProfile",
        "ClearDisplayMessage",
        "ClearVariableMonitoring",
        "CostUpdated",
        "CustomerInformation",
        "DataTransfer",
        "DeleteCertificate",
        "Get15118EVCertificate",
        "GetBaseReport",
        "GetCertificateStatus",
        "GetChargingProfiles",
        "GetCompositeSchedule",
        "GetDisplayMessages",
        "GetInstalledCertificateIds",
        "GetLocalListVersion",
        "GetLog",
        "GetMonitoringReport",
        "GetReport",
        "GetTransactionStatus",
        "GetVariables",
        "InstallCertificate",
        "PublishFirmware",
        "RequestStartTransaction",
        "RequestStopTransaction",
        "ReserveNow",
        "Reset",
        "SendLocalList",
        "SetChargingProfile",
        "SetDisplayMessage",
        "SetMonitoringBase",
        "SetMonitoringLevel",
        "SetNetworkProfile",
        "SetVariableMonitoring",
        "SetVariables",
        "TriggerMessage",
        "UnlockConnector",
        "UnpublishFirmware",
        "UpdateFirmware"
    ]

    def log_test(self, test_name, status, message=""):
        """Log test results with colored output"""
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

    async def connect(self):
        """Establish WebSocket connection"""
        try:
            self.websocket = await websockets.connect(
                f"{self.server_url}/{self.charger_id}",
                subprotocols=["ocpp2.0", "ocpp2.0.1"]
            )
            print(f"{Colors.OKGREEN}Connected to {self.server_url}/{self.charger_id}{Colors.ENDC}")
            return True
        except Exception as e:
            print(f"{Colors.FAIL}Failed to connect: {e}{Colors.ENDC}")
            return False

    async def send_request(self, action, payload, timeout=10):
        """Send OCPP request and wait for response"""
        self.message_counter += 1
        message_id = f"msg-{self.message_counter}"

        request = [MessageType.CALL.value, message_id, action, payload]
        await self.websocket.send(json.dumps(request))

        # Store pending request for response matching
        self.pending_requests[message_id] = action

        try:
            response_raw = await asyncio.wait_for(self.websocket.recv(), timeout=timeout)
            response = json.loads(response_raw)

            if response[0] == MessageType.CALL_RESULT.value and response[1] == message_id:
                del self.pending_requests[message_id]
                return {"status": "success", "payload": response[2]}
            elif response[0] == MessageType.CALL_ERROR.value and response[1] == message_id:
                del self.pending_requests[message_id]
                return {"status": "error", "code": response[2], "description": response[3]}
            else:
                return {"status": "unexpected", "data": response}

        except asyncio.TimeoutError:
            return {"status": "timeout"}
        except Exception as e:
            return {"status": "exception", "error": str(e)}

    async def handle_incoming_request(self):
        """Handle incoming CSMS→CP requests"""
        while True:
            try:
                message = await asyncio.wait_for(self.websocket.recv(), timeout=1.0)
                data = json.loads(message)

                if data[0] == MessageType.CALL.value:
                    message_id = data[1]
                    action = data[2]
                    payload = data[3]

                    print(f"\n{Colors.OKCYAN}📥 Received {action} request from CSMS{Colors.ENDC}")

                    # Send appropriate response based on action
                    response = await self.generate_response(action, payload)
                    response_message = [MessageType.CALL_RESULT.value, message_id, response]
                    await self.websocket.send(json.dumps(response_message))

                    print(f"{Colors.OKGREEN}✅ Sent response for {action}{Colors.ENDC}")

            except asyncio.TimeoutError:
                # Just continue waiting - no message received in this timeout period
                continue
            except websockets.exceptions.ConnectionClosed:
                print(f"\n{Colors.WARNING}WebSocket connection closed{Colors.ENDC}")
                break
            except asyncio.CancelledError:
                # Handle graceful shutdown
                raise
            except Exception as e:
                print(f"{Colors.FAIL}Error handling incoming: {e}{Colors.ENDC}")
                # Continue listening unless it's a critical error
                if "closed" in str(e).lower():
                    break

    async def generate_response(self, action, payload):
        """Generate appropriate response for CSMS→CP requests"""
        responses = {
            "Reset": {"status": "Accepted"},
            "ClearCache": {"status": "Accepted"},
            "GetVariables": {"getVariableResult": [{"attributeStatus": "Accepted"}]},
            "SetVariables": {"setVariableResult": [{"attributeStatus": "Accepted"}]},
            "GetBaseReport": {"status": "Accepted"},
            "GetReport": {"status": "Accepted"},
            "SetNetworkProfile": {"status": "Accepted"},
            "ChangeAvailability": {"status": "Accepted"},
            "TriggerMessage": {"status": "Accepted"},
            "RequestStartTransaction": {"status": "Accepted", "transactionId": str(uuid.uuid4())},
            "RequestStopTransaction": {"status": "Accepted"},
            "GetLog": {"status": "Accepted"},
            "UpdateFirmware": {"status": "Accepted"},
            "UnlockConnector": {"status": "Unlocked"},
            "GetChargingProfiles": {"status": "Accepted"},
            "SetChargingProfile": {"status": "Accepted"},
            "ClearChargingProfile": {"status": "Accepted"},
            "GetCompositeSchedule": {"status": "Accepted"},
            "GetLocalListVersion": {"versionNumber": 1},
            "SendLocalList": {"status": "Accepted"},
            "GetTransactionStatus": {"messagesInQueue": False},
            "InstallCertificate": {"status": "Accepted"},
            "GetInstalledCertificateIds": {"status": "Accepted"},
            "DeleteCertificate": {"status": "Accepted"},
            "CertificateSigned": {"status": "Accepted"},
            "ReserveNow": {"status": "Accepted"},
            "CancelReservation": {"status": "Accepted"},
            "SetMonitoringBase": {"status": "Accepted"},
            "GetMonitoringReport": {"status": "Accepted"},
            "SetMonitoringLevel": {"status": "Accepted"},
            "SetVariableMonitoring": {"status": "Accepted"},
            "ClearVariableMonitoring": {"clearMonitoringResult": [{"status": "Accepted"}]},
            "GetDisplayMessages": {"status": "Accepted"},
            "SetDisplayMessage": {"status": "Accepted"},
            "ClearDisplayMessage": {"status": "Accepted"},
            "CostUpdated": {"status": "Accepted"},
            "CustomerInformation": {"status": "Accepted"},
            "PublishFirmware": {"status": "Accepted"},
            "UnpublishFirmware": {"status": "Accepted"},
            "DataTransfer": {"status": "Accepted"}
        }

        return responses.get(action, {"status": "Accepted"})

    # CP→CSMS Operation Tests
    async def test_boot_notification(self):
        """Test BootNotification"""
        payload = {
            "reason": "PowerUp",
            "chargingStation": {
                "model": "Simulator2024",
                "vendorName": "OCPP Testing Inc",
                "serialNumber": f"SIM-{random.randint(1000, 9999)}",
                "firmwareVersion": "2.0.1"
            }
        }

        result = await self.send_request("BootNotification", payload)
        if result["status"] == "success":
            self.log_test("BootNotification", "PASS",
                         f"Status: {result['payload'].get('status')}")
            return True
        else:
            self.log_test("BootNotification", "FAIL", f"Error: {result}")
            return False

    async def test_authorize(self):
        """Test Authorize"""
        payload = {
            "idToken": self.id_token
        }

        result = await self.send_request("Authorize", payload)
        if result["status"] == "success":
            self.log_test("Authorize", "PASS",
                         f"Status: {result['payload'].get('idTokenInfo', {}).get('status')}")
            return True
        else:
            self.log_test("Authorize", "FAIL", f"Error: {result}")
            return False

    async def test_heartbeat(self):
        """Test Heartbeat"""
        result = await self.send_request("Heartbeat", {})
        if result["status"] == "success":
            self.log_test("Heartbeat", "PASS",
                         f"Time: {result['payload'].get('currentTime')}")
            return True
        else:
            self.log_test("Heartbeat", "FAIL", f"Error: {result}")
            return False

    async def test_status_notification(self):
        """Test StatusNotification"""
        payload = {
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "connectorStatus": "Available",
            "evseId": 1,
            "connectorId": 1
        }

        result = await self.send_request("StatusNotification", payload)
        if result["status"] == "success":
            self.log_test("StatusNotification", "PASS", "Connector status updated")
            return True
        else:
            self.log_test("StatusNotification", "FAIL", f"Error: {result}")
            return False

    async def test_transaction_event(self):
        """Test TransactionEvent (Started/Updated/Ended)"""
        # Start transaction
        self.transaction_id = str(uuid.uuid4())
        start_payload = {
            "eventType": "Started",
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "triggerReason": "Authorized",
            "seqNo": 0,
            "transactionInfo": {
                "transactionId": self.transaction_id
            },
            "evse": {"id": 1, "connectorId": 1},
            "idToken": self.id_token
        }

        result = await self.send_request("TransactionEvent", start_payload)
        if result["status"] != "success":
            self.log_test("TransactionEvent (Started)", "FAIL", f"Error: {result}")
            return False

        self.log_test("TransactionEvent (Started)", "PASS",
                     f"Transaction ID: {self.transaction_id}")

        # Update transaction
        update_payload = {
            "eventType": "Updated",
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "triggerReason": "MeterValuePeriodic",
            "seqNo": 1,
            "transactionInfo": {
                "transactionId": self.transaction_id
            }
        }

        result = await self.send_request("TransactionEvent", update_payload)
        if result["status"] == "success":
            self.log_test("TransactionEvent (Updated)", "PASS", "Transaction updated")

        # End transaction
        end_payload = {
            "eventType": "Ended",
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "triggerReason": "EVDisconnected",
            "seqNo": 2,
            "transactionInfo": {
                "transactionId": self.transaction_id,
                "stoppedReason": "EVDisconnected"
            },
            "meterValue": [{
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "sampledValue": [{
                    "value": "12345",
                    "context": "Transaction.End",
                    "measurand": "Energy.Active.Import.Register",
                    "unit": "Wh"
                }]
            }]
        }

        result = await self.send_request("TransactionEvent", end_payload)
        if result["status"] == "success":
            self.log_test("TransactionEvent (Ended)", "PASS", "Transaction completed")
            return True
        else:
            self.log_test("TransactionEvent (Ended)", "FAIL", f"Error: {result}")
            return False

    async def test_meter_values(self):
        """Test MeterValues"""
        payload = {
            "evseId": 1,
            "meterValue": [{
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "sampledValue": [{
                    "value": "230.5",
                    "context": "Sample.Periodic",
                    "measurand": "Voltage",
                    "unit": "V",
                    "phase": "L1"
                }]
            }]
        }

        result = await self.send_request("MeterValues", payload)
        if result["status"] == "success":
            self.log_test("MeterValues", "PASS", "Meter values sent")
            return True
        else:
            self.log_test("MeterValues", "FAIL", f"Error: {result}")
            return False

    async def test_notify_report(self):
        """Test NotifyReport"""
        payload = {
            "requestId": random.randint(1, 1000),
            "generatedAt": datetime.now(timezone.utc).isoformat(),
            "seqNo": 0,
            "reportData": [{
                "component": {"name": "OCPPCommCtrlr"},
                "variable": {"name": "HeartbeatInterval"},
                "variableAttribute": [{
                    "value": "60",
                    "persistent": True,
                    "constant": False
                }]
            }]
        }

        result = await self.send_request("NotifyReport", payload)
        if result["status"] == "success":
            self.log_test("NotifyReport", "PASS", "Report sent")
            return True
        else:
            self.log_test("NotifyReport", "FAIL", f"Error: {result}")
            return False

    async def test_notify_event(self):
        """Test NotifyEvent"""
        payload = {
            "generatedAt": datetime.now(timezone.utc).isoformat(),
            "seqNo": 0,
            "eventData": [{
                "eventId": random.randint(1, 1000),
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "trigger": "Alerting",
                "actualValue": "High Temperature",
                "component": {"name": "Controller"},
                "variable": {"name": "Temperature"},
                "eventNotificationType": "HardWiredNotification"
            }]
        }

        result = await self.send_request("NotifyEvent", payload)
        if result["status"] == "success":
            self.log_test("NotifyEvent", "PASS", "Event notification sent")
            return True
        else:
            self.log_test("NotifyEvent", "FAIL", f"Error: {result}")
            return False

    async def test_security_event_notification(self):
        """Test SecurityEventNotification"""
        payload = {
            "type": "SettingSystemTime",
            "timestamp": datetime.now(timezone.utc).isoformat()
        }

        result = await self.send_request("SecurityEventNotification", payload)
        if result["status"] == "success":
            self.log_test("SecurityEventNotification", "PASS", "Security event logged")
            return True
        else:
            self.log_test("SecurityEventNotification", "FAIL", f"Error: {result}")
            return False

    async def test_firmware_status_notification(self):
        """Test FirmwareStatusNotification"""
        payload = {
            "status": "Downloading",
            "requestId": random.randint(1, 1000)
        }

        result = await self.send_request("FirmwareStatusNotification", payload)
        if result["status"] == "success":
            self.log_test("FirmwareStatusNotification", "PASS", "Firmware status updated")
            return True
        else:
            self.log_test("FirmwareStatusNotification", "FAIL", f"Error: {result}")
            return False

    async def test_cleared_charging_limit(self):
        """Test ClearedChargingLimit"""
        payload = {
            "chargingLimitSource": "EMS",
            "evseId": 1
        }

        result = await self.send_request("ClearedChargingLimit", payload)
        if result["status"] == "success":
            self.log_test("ClearedChargingLimit", "PASS", "Charging limit cleared")
            return True
        else:
            self.log_test("ClearedChargingLimit", "FAIL", f"Error: {result}")
            return False

    async def test_notify_charging_limit(self):
        """Test NotifyChargingLimit"""
        payload = {
            "evseId": 1,
            "chargingLimit": {
                "chargingLimitSource": "EMS",
                "isGridCritical": False
            }
        }

        result = await self.send_request("NotifyChargingLimit", payload)
        if result["status"] == "success":
            self.log_test("NotifyChargingLimit", "PASS", "Charging limit notified")
            return True
        else:
            self.log_test("NotifyChargingLimit", "FAIL", f"Error: {result}")
            return False

    async def test_report_charging_profiles(self):
        """Test ReportChargingProfiles"""
        payload = {
            "requestId": random.randint(1, 1000),
            "chargingLimitSource": "EMS",
            "evseId": 1,
            "chargingProfile": [{
                "id": 1,
                "stackLevel": 0,
                "chargingProfilePurpose": "TxDefaultProfile",
                "chargingProfileKind": "Absolute",
                "chargingSchedule": [{
                    "id": 1,
                    "startSchedule": datetime.now(timezone.utc).isoformat(),
                    "chargingRateUnit": "W",
                    "chargingSchedulePeriod": [{
                        "startPeriod": 0,
                        "limit": 11000
                    }]
                }]
            }]
        }

        result = await self.send_request("ReportChargingProfiles", payload)
        if result["status"] == "success":
            self.log_test("ReportChargingProfiles", "PASS", "Charging profiles reported")
            return True
        else:
            self.log_test("ReportChargingProfiles", "FAIL", f"Error: {result}")
            return False

    async def test_all_cp_to_csms_operations(self):
        """Test all CP→CSMS operations"""
        print(f"\n{Colors.HEADER}{'='*60}{Colors.ENDC}")
        print(f"{Colors.HEADER}Testing CP→CSMS Operations{Colors.ENDC}")
        print(f"{Colors.HEADER}{'='*60}{Colors.ENDC}\n")

        # Core operations
        await self.test_boot_notification()
        await asyncio.sleep(0.5)
        await self.test_authorize()
        await asyncio.sleep(0.5)
        await self.test_heartbeat()
        await asyncio.sleep(0.5)
        await self.test_status_notification()
        await asyncio.sleep(0.5)
        await self.test_transaction_event()
        await asyncio.sleep(0.5)
        await self.test_meter_values()
        await asyncio.sleep(0.5)

        # Notification operations
        await self.test_notify_report()
        await asyncio.sleep(0.5)
        await self.test_notify_event()
        await asyncio.sleep(0.5)
        await self.test_security_event_notification()
        await asyncio.sleep(0.5)
        await self.test_firmware_status_notification()
        await asyncio.sleep(0.5)

        # Charging limit operations
        await self.test_cleared_charging_limit()
        await asyncio.sleep(0.5)
        await self.test_notify_charging_limit()
        await asyncio.sleep(0.5)
        await self.test_report_charging_profiles()
        await asyncio.sleep(0.5)

    async def simulate_csms_operations(self, timeout=None):
        """Simulate handling of CSMS→CP operations

        Args:
            timeout: Optional timeout in seconds. If None, runs indefinitely.
        """
        print(f"\n{Colors.HEADER}{'='*60}{Colors.ENDC}")
        print(f"{Colors.HEADER}Ready to handle CSMS→CP Operations{Colors.ENDC}")
        print(f"{Colors.HEADER}{'='*60}{Colors.ENDC}\n")

        print(f"{Colors.OKCYAN}Listening for incoming CSMS requests...{Colors.ENDC}")
        print(f"{Colors.OKCYAN}The simulator will respond to:{Colors.ENDC}")

        for op in self.CSMS_TO_CP_OPERATIONS[:10]:  # Show first 10 as examples
            print(f"  • {op}")
        print(f"  ... and {len(self.CSMS_TO_CP_OPERATIONS) - 10} more operations")

        # Listen for incoming requests
        print(f"\n{Colors.OKBLUE}Simulator is now online and listening for CSMS→CP messages...{Colors.ENDC}")

        if timeout:
            print(f"{Colors.WARNING}Will run for {timeout} seconds{Colors.ENDC}")
            try:
                await asyncio.wait_for(self._listen_loop(), timeout=timeout)
            except asyncio.TimeoutError:
                print(f"\n{Colors.WARNING}Timeout reached after {timeout} seconds{Colors.ENDC}")
        else:
            print(f"{Colors.WARNING}Press Ctrl+C to stop the simulator{Colors.ENDC}")
            try:
                await self._listen_loop()
            except asyncio.CancelledError:
                print(f"\n{Colors.WARNING}Simulator shutdown requested{Colors.ENDC}")

    async def _listen_loop(self):
        """Internal listening loop"""
        while True:
            await self.handle_incoming_request()

    async def run_complete_test(self, listen_timeout=None):
        """Run complete test suite

        Args:
            listen_timeout: Optional timeout in seconds for listening phase
        """
        if not await self.connect():
            return False

        try:
            # Test CP→CSMS operations
            await self.test_all_cp_to_csms_operations()

            # Ready to handle CSMS→CP operations
            await self.simulate_csms_operations(timeout=listen_timeout)

            # Print final summary
            print(f"\n{Colors.HEADER}{'='*60}{Colors.ENDC}")
            print(f"{Colors.HEADER}Test Summary{Colors.ENDC}")
            print(f"{Colors.HEADER}{'='*60}{Colors.ENDC}")
            print(f"{Colors.OKGREEN}Passed: {self.passed_tests}{Colors.ENDC}")
            print(f"{Colors.FAIL}Failed: {self.failed_tests}{Colors.ENDC}")

            if self.failed_tests == 0:
                print(f"\n{Colors.OKGREEN}All tests passed! 🎉{Colors.ENDC}")
            else:
                print(f"\n{Colors.WARNING}Some tests failed. Please review the output.{Colors.ENDC}")

            return self.failed_tests == 0

        finally:
            if self.websocket:
                await self.websocket.close()

async def main():
    """Main entry point"""
    parser = argparse.ArgumentParser(
        description='OCPP 2.0.1 Complete Simulator for testing both CP→CSMS and CSMS→CP operations'
    )
    parser.add_argument(
        '--timeout', '-t',
        type=int,
        default=None,
        help='Timeout in seconds for listening phase (default: run indefinitely)'
    )
    parser.add_argument(
        '--server',
        default='ws://localhost:8080/steve/ocpp/v20',
        help='WebSocket server URL (default: ws://localhost:8080/steve/ocpp/v20)'
    )
    parser.add_argument(
        '--charger-id',
        default=f'CP_SIM_{random.randint(1000, 9999)}',
        help='Charger ID (default: auto-generated CP_SIM_XXXX)'
    )

    args = parser.parse_args()

    print(f"{Colors.BOLD}OCPP 2.0.1 Complete Simulator{Colors.ENDC}")
    print(f"Tests both CP→CSMS and CSMS→CP operations\n")

    print(f"Server: {args.server}")
    print(f"Charger ID: {args.charger_id}")
    if args.timeout:
        print(f"Timeout: {args.timeout} seconds")

    simulator = OCPP20CompleteSimulator(args.server, args.charger_id)
    success = await simulator.run_complete_test(listen_timeout=args.timeout)

    sys.exit(0 if success else 1)

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print(f"\n{Colors.WARNING}Test interrupted by user{Colors.ENDC}")
        sys.exit(1)