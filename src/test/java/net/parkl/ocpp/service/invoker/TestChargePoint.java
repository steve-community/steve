package net.parkl.ocpp.service.invoker;

import de.rwth.idsg.steve.ocpp.ChargePointService16_Invoker;
import de.rwth.idsg.steve.ocpp.task.*;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.CentralSystemService16_Service;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import net.parkl.ocpp.service.OcppTestFixture;
import ocpp.cp._2015._10.GetLocalListVersionResponse;
import ocpp.cp._2015._10.RemoteStartStopStatus;
import ocpp.cp._2015._10.RemoteStartTransactionResponse;
import ocpp.cp._2015._10.RemoteStopTransactionResponse;
import ocpp.cp._2015._10.ResetResponse;
import ocpp.cp._2015._10.ResetStatus;
import ocpp.cp._2015._10.SendLocalListResponse;
import ocpp.cp._2015._10.UpdateStatus;
import ocpp.cs._2015._10.Reason;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class TestChargePoint implements ChargePointService16_Invoker {
	
	@Autowired
	private OcppTestFixture fixture;
	
	@Autowired
	private CentralSystemService16_Service csService;
	
	@Autowired
    private TaskExecutor taskExecutor;


	private String lastIdTag;

	@Override
	public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
	}

	@Override
	public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
	}

	@Override
	public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<>(createGetLocalListVersionResponse(task.getParams())));
	}

	

	@Override
	public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<>(createSendLocalListResponse(task.getParams())));
	}

	@Override
	public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
	}

	@Override
	public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
	}

	@Override
	public void reset(ChargePointSelect cp, ResetTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<>(createResetResponse(task.getParams())));
	}

	private ResetResponse createResetResponse(ResetParams params) {
		ResetResponse resp=new ResetResponse();
		resp.setStatus(ResetStatus.ACCEPTED);
		return resp;
	}
	
	private SendLocalListResponse createSendLocalListResponse(SendLocalListParams params) {
		SendLocalListResponse resp=new SendLocalListResponse();
		resp.setStatus(UpdateStatus.ACCEPTED);
		return resp;
	}
	
	private GetLocalListVersionResponse createGetLocalListVersionResponse(MultipleChargePointSelect params) {
		GetLocalListVersionResponse resp=new GetLocalListVersionResponse();
		resp.setListVersion(1);
		return resp;
	}

	@Override
	public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
	}

	@Override
	public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
	}

	@Override
	public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
	}

	@Override
	public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
	}

	@Override
	public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
	}

	@Override
	public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
	}

	@Override
	public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<>(createRemoteStartTransactionResponse(task.getParams())));
		
		taskExecutor.execute(() -> {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			StartTransactionRequest req=new StartTransactionRequest();
			req.setConnectorId(task.getParams().getConnectorId());
			req.setIdTag(task.getParams().getIdTag());
			req.setMeterStart(fixture.getStartValue());
			req.setTimestamp(DateTime.now());

			csService.startTransaction(req, cp.getChargeBoxId());
		});
	
		
		lastIdTag=task.getParams().getIdTag();
	}

	

	@Override
	public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<RemoteStopTransactionResponse>(createRemoteStopTransactionResponse(task.getParams())));
		
		taskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
				StopTransactionRequest req=new StopTransactionRequest();
				req.setTimestamp(DateTime.now());
				req.setMeterStop(fixture.getStopValue());
				req.setReason(Reason.LOCAL);
				req.setTransactionId(task.getParams().getTransactionId());
				req.setIdTag(lastIdTag);
				csService.stopTransaction(req, cp.getChargeBoxId());
			}
		});
	}
	
	private RemoteStartTransactionResponse createRemoteStartTransactionResponse(RemoteStartTransactionParams params) {
		RemoteStartTransactionResponse resp=new RemoteStartTransactionResponse();
		resp.setStatus(RemoteStartStopStatus.ACCEPTED);
		return resp;
	}
	
	private RemoteStopTransactionResponse createRemoteStopTransactionResponse(RemoteStopTransactionParams params) {
		RemoteStopTransactionResponse resp=new RemoteStopTransactionResponse();
		resp.setStatus(RemoteStartStopStatus.ACCEPTED);
		return resp;
	}


	@Override
	public void clearChargingProfile(ChargePointSelect cp, ClearChargingProfileTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChargingProfile(ChargePointSelect cp, SetChargingProfileTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getCompositeSchedule(ChargePointSelect cp, GetCompositeScheduleTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggerMessage(ChargePointSelect cp, TriggerMessageTask task) {
		// TODO Auto-generated method stub
		
	}

}
