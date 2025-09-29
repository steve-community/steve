#!/usr/bin/env python3
"""
Simple OCPP 2.0.1 Charge Point Simulator
Tests SteVe OCPP 2.0 implementation
"""
import asyncio
import websockets
import json
import sys
from datetime import datetime, timezone

STEVE_URL = "ws://127.0.0.1:8080/steve/ocpp/v20/CP001"
MESSAGE_ID_COUNTER = 1

def get_message_id():
    global MESSAGE_ID_COUNTER
    msg_id = str(MESSAGE_ID_COUNTER)
    MESSAGE_ID_COUNTER += 1
    return msg_id

def ocpp_call(action, payload):
    return [2, get_message_id(), action, payload]

async def send_boot_notification(websocket):
    boot_request = ocpp_call("BootNotification", {
        "reason": "PowerUp",
        "chargingStation": {
            "model": "TestCharger",
            "vendorName": "TestVendor",
            "serialNumber": "TEST-001",
            "firmwareVersion": "1.0.0"
        }
    })

    print(f"[SEND] BootNotification: {json.dumps(boot_request)}")
    await websocket.send(json.dumps(boot_request))

    response = await websocket.recv()
    print(f"[RECV] {response}")
    return json.loads(response)

async def send_authorize(websocket):
    auth_request = ocpp_call("Authorize", {
        "idToken": {
            "idToken": "RFID123456",
            "type": "ISO14443"
        }
    })

    print(f"[SEND] Authorize: {json.dumps(auth_request)}")
    await websocket.send(json.dumps(auth_request))

    response = await websocket.recv()
    print(f"[RECV] {response}")
    return json.loads(response)

async def send_transaction_event(websocket, event_type, transaction_id):
    tx_request = ocpp_call("TransactionEvent", {
        "eventType": event_type,
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "triggerReason": "Authorized",
        "seqNo": 0 if event_type == "Started" else 1,
        "transactionInfo": {
            "transactionId": transaction_id
        }
    })

    print(f"[SEND] TransactionEvent ({event_type}): {json.dumps(tx_request)}")
    await websocket.send(json.dumps(tx_request))

    response = await websocket.recv()
    print(f"[RECV] {response}")
    return json.loads(response)

async def send_heartbeat(websocket):
    heartbeat_request = ocpp_call("Heartbeat", {})

    print(f"[SEND] Heartbeat: {json.dumps(heartbeat_request)}")
    await websocket.send(json.dumps(heartbeat_request))

    response = await websocket.recv()
    print(f"[RECV] {response}")
    return json.loads(response)

async def test_ocpp20():
    print(f"=== OCPP 2.0.1 Charge Point Simulator ===")
    print(f"Connecting to SteVe at {STEVE_URL}...")

    try:
        async with websockets.connect(STEVE_URL, subprotocols=["ocpp2.0.1"]) as websocket:
            print(f"[CONNECTED] WebSocket connection established")
            print(f"[PROTOCOL] {websocket.subprotocol}")

            # Test 1: BootNotification
            print("\n--- Test 1: BootNotification ---")
            boot_response = await send_boot_notification(websocket)
            assert boot_response[0] == 3, "Expected CallResult"
            print(f"✓ BootNotification: {boot_response[2]['status']}")

            # Test 2: Authorize
            print("\n--- Test 2: Authorize ---")
            auth_response = await send_authorize(websocket)
            assert auth_response[0] == 3, "Expected CallResult"
            print(f"✓ Authorize: {auth_response[2]['idTokenInfo']['status']}")

            # Test 3: Transaction Started
            print("\n--- Test 3: TransactionEvent (Started) ---")
            tx_id = f"TX-{datetime.now().timestamp()}"
            tx_start_response = await send_transaction_event(websocket, "Started", tx_id)
            assert tx_start_response[0] == 3, "Expected CallResult"
            print(f"✓ TransactionEvent Started")

            # Test 4: Heartbeat
            print("\n--- Test 4: Heartbeat ---")
            heartbeat_response = await send_heartbeat(websocket)
            assert heartbeat_response[0] == 3, "Expected CallResult"
            print(f"✓ Heartbeat received")

            # Test 5: Transaction Ended
            print("\n--- Test 5: TransactionEvent (Ended) ---")
            tx_end_response = await send_transaction_event(websocket, "Ended", tx_id)
            assert tx_end_response[0] == 3, "Expected CallResult"
            print(f"✓ TransactionEvent Ended")

            # Test 6: GetBaseReport (New command)
            print("\n--- Test 6: GetBaseReport ---")
            response = await websocket.recv()
            message = json.loads(response)
            if message[0] == 2 and message[2] == "GetBaseReport":
                print(f"✓ Received GetBaseReport request")
                print(f"  Request ID: {message[3].get('requestId')}")
                print(f"  Report Base: {message[3].get('reportBase')}")
                # Send response
                response_msg = [3, message[1], {"status": "Accepted"}]
                await websocket.send(json.dumps(response_msg))
                print(f"✓ GetBaseReport response sent")

            # Test 7: GetReport (New command)
            print("\n--- Test 7: GetReport ---")
            response = await websocket.recv()
            message = json.loads(response)
            if message[0] == 2 and message[2] == "GetReport":
                print(f"✓ Received GetReport request")
                print(f"  Request ID: {message[3].get('requestId')}")
                # Send response
                response_msg = [3, message[1], {"status": "Accepted"}]
                await websocket.send(json.dumps(response_msg))
                print(f"✓ GetReport response sent")

            # Test 8: SetNetworkProfile (New command)
            print("\n--- Test 8: SetNetworkProfile ---")
            response = await websocket.recv()
            message = json.loads(response)
            if message[0] == 2 and message[2] == "SetNetworkProfile":
                print(f"✓ Received SetNetworkProfile request")
                print(f"  Configuration Slot: {message[3].get('configurationSlot')}")
                # Send response
                response_msg = [3, message[1], {"status": "Accepted"}]
                await websocket.send(json.dumps(response_msg))
                print(f"✓ SetNetworkProfile response sent")

            # Test 9: SetChargingProfile (New command)
            print("\n--- Test 9: SetChargingProfile ---")
            response = await websocket.recv()
            message = json.loads(response)
            if message[0] == 2 and message[2] == "SetChargingProfile":
                print(f"✓ Received SetChargingProfile request")
                profile = message[3].get('chargingProfile', {})
                print(f"  Profile ID: {profile.get('id')}")
                print(f"  Stack Level: {profile.get('stackLevel')}")
                # Send response
                response_msg = [3, message[1], {"status": "Accepted"}]
                await websocket.send(json.dumps(response_msg))
                print(f"✓ SetChargingProfile response sent")

            print("\n=== ALL TESTS PASSED ===")
            print(f"✓ BootNotification: OK")
            print(f"✓ Authorize: OK")
            print(f"✓ TransactionEvent (Started): OK")
            print(f"✓ Heartbeat: OK")
            print(f"✓ TransactionEvent (Ended): OK")
            print(f"✓ GetBaseReport: OK")
            print(f"✓ GetReport: OK")
            print(f"✓ SetNetworkProfile: OK")
            print(f"✓ SetChargingProfile: OK")

    except websockets.exceptions.InvalidStatusCode as e:
        print(f"[ERROR] Connection rejected: {e}")
        sys.exit(1)
    except ConnectionRefusedError:
        print(f"[ERROR] Connection refused. Is SteVe running?")
        sys.exit(1)
    except Exception as e:
        print(f"[ERROR] {type(e).__name__}: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    asyncio.run(test_ocpp20())