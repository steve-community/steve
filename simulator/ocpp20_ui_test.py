#!/usr/bin/env python3
"""
OCPP 2.0 Complete Test Suite
Tests all OCPP 2.0 operations (both CP->CSMS and CSMS->CP) for accessibility
Author: Claude Code
"""

import sys
import time
from urllib.parse import urljoin

try:
    import requests  # type: ignore
except ModuleNotFoundError:  # pragma: no cover - optional runtime dependency
    # Provide a tiny fallback so the test suite can run without external deps
    import urllib.error
    import urllib.parse
    import urllib.request
    from http.cookiejar import CookieJar

    class _NoRedirectHandler(urllib.request.HTTPRedirectHandler):
        def redirect_request(self, req, fp, code, msg, headers, newurl):
            return None

    class _SimpleResponse:
        def __init__(self, status_code, headers, url, content):
            self.status_code = status_code
            self.headers = dict(headers.items()) if headers else {}
            self.url = url
            self._content = content or b""

        @property
        def text(self):
            return self._content.decode("utf-8", errors="replace")

    class _SimpleSession:
        def __init__(self):
            self.headers = {}
            self._cookie_jar = CookieJar()
            self._opener = urllib.request.build_opener(
                urllib.request.HTTPCookieProcessor(self._cookie_jar)
            )

        def _make_request(self, url, method, data=None, allow_redirects=True, timeout=None):
            request_headers = dict(self.headers)
            request_data = data

            if isinstance(data, dict):
                request_data = urllib.parse.urlencode(data).encode()
                request_headers.setdefault("Content-Type", "application/x-www-form-urlencoded")
            elif isinstance(data, str):
                request_data = data.encode()

            req = urllib.request.Request(url, data=request_data, headers=request_headers, method=method)

            opener = self._opener
            if not allow_redirects:
                opener = urllib.request.build_opener(
                    urllib.request.HTTPCookieProcessor(self._cookie_jar),
                    _NoRedirectHandler(),
                )

            try:
                response = opener.open(req, timeout=timeout)
                content = response.read()
                return _SimpleResponse(response.getcode(), response.headers, response.geturl(), content)
            except urllib.error.HTTPError as exc:
                content = exc.read()
                return _SimpleResponse(exc.code, exc.headers, exc.geturl(), content)
            except urllib.error.URLError as exc:  # bubble up a familiar error message
                raise ConnectionError(str(exc)) from exc

        def get(self, url, allow_redirects=True, timeout=None):
            return self._make_request(url, "GET", allow_redirects=allow_redirects, timeout=timeout)

        def post(self, url, data=None, allow_redirects=True, timeout=None):
            return self._make_request(url, "POST", data=data, allow_redirects=allow_redirects, timeout=timeout)

    class _RequestsModule:
        Session = _SimpleSession

        @staticmethod
        def get(url, **kwargs):
            return _SimpleSession().get(url, **kwargs)

        @staticmethod
        def post(url, **kwargs):
            return _SimpleSession().post(url, **kwargs)

    requests = _RequestsModule()  # type: ignore

# Configuration
BASE_URL = "http://localhost:8080/steve/"  # Add trailing slash for proper urljoin behavior
USERNAME = "admin"
PASSWORD = "1234"

# CSMS->CP operations (Central System initiated to Charge Point)
# These operations should have UI forms for operators to trigger
CSMS_TO_CP_OPERATIONS = [
    "CancelReservation", "CertificateSigned", "ChangeAvailability", "ClearCache",
    "ClearChargingProfile", "ClearDisplayMessage", "ClearVariableMonitoring",
    "CostUpdated", "CustomerInformation", "DataTransfer", "DeleteCertificate",
    "Get15118EVCertificate", "GetBaseReport", "GetCertificateStatus", "GetChargingProfiles",
    "GetCompositeSchedule", "GetDisplayMessages", "GetInstalledCertificateIds",
    "GetLocalListVersion", "GetLog", "GetMonitoringReport", "GetReport",
    "GetTransactionStatus", "GetVariables", "InstallCertificate", "PublishFirmware",
    "RequestStartTransaction", "RequestStopTransaction", "ReserveNow", "Reset",
    "SendLocalList", "SetChargingProfile", "SetDisplayMessage", "SetMonitoringBase",
    "SetMonitoringLevel", "SetNetworkProfile", "SetVariableMonitoring", "SetVariables",
    "TriggerMessage", "UnlockConnector", "UnpublishFirmware", "UpdateFirmware"
]

