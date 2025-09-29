# OCPP 2.0.1 CSMS Commands Implementation Roadmap

## Already Implemented ✅ (10 commands)
1. ✅ RequestStartTransaction - Start remote transaction
2. ✅ RequestStopTransaction - Stop remote transaction
3. ✅ Reset - Reset charge point
4. ✅ UnlockConnector - Unlock connector
5. ✅ GetVariables - Get device model variables
6. ✅ SetVariables - Set device model variables
7. ✅ TriggerMessage - Trigger specific message
8. ✅ ChangeAvailability - Change EVSE availability
9. ✅ ClearCache - Clear authorization cache
10. ✅ DataTransfer - Vendor-specific data exchange

## Priority 1: Essential Operations (11 remaining)

### ✅ 1. ChangeAvailability - COMPLETED
- **Status**: Implemented and tested
- **Files created**:
  - Task: `ChangeAvailabilityTask.java`
  - Controller: Updated `Ocpp20Controller.java`
  - JSP View: `changeAvailability.jsp`
  - DTO: `ChangeAvailabilityParams.java`
  - Simulator: Updated `ocpp20_charge_point_simulator.py` and `test_csms_all_operations.py`

### ✅ 2. ClearCache - COMPLETED
- **Status**: Implemented and tested
- **Files created**:
  - Task: `ClearCacheTask.java`
  - Controller: Updated `Ocpp20Controller.java`
  - JSP View: `clearCache.jsp`
  - DTO: `ClearCacheParams.java`
  - Simulator: Updated both simulators

### 3. GetBaseReport
- **Purpose**: Get base device model report
- **Files needed**:
  - Task: `GetBaseReportTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `getBaseReport.jsp`
  - DTO: `GetBaseReportForm.java`

### 4. GetReport
- **Purpose**: Get detailed device model report
- **Files needed**:
  - Task: `GetReportTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `getReport.jsp`
  - DTO: `GetReportForm.java`

### 5. SetChargingProfile
- **Purpose**: Set smart charging profile
- **Files needed**:
  - Task: `SetChargingProfileTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `setChargingProfile.jsp` (complex form)
  - DTO: `SetChargingProfileForm.java`

### 6. GetChargingProfiles
- **Purpose**: Get installed charging profiles
- **Files needed**:
  - Task: `GetChargingProfilesTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `getChargingProfiles.jsp`
  - DTO: `GetChargingProfilesForm.java`

### 7. ClearChargingProfile
- **Purpose**: Remove charging profiles
- **Files needed**:
  - Task: `ClearChargingProfileTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `clearChargingProfile.jsp`
  - DTO: `ClearChargingProfileForm.java`

### 8. GetCompositeSchedule
- **Purpose**: Calculate composite charging schedule
- **Files needed**:
  - Task: `GetCompositeScheduleTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `getCompositeSchedule.jsp`
  - DTO: `GetCompositeScheduleForm.java`

### 9. UpdateFirmware
- **Purpose**: Update charge point firmware
- **Files needed**:
  - Task: `UpdateFirmwareTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `updateFirmware.jsp`
  - DTO: `UpdateFirmwareForm.java`

### 10. GetLog
- **Purpose**: Retrieve diagnostics/security logs
- **Files needed**:
  - Task: `GetLogTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `getLog.jsp`
  - DTO: `GetLogForm.java`

### 11. SetNetworkProfile
- **Purpose**: Configure network connection
- **Files needed**:
  - Task: `SetNetworkProfileTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `setNetworkProfile.jsp`
  - DTO: `SetNetworkProfileForm.java`

### ✅ 12. DataTransfer - COMPLETED
- **Status**: Implemented and tested
- **Files created**:
  - Task: `DataTransferTask.java`
  - Controller: Updated `Ocpp20Controller.java`
  - JSP View: `dataTransfer.jsp`
  - DTO: `DataTransferParams.java`
  - Simulator: Updated both simulators

## Priority 2: Authorization & Reservations (4 commands)

### 13. SendLocalList
- **Purpose**: Send local authorization list
- **Files needed**:
  - Task: `SendLocalListTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `sendLocalList.jsp`
  - DTO: `SendLocalListForm.java`

