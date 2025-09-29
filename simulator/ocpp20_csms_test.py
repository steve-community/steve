#!/usr/bin/env python3
"""
OCPP 2.0.1 CSMS Operations Test
Tests CSMS→CP operations (RequestStartTransaction, Reset, etc.)
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

class OCPP20CSMSTest:
    def __init__(self, server_url, charger_id):
        self.server_url = server_url
        self.charger_id = charger_id
        self.websocket = None
        self.pending_responses = {}

    async def handle_server_messages(self):
        try:
            async for message in self.websocket:
                data = json.loads(message)
                message_type = data[0]
                message_id = data[1]

                if message_type == 2:
                    action = data[2]
                    payload = data[3]

                    print(f"\n{Colors.CYAN}← Server Request{Colors.ENDC}")
                    print(f"  Action: {Colors.BOLD}{action}{Colors.ENDC}")
                    print(f"  Message ID: {message_id}")
                    print(f"  Payload: {json.dumps(payload, indent=2)}")

                    response = await self.handle_csms_request(action, payload, message_id)

                    if response:
                        response_msg = [3, message_id, response]
                        await self.websocket.send(json.dumps(response_msg))
                        print(f"{Colors.OKGREEN}→ Sent response{Colors.ENDC}")

                elif message_type == 3:
                    print(f"\n{Colors.OKGREEN}← Server Response (CallResult){Colors.ENDC}")
                    print(f"  Message ID: {message_id}")
                    print(f"  Payload: {json.dumps(data[2], indent=2)}")

        except websockets.exceptions.ConnectionClosed:
            print(f"\n{Colors.WARNING}Connection closed{Colors.ENDC}")

    async def handle_csms_request(self, action, payload, message_id):
        if action == "RequestStartTransaction":
            print(f"\n{Colors.BOLD}📱 Remote Start Transaction Request{Colors.ENDC}")
            print(f"  EVSE ID: {payload.get('evseId')}")
            print(f"  ID Token: {payload.get('idToken', {}).get('idToken')}")
            print(f"  Token Type: {payload.get('idToken', {}).get('type')}")
            print(f"  Remote Start ID: {payload.get('remoteStartId')}")

            return {
                "status": "Accepted",
                "transactionId": f"TXN_{int(datetime.now().timestamp())}",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Transaction started successfully"
                }
            }

        elif action == "RequestStopTransaction":
            print(f"\n{Colors.BOLD}🛑 Remote Stop Transaction Request{Colors.ENDC}")
            print(f"  Transaction ID: {payload.get('transactionId')}")

            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Transaction will be stopped"
                }
            }

        elif action == "Reset":
            print(f"\n{Colors.BOLD}🔄 Reset Request{Colors.ENDC}")
            print(f"  Type: {payload.get('type', 'Immediate')}")
            evse_id = payload.get('evseId')
            if evse_id:
                print(f"  EVSE ID: {evse_id}")

            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Reset will be performed"
                }
            }

        elif action == "UnlockConnector":
            print(f"\n{Colors.BOLD}🔓 Unlock Connector Request{Colors.ENDC}")
            print(f"  EVSE ID: {payload.get('evseId')}")
            print(f"  Connector ID: {payload.get('connectorId')}")

            return {
                "status": "Unlocked",
                "statusInfo": {
                    "reasonCode": "Unlocked",
                    "additionalInfo": "Connector unlocked successfully"
                }
            }

        elif action == "GetVariables":
            print(f"\n{Colors.BOLD}📊 Get Variables Request{Colors.ENDC}")
            return {
                "getVariableResult": []
            }

        elif action == "GetBaseReport":
            print(f"\n{Colors.BOLD}📊 Get Base Report Request{Colors.ENDC}")
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
            print(f"\n{Colors.BOLD}📈 Get Report Request{Colors.ENDC}")
            print(f"  Request ID: {payload.get('requestId')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Report generation started"
                }
            }

        elif action == "SetNetworkProfile":
            print(f"\n{Colors.BOLD}🌐 Set Network Profile Request{Colors.ENDC}")
            print(f"  Configuration Slot: {payload.get('configurationSlot')}")
            connection_data = payload.get('connectionData', {})
            if connection_data:
                print(f"  OCPP Version: {connection_data.get('ocppVersion')}")
                print(f"  OCPP CSMS URL: {connection_data.get('ocppCsmsUrl')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Network profile configured"
                }
            }

        elif action == "SetChargingProfile":
            print(f"\n{Colors.BOLD}⚡ Set Charging Profile Request{Colors.ENDC}")
            print(f"  EVSE ID: {payload.get('evseId')}")
            charging_profile = payload.get('chargingProfile', {})
            if charging_profile:
                print(f"  Profile ID: {charging_profile.get('id')}")
                print(f"  Stack Level: {charging_profile.get('stackLevel')}")
                print(f"  Purpose: {charging_profile.get('chargingProfilePurpose')}")
            return {
                "status": "Accepted",
                "statusInfo": {
                    "reasonCode": "Accepted",
                    "additionalInfo": "Charging profile set successfully"
                }
            }

        else:
            print(f"\n{Colors.WARNING}⚠ Unsupported action: {action}{Colors.ENDC}")
            return {}

    async def run(self):
        print(f"\n{Colors.BOLD}{'='*60}{Colors.ENDC}")
        print(f"{Colors.BOLD}OCPP 2.0.1 CSMS Operations Test{Colors.ENDC}")
        print(f"{Colors.BOLD}{'='*60}{Colors.ENDC}")
        print(f"Server: {self.server_url}")
        print(f"Charge Point ID: {self.charger_id}\n")

        try:
            async with websockets.connect(
                self.server_url,
                subprotocols=["ocpp2.0.1"]
            ) as websocket:
                self.websocket = websocket

                print(f"{Colors.OKGREEN}✓ Connected to server{Colors.ENDC}")

                boot_notification = [
                    2,
                    "boot-001",
                    "BootNotification",
                    {
                        "reason": "PowerUp",
                        "chargingStation": {
                            "model": "CSMSTestCharger",
                            "vendorName": "TestVendor"
                        }
                    }
                ]

                await websocket.send(json.dumps(boot_notification))
                print(f"{Colors.CYAN}→ Sent BootNotification{Colors.ENDC}")

                response = await websocket.recv()
                print(f"{Colors.OKGREEN}← Received BootNotification response{Colors.ENDC}")

                print(f"\n{Colors.BOLD}🎯 Ready for CSMS operations{Colors.ENDC}")
                print(f"  You can now:")
                print(f"  1. Use SteVe UI Operations page")
                print(f"  2. Use Ocpp20TaskService programmatically")
                print(f"  3. Send WebSocket messages directly\n")
                print(f"Listening for CSMS requests (Ctrl+C to stop)...\n")

                await self.handle_server_messages()

        except Exception as e:
            print(f"\n{Colors.FAIL}Error: {e}{Colors.ENDC}")

async def main():
    if len(sys.argv) > 1:
        charger_id = sys.argv[1]
    else:
        charger_id = "TEST_CP_CSMS"

    server_url = f"ws://localhost:8080/steve/ocpp/v20/{charger_id}"

    tester = OCPP20CSMSTest(server_url, charger_id)
    await tester.run()

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print(f"\n\n{Colors.WARNING}Stopped by user{Colors.ENDC}")