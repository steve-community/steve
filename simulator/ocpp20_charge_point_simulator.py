#!/usr/bin/env python3
import asyncio
import websockets
import json
from datetime import datetime

CHARGER_ID = "TEST_CP_001"
SERVER_URL = f"ws://localhost:8080/steve/ocpp/v20/{CHARGER_ID}"

async def handle_server_messages(websocket):
    try:
        async for message in websocket:
            data = json.loads(message)
            print(f"\n[{datetime.now().strftime('%H:%M:%S')}] ← Received from server: {json.dumps(data, indent=2)}")

            message_type = data[0]
            message_id = data[1]

            if message_type == 2:
                action = data[2]
                payload = data[3]

                print(f"[Server Call] Action: {action}, MessageId: {message_id}")

                if action == "RequestStartTransaction":
                    print(f"📱 Remote Start Transaction Request")
                    print(f"   EVSE ID: {payload.get('evseId')}")
                    print(f"   ID Token: {payload.get('idToken', {}).get('idToken')}")
                    print(f"   Token Type: {payload.get('idToken', {}).get('type')}")
                    print(f"   Remote Start ID: {payload.get('remoteStartId')}")

                    response = [
                        3,
                        message_id,
                        {
                            "status": "Accepted",
                            "transactionId": f"TXN_{int(datetime.now().timestamp())}",
                            "statusInfo": {
                                "reasonCode": "Accepted",
                                "additionalInfo": "Transaction will be started"
                            }
                        }
                    ]

                    await websocket.send(json.dumps(response))
                    print(f"✅ Sent acceptance response")

                elif action == "RequestStopTransaction":
                    print(f"🛑 Remote Stop Transaction Request")
                    print(f"   Transaction ID: {payload.get('transactionId')}")

                    response = [
                        3,
                        message_id,
                        {
                            "status": "Accepted",
                            "statusInfo": {
                                "reasonCode": "Accepted",
                                "additionalInfo": "Transaction will be stopped"
                            }
                        }
                    ]

                    await websocket.send(json.dumps(response))
                    print(f"✅ Sent acceptance response")

                elif action == "Reset":
                    print(f"🔄 Reset Request")
                    print(f"   Type: {payload.get('type', 'Immediate')}")
                    evse_id = payload.get('evseId')
                    if evse_id:
                        print(f"   EVSE ID: {evse_id}")

                    response = [
                        3,
                        message_id,
                        {
                            "status": "Accepted",
                            "statusInfo": {
                                "reasonCode": "Accepted",
                                "additionalInfo": "Reset will be performed"
                            }
                        }
                    ]

                    await websocket.send(json.dumps(response))
                    print(f"✅ Sent acceptance response")

                elif action == "UnlockConnector":
                    print(f"🔓 Unlock Connector Request")
                    print(f"   EVSE ID: {payload.get('evseId')}")
                    print(f"   Connector ID: {payload.get('connectorId')}")

                    response = [
                        3,
                        message_id,
                        {
                            "status": "Unlocked",
                            "statusInfo": {
                                "reasonCode": "Unlocked",
                                "additionalInfo": "Connector unlocked successfully"
                            }
                        }
                    ]

                    await websocket.send(json.dumps(response))
                    print(f"✅ Sent unlock response")

    except websockets.exceptions.ConnectionClosed:
        print("Connection closed by server")

async def simulate_charge_point():
    print(f"🔌 Connecting to: {SERVER_URL}")

    try:
        async with websockets.connect(SERVER_URL, subprotocols=["ocpp2.0.1"]) as websocket:
            print(f"✅ Connected successfully")

            boot_notification = [
                2,
                "boot-001",
                "BootNotification",
                {
                    "reason": "PowerUp",
                    "chargingStation": {
                        "model": "TestCharger",
                        "vendorName": "TestVendor"
                    }
                }
            ]

            await websocket.send(json.dumps(boot_notification))
            print(f"\n[{datetime.now().strftime('%H:%M:%S')}] → Sent BootNotification")

            response = await websocket.recv()
            data = json.loads(response)
            print(f"[{datetime.now().strftime('%H:%M:%S')}] ← Received: {json.dumps(data, indent=2)}")

            print("\n🎯 Charge point ready. Waiting for CSMS operations...")
            print("💡 You can now call RequestStartTransaction from SteVe's Operations page")
            print("   or use the Ocpp20TaskService programmatically")
            print("\nListening for messages (press Ctrl+C to stop)...")

            await handle_server_messages(websocket)

    except websockets.exceptions.InvalidStatusCode as e:
        print(f"❌ Connection failed: {e}")
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    try:
        asyncio.run(simulate_charge_point())
    except KeyboardInterrupt:
        print("\n\n👋 Shutting down simulator")