### 14. GetLocalListVersion
- **Purpose**: Get local list version
- **Files needed**:
  - Task: `GetLocalListVersionTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: Simple button

### 15. ReserveNow
- **Purpose**: Reserve EVSE for user
- **Files needed**:
  - Task: `ReserveNowTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `reserveNow.jsp`
  - DTO: `ReserveNowForm.java`

### 16. CancelReservation
- **Purpose**: Cancel existing reservation
- **Files needed**:
  - Task: `CancelReservationTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `cancelReservation.jsp`
  - DTO: `CancelReservationForm.java`

## Priority 3: Certificate Management (6 commands)

### 17. CertificateSigned
- **Purpose**: Provide signed certificate
- **Files needed**:
  - Task: `CertificateSignedTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `certificateSigned.jsp`
  - DTO: `CertificateSignedForm.java`

### 18. DeleteCertificate
- **Purpose**: Delete installed certificate
- **Files needed**:
  - Task: `DeleteCertificateTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `deleteCertificate.jsp`
  - DTO: `DeleteCertificateForm.java`

### 19. GetInstalledCertificateIds
- **Purpose**: Get list of installed certificates
- **Files needed**:
  - Task: `GetInstalledCertificateIdsTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: Simple button

### 20. GetCertificateStatus
- **Purpose**: Get OCSP certificate status
- **Files needed**:
  - Task: `GetCertificateStatusTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `getCertificateStatus.jsp`
  - DTO: `GetCertificateStatusForm.java`

### 21. InstallCertificate
- **Purpose**: Install root/V2G certificate
- **Files needed**:
  - Task: `InstallCertificateTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `installCertificate.jsp`
  - DTO: `InstallCertificateForm.java`

### 22. Get15118EVCertificate
- **Purpose**: Trigger ISO 15118 EV certificate request
- **Files needed**:
  - Task: `Get15118EVCertificateTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `get15118EVCertificate.jsp`
  - DTO: `Get15118EVCertificateForm.java`

## Priority 4: Monitoring & Display (7 commands)

### 23. SetMonitoringBase
- **Purpose**: Set monitoring base level
- **Files needed**:
  - Task: `SetMonitoringBaseTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `setMonitoringBase.jsp`
  - DTO: `SetMonitoringBaseForm.java`

### 24. SetMonitoringLevel
- **Purpose**: Set monitoring severity level
- **Files needed**:
  - Task: `SetMonitoringLevelTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `setMonitoringLevel.jsp`
  - DTO: `SetMonitoringLevelForm.java`

### 25. SetVariableMonitoring
- **Purpose**: Set variable monitoring
- **Files needed**:
  - Task: `SetVariableMonitoringTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `setVariableMonitoring.jsp`
  - DTO: `SetVariableMonitoringForm.java`

### 26. ClearVariableMonitoring
- **Purpose**: Clear variable monitoring
- **Files needed**:
  - Task: `ClearVariableMonitoringTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `clearVariableMonitoring.jsp`
  - DTO: `ClearVariableMonitoringForm.java`

### 27. GetMonitoringReport
- **Purpose**: Get monitoring report
- **Files needed**:
  - Task: `GetMonitoringReportTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `getMonitoringReport.jsp`
  - DTO: `GetMonitoringReportForm.java`

### 28. SetDisplayMessage
- **Purpose**: Display message on charge point
- **Files needed**:
  - Task: `SetDisplayMessageTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `setDisplayMessage.jsp`
  - DTO: `SetDisplayMessageForm.java`

### 29. GetDisplayMessages
- **Purpose**: Get configured display messages
- **Files needed**:
  - Task: `GetDisplayMessagesTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `getDisplayMessages.jsp`
  - DTO: `GetDisplayMessagesForm.java`

### 30. ClearDisplayMessage
- **Purpose**: Clear display message
- **Files needed**:
  - Task: `ClearDisplayMessageTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `clearDisplayMessage.jsp`
  - DTO: `ClearDisplayMessageForm.java`

### 31. GetTransactionStatus
- **Purpose**: Get transaction queue status
- **Files needed**:
  - Task: `GetTransactionStatusTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: Simple button or with transaction ID