# CP->CSMS operations (Charge Point initiated to Central System)
# These are handled by the server and may have viewing pages but typically not forms
CP_TO_CSMS_OPERATIONS = [
    "Authorize", "BootNotification", "ClearedChargingLimit", "DataTransfer",
    "FirmwareStatusNotification", "Get15118EVCertificate", "GetCertificateStatus",
    "Heartbeat", "LogStatusNotification", "MeterValues", "NotifyChargingLimit",
    "NotifyCustomerInformation", "NotifyDisplayMessages", "NotifyEvent",
    "NotifyEVChargingNeeds", "NotifyEVChargingSchedule", "NotifyMonitoringReport",
    "NotifyReport", "PublishFirmwareStatusNotification", "ReportChargingProfiles",
    "ReservationStatusUpdate", "SecurityEventNotification", "SignCertificate",
    "StatusNotification", "TransactionEvent"
]

# All OCPP 2.0 operations combined
ALL_OCPP20_OPERATIONS = sorted(list(set(CSMS_TO_CP_OPERATIONS + CP_TO_CSMS_OPERATIONS)))

class SteveUITester:
    def __init__(self):
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        })
        self.authenticated = False

    def login(self):
        """Authenticate with SteVe using admin credentials"""
        # First try to access the base URL to check server connectivity
        try:
            base_response = self.session.get(BASE_URL)
            print(f"📡 Base URL ({BASE_URL}) returned: {base_response.status_code}")
        except Exception as e:
            print(f"❌ Cannot connect to base URL: {e}")
            return False

        signin_url = urljoin(BASE_URL, "manager/signin")  # Don't use leading slash with urljoin
        print(f"🔐 Attempting to access login page: {signin_url}")

        # Get login page to extract CSRF token
        try:
            response = self.session.get(signin_url)
            print(f"📄 Login page request returned: {response.status_code}")
            if response.status_code == 404:
                print(f"❌ 404 NOT FOUND: {signin_url}")
                print(f"   Response URL: {response.url}")
                print(f"   Content preview: {response.text[:200]}...")
                return False
            elif response.status_code != 200:
                print(f"❌ Failed to access login page: {response.status_code}")
                print(f"   Response URL: {response.url}")
                return False

            # Extract CSRF token from the response
            import re
            csrf_match = re.search(r'<input type="hidden" name="_csrf" value="([^"]+)"', response.text)
            if not csrf_match:
                print("❌ Could not extract CSRF token from login page")
                print(f"   Page title: {re.search(r'<title>(.*?)</title>', response.text, re.IGNORECASE)}")
                return False

            csrf_token = csrf_match.group(1)
            print(f"🔑 Extracted CSRF token: {csrf_token[:20]}...")
        except Exception as e:
            print(f"❌ Connection error accessing login page: {e}")
            return False

        # Perform login with CSRF token
        login_data = {
            'username': USERNAME,
            'password': PASSWORD,
            '_csrf': csrf_token
        }

        try:
            login_url = signin_url  # Use same URL for GET and POST
            print(f"🔐 Posting login credentials to: {login_url}")
            response = self.session.post(login_url, data=login_data, allow_redirects=False)
            print(f"📤 Login POST returned: {response.status_code}")

            # Check if login was successful by looking for redirect
            if response.status_code == 302:
                location = response.headers.get('Location', '')
                print(f"↩️  Redirected to: {location}")
                if 'signin' not in location.lower() and 'error' not in location.lower():
                    # Success! We got redirected to the main page, which means authentication worked
                    print("✅ Successfully authenticated (redirected to main page)")
                    self.authenticated = True
                    return True
                else:
                    print(f"❌ Authentication failed: redirected back to signin or error page")
                    return False
            else:
                print(f"❌ Authentication failed: status={response.status_code}")
                if response.status_code == 404:
                    print(f"   404 NOT FOUND: {login_url}")
                return False
        except Exception as e:
            print(f"❌ Login error: {e}")
            return False

    def test_operation(self, operation):
        """Test a single OCPP 2.0 operation page"""
        url = urljoin(BASE_URL, f"manager/operations/v2.0/{operation}")  # Don't use leading slash

        try:
            response = self.session.get(url, allow_redirects=True)

            if response.status_code == 200:
                # Check if we got redirected to signin page
                if 'signin' in response.url.lower() or 'Sign In' in response.text:
                    return "🔐 AUTH REQUIRED", 302
                # Check if it's a valid OCPP 2.0 operation page
                elif 'OCPP v2.0' in response.text or f'{operation}' in response.text:
                    return "✅ OK", response.status_code
                # Check if it's a general error page
                elif 'error' in response.text.lower():
                    return "⚠️  ERROR PAGE", response.status_code
                else:
                    return "✅ OK", response.status_code
            elif response.status_code == 403:
                return "❌ 403 FORBIDDEN", response.status_code
            elif response.status_code == 404:
                print(f"   🔍 404 Details for {operation}:")
                print(f"      Requested URL: {url}")
                print(f"      Final URL: {response.url}")
                print(f"      Content preview: {response.text[:300]}...")
                return "⚠️  404 NOT FOUND", response.status_code
            elif response.status_code == 302:
                # Check redirect location
                location = response.headers.get('Location', '')
                if 'signin' in location:
                    return "🔐 AUTH REQUIRED", response.status_code
                else:
                    return f"↩️  REDIRECT to {location}", response.status_code
            else:
                return f"❓ {response.status_code}", response.status_code

        except Exception as e:
            return f"💥 ERROR: {str(e)}", 0

    def test_all_operations(self, mode="all"):
        """Test OCPP 2.0 operations based on mode

        Args:
            mode: "all", "csms_to_cp", or "cp_to_csms"
        """
        if not self.authenticated:
            print("❌ Not authenticated. Please login first.")
            return False

        if mode == "csms_to_cp":
            operations = CSMS_TO_CP_OPERATIONS
            description = "CSMS→CP (Central System initiated)"
        elif mode == "cp_to_csms":
            operations = CP_TO_CSMS_OPERATIONS
            description = "CP→CSMS (Charge Point initiated)"
        else:  # all
            operations = ALL_OCPP20_OPERATIONS
            description = "ALL OCPP 2.0"

        print(f"\n🧪 Testing {len(operations)} {description} operations...")
        print("=" * 80)

        results = {
            'success': [],
            'forbidden': [],
            'not_found': [],
            'errors': [],
            'csms_to_cp': [],
            'cp_to_csms': []
        }

        for operation in sorted(operations):
            status, code = self.test_operation(operation)
            url = urljoin(BASE_URL, f"/manager/operations/v2.0/{operation}")

            # Determine operation type
            op_type = ""
            if operation in CSMS_TO_CP_OPERATIONS:
                op_type = "CSMS→CP"
                results['csms_to_cp'].append((operation, code))
            if operation in CP_TO_CSMS_OPERATIONS:
                op_type = "CP→CSMS" if op_type == "" else "BOTH"
                results['cp_to_csms'].append((operation, code))

            print(f"{status:<20} {operation:<30} {op_type:<10} {url}")

            if code == 200:
                results['success'].append(operation)
            elif code == 403:
                results['forbidden'].append(operation)
            elif code == 404:
                results['not_found'].append(operation)
            elif code == 0:
                results['errors'].append(operation)

            # Small delay to avoid overwhelming the server
            time.sleep(0.1)

        return results

    def print_summary(self, results, mode="all"):
        """Print test results summary"""
        if mode == "csms_to_cp":
            total = len(CSMS_TO_CP_OPERATIONS)
        elif mode == "cp_to_csms":
            total = len(CP_TO_CSMS_OPERATIONS)
        else:
            total = len(ALL_OCPP20_OPERATIONS)

        success_count = len(results['success'])
        forbidden_count = len(results['forbidden'])
        not_found_count = len(results['not_found'])
        error_count = len(results['errors'])

        print("\n" + "=" * 80)
        print("📊 TEST SUMMARY")
        print("=" * 80)
        print(f"Total operations tested: {total}")
        print(f"✅ Successful (200):     {success_count}")
        print(f"❌ Forbidden (403):      {forbidden_count}")
        print(f"⚠️  Not Found (404):      {not_found_count}")
        print(f"💥 Errors:               {error_count}")

        success_rate = (success_count / total) * 100 if total > 0 else 0
        print(f"\n🎯 Success Rate: {success_rate:.1f}%")

        # Show breakdown by type if testing all
        if mode == "all":
            print("\n📈 Breakdown by Direction:")
            csms_success = sum(1 for op, code in results['csms_to_cp'] if code == 200)
            cp_success = sum(1 for op, code in results['cp_to_csms'] if code == 200)
            print(f"   CSMS→CP: {csms_success}/{len(CSMS_TO_CP_OPERATIONS)} ({csms_success/len(CSMS_TO_CP_OPERATIONS)*100:.1f}%)")
            print(f"   CP→CSMS: {cp_success}/{len(CP_TO_CSMS_OPERATIONS)} ({cp_success/len(CP_TO_CSMS_OPERATIONS)*100:.1f}%)")

        if results['forbidden']:
            print(f"\n❌ Operations with 403 FORBIDDEN errors:")
            for op in results['forbidden']:
                print(f"   - {op}")

        if results['not_found']:
            print(f"\n⚠️  Operations with 404 NOT FOUND errors:")
            for op in results['not_found']:
                print(f"   - {op}")

        if results['errors']:
            print(f"\n💥 Operations with connection errors:")
            for op in results['errors']:
                print(f"   - {op}")

        return success_rate >= 95.0  # Consider test passed if 95%+ success rate

    def test_specific_operations(self, operations):
        """Test specific operations only"""
        if not self.authenticated:
            print("❌ Not authenticated. Please login first.")
            return False

        print(f"\n🎯 Testing {len(operations)} specific operations...")
        print("=" * 80)

        for operation in operations:
            status, code = self.test_operation(operation)
            url = urljoin(BASE_URL, f"/manager/operations/v2.0/{operation}")
            print(f"{status:<20} {operation:<30} {url}")
            time.sleep(0.1)

