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

        elif action == "UnlockConnector":
            print(f"\n{Colors.BOLD}🔓 Unlock Connector{Colors.ENDC}")
            return {
                "status": "Unlocked",
                "statusInfo": {
                    "reasonCode": "Unlocked",
                    "additionalInfo": "Connector unlocked"
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