### 32. CustomerInformation
- **Purpose**: Request customer information
- **Files needed**:
  - Task: `CustomerInformationTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `customerInformation.jsp`
  - DTO: `CustomerInformationForm.java`

## Priority 5: Advanced Features (6 commands - OCPP 2.1)

### 33. PublishFirmware
- **Purpose**: Publish firmware for local distribution
- **Files needed**:
  - Task: `PublishFirmwareTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `publishFirmware.jsp`
  - DTO: `PublishFirmwareForm.java`

### 34. UnpublishFirmware
- **Purpose**: Stop publishing firmware
- **Files needed**:
  - Task: `UnpublishFirmwareTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `unpublishFirmware.jsp`
  - DTO: `UnpublishFirmwareForm.java`

### 35. CostUpdated
- **Purpose**: Update transaction cost
- **Files needed**:
  - Task: `CostUpdatedTask.java`
  - UI Controller: Add to `Ocpp20Controller.java`
  - JSP View: `costUpdated.jsp`
  - DTO: `CostUpdatedForm.java`

### 36-43. DER Commands (Distributed Energy Resources - OCPP 2.1)
- SetDERControl
- GetDERControl
- ClearDERControl
- NotifyDERAlarm (CP-initiated)
- ReportDERControl (CP-initiated)

## Implementation Process Per Command

For each command, follow this systematic approach:

### Step 1: Create Task Class (5 min)
```java
@Getter
public class CommandNameTask extends Ocpp20Task<CommandNameRequest, CommandNameResponse> {
    // Parameters
    public CommandNameTask(List<String> chargeBoxIds, ...) {
        super("CommandName", chargeBoxIds);
    }

    @Override
    public CommandNameRequest createRequest() {
        // Build request
    }

    @Override
    public Class<CommandNameResponse> getResponseClass() {
        return CommandNameResponse.class;
    }
}
```

### Step 2: Create DTO Form Class (5 min)
```java
@Getter
@Setter
public class CommandNameForm {
    @NotEmpty
    private List<String> chargeBoxIds;

    // Command-specific fields with validation
}
```

### Step 3: Add Controller Method (10 min)
```java
@GetMapping("/operations/v20/CommandName")
public String getCommandName(Model model) {
    model.addAttribute("commandNameForm", new CommandNameForm());
    model.addAttribute("chargeBoxes", chargePointRepository.getChargeBoxIds());
    return "data-man/ocpp20/commandName";
}

@PostMapping("/operations/v20/CommandName")
public String postCommandName(@Valid CommandNameForm form, BindingResult result) {
    if (result.hasErrors()) {
        return "data-man/ocpp20/commandName";
    }

    CommandNameTask task = new CommandNameTask(form.getChargeBoxIds(), ...);
    ocpp20TaskService.addTask(task);
    return "redirect:/manager/operations/v20/tasks";
}
```

### Step 4: Create JSP View (15 min)
- Copy template from existing operation JSP
- Add form fields for command parameters
- Add validation and help text
- Style consistently with existing UI

### Step 5: Test (10 min)
1. Compile and deploy
2. Access UI form
3. Submit to charge point simulator
4. Verify request/response in logs
5. Check database persistence if applicable

### Step 6: Document (5 min)
- Update OCPP20_IMPLEMENTATION.md
- Add command to user documentation
- Note any limitations or requirements

**Total per command: ~50 minutes**

## Testing Strategy

1. **Unit Tests**: Test task creation with valid/invalid parameters
2. **Integration Tests**: Test with OCPP 2.0.1 simulator
3. **UI Tests**: Verify form validation and submission
4. **End-to-End**: Test against real charge point if available

## Progress Tracking

- [x] Priority 1: 3/12 commands (ChangeAvailability ✅, ClearCache ✅, DataTransfer ✅)
- [ ] Priority 2: 0/4 commands
- [ ] Priority 3: 0/6 commands
- [ ] Priority 4: 0/8 commands
- [ ] Priority 5: 0/6 commands

**Total: 3/36 additional CSMS commands**

---

**Note**: This implementation plan assumes the generated OCPP 2.0.1 model classes have standard setter methods. Actual implementation may require adjustments based on the generated API.