def main():
    """Main test runner for OCPP 2.0 operations"""
    print("🚀 SteVe OCPP 2.0 Complete Test Suite")
    print("=" * 80)

    tester = SteveUITester()

    # Check if server is running
    try:
        response = requests.get(BASE_URL, timeout=5)
        print(f"✅ Server is running at {BASE_URL}")
    except Exception as e:
        print(f"❌ Cannot connect to server at {BASE_URL}: {e}")
        sys.exit(1)

    # Authenticate
    if not tester.login():
        sys.exit(1)

    # Parse command line arguments
    if len(sys.argv) > 1:
        if sys.argv[1] == "--quick":
            # Test only operations that previously had 403 errors
            problem_operations = ["CancelReservation", "ClearDisplayMessage", "GetMonitoringReport",
                                "SetDisplayMessage", "SetVariableMonitoring"]
            print("🏃‍♂️ Running quick test on previously problematic operations")
            tester.test_specific_operations(problem_operations)
        elif sys.argv[1] == "--all":
            # Test ALL operations (both CP->CSMS and CSMS->CP)
            print("🔍 Testing ALL OCPP 2.0 operations (both CP→CSMS and CSMS→CP)")
            results = tester.test_all_operations(mode="all")
            if results:
                passed = tester.print_summary(results, mode="all")
                sys.exit(0 if passed else 1)
            else:
                sys.exit(1)
        elif sys.argv[1] == "--cp-to-csms":
            # Test only CP->CSMS operations
            print("🎯 Testing CP→CSMS operations only")
            results = tester.test_all_operations(mode="cp_to_csms")
            if results:
                passed = tester.print_summary(results, mode="cp_to_csms")
                sys.exit(0 if passed else 1)
            else:
                sys.exit(1)
        elif sys.argv[1].startswith("--test="):
            # Test specific operation
            operation = sys.argv[1].split("=", 1)[1]
            print(f"🎯 Testing single operation: {operation}")
            tester.test_specific_operations([operation])
        elif sys.argv[1] == "--help":
            print("\nUsage: python3 ocpp20_ui_test.py [OPTIONS]")
            print("\nOptions:")
            print("  (no args)        Test only CSMS→CP operations (default)")
            print("  --all            Test ALL OCPP 2.0 operations (both directions)")
            print("  --cp-to-csms     Test only CP→CSMS operations")
            print("  --quick          Test problematic operations only")
            print("  --test=OpName    Test specific operation")
            print("  --help           Show this help message")
            print("\nOperations counts:")
            print(f"  CSMS→CP: {len(CSMS_TO_CP_OPERATIONS)} operations")
            print(f"  CP→CSMS: {len(CP_TO_CSMS_OPERATIONS)} operations")
            print(f"  Total unique: {len(ALL_OCPP20_OPERATIONS)} operations")
            sys.exit(0)
        else:
            print("Invalid option. Use --help for usage information.")
            sys.exit(1)
    else:
        # DEFAULT: Test only CSMS→CP operations (as requested)
        print("🎯 Testing CSMS→CP operations only (use --all for both directions)")
        results = tester.test_all_operations(mode="csms_to_cp")
        if results:
            passed = tester.print_summary(results, mode="csms_to_cp")
            sys.exit(0 if passed else 1)
        else:
            sys.exit(1)

if __name__ == "__main__":
    main()
