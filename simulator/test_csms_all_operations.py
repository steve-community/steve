#!/usr/bin/env python3
"""
Test all CSMS→CP operations
Demonstrates RequestStart, RequestStop, Reset, and UnlockConnector
"""
import asyncio
import websockets
import json
from datetime import datetime
import sys

class Colors:
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    CYAN = '\033[96m'

class CSMSOperationsDemo:
    def __init__(self, server_url, charger_id):
        self.server_url = server_url
        self.charger_id = charger_id
        self.websocket = None
        self.transaction_id = None

    async def handle_server_messages(self):
        """Handle incoming CSMS requests"""
        try:
            async for message in self.websocket:
                data = json.loads(message)
                message_type = data[0]
                message_id = data[1]

                if message_type == 2:  # Call
                    action = data[2]
                    payload = data[3]

                    print(f"\n{Colors.CYAN}← Server Request: {action}{Colors.ENDC}")
                    print(f"   Message ID: {message_id}")
                    print(f"   Payload: {json.dumps(payload, indent=2)}")

                    response = await self.handle_csms_request(action, payload)

                    if response:
                        response_msg = [3, message_id, response]
                        await self.websocket.send(json.dumps(response_msg))
                        print(f"{Colors.OKGREEN}→ Sent response{Colors.ENDC}")

        except websockets.exceptions.ConnectionClosed:
            print(f"\n{Colors.WARNING}Connection closed{Colors.ENDC}")

    async def handle_csms_request(self, action, payload):
        """Handle CSMS requests and return appropriate responses"""

        if action == "RequestStartTransaction":
            print(f"\n{Colors.BOLD}📱 Remote Start Transaction{Colors.ENDC}")
            self.transaction_id = f"TXN_{int(datetime.now().timestamp())}"
            return {
                "status": "Accepted",
                "transactionId": self.transaction_id,
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Transaction started"
                }
            }

        elif action == "RequestStopTransaction":
            print(f"\n{Colors.BOLD}🛑 Remote Stop Transaction{Colors.ENDC}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Transaction stopped"
                }
            }

        elif action == "Reset":
            print(f"\n{Colors.BOLD}🔄 Reset{Colors.ENDC}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Reset will be performed"
                }
            }

        elif action == "DataTransfer":
            print(f"\n{Colors.BOLD}📦 Data Transfer{Colors.ENDC}")
            return {
                "status": "Accepted",
                "data": "Response data from charge point",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Data transfer processed"
                }
            }

        elif action == "ClearCache":
            print(f"\n{Colors.BOLD}🗑️  Clear Cache{Colors.ENDC}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Authorization cache cleared"
                }
            }

        elif action == "ChangeAvailability":
            print(f"\n{Colors.BOLD}🔧 Change Availability{Colors.ENDC}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": f"Availability changed to {payload.get('operationalStatus')}"
                }
            }

        elif action == "UnlockConnector":
            print(f"\n{Colors.BOLD}🔓 Unlock Connector{Colors.ENDC}")
            return {
                "status": "Unlocked",
                "statusInfo": {
                    "reasonCode": "Unlocked",
                    "additionalInfo": "Connector unlocked"
                }
            }

        elif action == "GetBaseReport":
            print(f"\n{Colors.BOLD}📊 Get Base Report{Colors.ENDC}")
            print(f"  Request ID: {payload.get('requestId')}")
            print(f"  Report Base: {payload.get('reportBase')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Base report generation started"
                }
            }

        elif action == "GetReport":
            print(f"\n{Colors.BOLD}📈 Get Report{Colors.ENDC}")
            print(f"  Request ID: {payload.get('requestId')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Report generation started"
                }
            }

        elif action == "SetNetworkProfile":
            print(f"\n{Colors.BOLD}🌐 Set Network Profile{Colors.ENDC}")
            print(f"  Configuration Slot: {payload.get('configurationSlot')}")
            connection_data = payload.get('connectionData', {})
            if connection_data:
                print(f"  OCPP CSMS URL: {connection_data.get('ocppCsmsUrl')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Network profile configured"
                }
            }

        elif action == "GetChargingProfiles":
            print(f"\n{Colors.BOLD}📊 Get Charging Profiles Request{Colors.ENDC}")
            print(f"  Request ID: {payload.get('requestId')}")
            print(f"  EVSE ID: {payload.get('evseId')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Charging profiles will be reported"
                }
            }

        elif action == "ClearChargingProfile":
            print(f"\n{Colors.BOLD}🗑️  Clear Charging Profile Request{Colors.ENDC}")
            print(f"  Profile ID: {payload.get('chargingProfileId')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Charging profile cleared"
                }
            }

        elif action == "GetCompositeSchedule":
            print(f"\n{Colors.BOLD}📅 Get Composite Schedule Request{Colors.ENDC}")
            print(f"  Duration: {payload.get('duration')}")
            print(f"  EVSE ID: {payload.get('evseId')}")
            return {
                "status": "Accepted",
                "chargingSchedule": {
                    "evseId": payload.get('evseId'),
                    "duration": payload.get('duration'),
                    "chargingRateUnit": "A",
                    "chargingSchedulePeriod": []
                },
                "statusInfo": {
                    "reasonCode": "Accepted"
                }
            }

        elif action == "UpdateFirmware":
            print(f"\n{Colors.BOLD}⬆️  Update Firmware Request{Colors.ENDC}")
            print(f"  Request ID: {payload.get('requestId')}")
            firmware = payload.get('firmware', {})
            if firmware:
                print(f"  Location: {firmware.get('location')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Firmware update will be performed"
                }
            }

        elif action == "GetLog":
            print(f"\n{Colors.BOLD}📜 Get Log Request{Colors.ENDC}")
            print(f"  Request ID: {payload.get('requestId')}")
            print(f"  Log Type: {payload.get('logType')}")
            log_params = payload.get('log', {})
            if log_params:
                print(f"  Remote Location: {log_params.get('remoteLocation')}")
            return {
                "status": "Accepted",
                "filename": f"log_{payload.get('requestId')}.log",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Log upload will start"
                }
            }

        elif action == "CancelReservation":
            print(f"\n{Colors.BOLD}🚫 Cancel Reservation Request{Colors.ENDC}")
            print(f"  Reservation ID: {payload.get('reservationId')}")
            return {"status": "Accepted"}

        elif action == "ReserveNow":
            print(f"\n{Colors.BOLD}📅 Reserve Now Request{Colors.ENDC}")
            print(f"  ID: {payload.get('id')}")
            print(f"  Expiry DateTime: {payload.get('expiryDateTime')}")
            id_token = payload.get('idToken', {})
            if id_token:
                print(f"  ID Token: {id_token.get('idToken')} ({id_token.get('type')})")
            if payload.get('evseId'):
                print(f"  EVSE ID: {payload.get('evseId')}")
            return {"status": "Accepted"}

        elif action == "SendLocalList":
            print(f"\n{Colors.BOLD}📋 Send Local List Request{Colors.ENDC}")
            print(f"  Version Number: {payload.get('versionNumber')}")
            print(f"  Update Type: {payload.get('updateType')}")
            auth_list = payload.get('localAuthorizationList', [])
            print(f"  Authorization List: {len(auth_list)} entries")
            return {"status": "Accepted"}

        elif action == "GetLocalListVersion":
            print(f"\n{Colors.BOLD}📋 Get Local List Version Request{Colors.ENDC}")
            return {"versionNumber": 42}

        elif action == "GetTransactionStatus":
            print(f"\n{Colors.BOLD}💳 Get Transaction Status Request{Colors.ENDC}")
            transaction_id = payload.get('transactionId')
            if transaction_id:
                print(f"  Transaction ID: {transaction_id}")
            else:
                print(f"  Getting status for all ongoing transactions")
            return {
                "messagesInQueue": False,
                "ongoingIndicator": True
            }

        elif action == "SetChargingProfile":
            print(f"\n{Colors.BOLD}⚡ Set Charging Profile{Colors.ENDC}")
            charging_profile = payload.get('chargingProfile', {})
            if charging_profile:
                print(f"  Profile ID: {charging_profile.get('id')}")
                print(f"  Stack Level: {charging_profile.get('stackLevel')}")
                print(f"  Purpose: {charging_profile.get('chargingProfilePurpose')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Charging profile set"
                }
            }

        return {}

    async def run_demo(self):
        """Run the demo showing all CSMS operations"""
        print(f"\n{Colors.BOLD}{'='*70}{Colors.ENDC}")
        print(f"{Colors.BOLD}OCPP 2.0.1 CSMS Operations Demo{Colors.ENDC}")
        print(f"{Colors.BOLD}{'='*70}{Colors.ENDC}")
        print(f"Server: {self.server_url}")
        print(f"Charge Point ID: {self.charger_id}\n")

        try:
            async with websockets.connect(
                self.server_url,
                subprotocols=["ocpp2.0.1"]
            ) as websocket:
                self.websocket = websocket

                print(f"{Colors.OKGREEN}✓ Connected to server{Colors.ENDC}")

                # Send BootNotification
                boot_notification = [
                    2,
                    "boot-001",
                    "BootNotification",
                    {
                        "reason": "PowerUp",
                        "chargingStation": {
                            "model": "DemoCharger",
                            "vendorName": "DemoVendor"
                        }
                    }
                ]

                await websocket.send(json.dumps(boot_notification))
                print(f"{Colors.CYAN}→ Sent BootNotification{Colors.ENDC}")

                response = await websocket.recv()
                print(f"{Colors.OKGREEN}← Received BootNotification response{Colors.ENDC}")

                print(f"\n{Colors.BOLD}{'='*70}{Colors.ENDC}")
                print(f"{Colors.BOLD}Demo: All CSMS Operations{Colors.ENDC}")
                print(f"{Colors.BOLD}{'='*70}{Colors.ENDC}\n")

                print(f"This charge point is now ready to receive CSMS operations:")
                print(f"\n{Colors.CYAN}Available Operations:{Colors.ENDC}")
                print(f"  1. {Colors.BOLD}RequestStartTransaction{Colors.ENDC} - Start a charging session")
                print(f"  2. {Colors.BOLD}RequestStopTransaction{Colors.ENDC} - Stop a charging session")
                print(f"  3. {Colors.BOLD}Reset{Colors.ENDC} - Reset the charge point")
                print(f"  4. {Colors.BOLD}UnlockConnector{Colors.ENDC} - Unlock a stuck connector")
                print(f"  5. {Colors.BOLD}GetBaseReport{Colors.ENDC} - Request base report from charge point")
                print(f"  6. {Colors.BOLD}GetReport{Colors.ENDC} - Request custom report from charge point")
                print(f"  7. {Colors.BOLD}SetNetworkProfile{Colors.ENDC} - Configure network profile")
                print(f"  8. {Colors.BOLD}SetChargingProfile{Colors.ENDC} - Set charging profile")

                print(f"\n{Colors.CYAN}How to test:{Colors.ENDC}")
                print(f"  • Use Ocpp20TaskService in Java code")
                print(f"  • Or implement UI Operations page")
                print(f"  • Or use WebSocket client to send JSON-RPC messages")

                print(f"\n{Colors.CYAN}Example Java code:{Colors.ENDC}")
                print(f"  {Colors.BOLD}// Start transaction{Colors.ENDC}")
                print(f"  RequestStartTransactionTask task = new RequestStartTransactionTask(")
                print(f"      Arrays.asList(\"{self.charger_id}\"),")
                print(f"      1, \"USER_TOKEN\", \"ISO14443\"")
                print(f"  );")
                print(f"  taskService.executeTask(task);")

                print(f"\n{Colors.WARNING}Listening for CSMS operations (Ctrl+C to stop)...{Colors.ENDC}\n")

                # Listen for CSMS operations
                await self.handle_server_messages()

        except Exception as e:
            print(f"\n{Colors.FAIL}Error: {e}{Colors.ENDC}")

async def main():
    if len(sys.argv) > 1:
        charger_id = sys.argv[1]
    else:
        charger_id = "DEMO_CHARGER"

    server_url = f"ws://localhost:8080/steve/ocpp/v20/{charger_id}"

    demo = CSMSOperationsDemo(server_url, charger_id)
    await demo.run_demo()

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print(f"\n\n{Colors.WARNING}Demo stopped{Colors.ENDC}")