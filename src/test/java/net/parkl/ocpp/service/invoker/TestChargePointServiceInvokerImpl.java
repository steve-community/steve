package net.parkl.ocpp.service.invoker;

import net.parkl.ocpp.service.OcppTestFixture;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import de.rwth.idsg.steve.ocpp.ChargePointService16_Invoker;
import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.ChangeAvailabilityTask;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.ClearChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.DataTransferTask;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStopTransactionTask;
import de.rwth.idsg.steve.ocpp.task.ReserveNowTask;
import de.rwth.idsg.steve.ocpp.task.ResetTask;
import de.rwth.idsg.steve.ocpp.task.SendLocalListTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.TriggerMessageTask;
import de.rwth.idsg.steve.ocpp.task.UnlockConnectorTask;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.CentralSystemService16_Service;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
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

@Component
public class TestChargePointServiceInvokerImpl implements ChargePointService16_Invoker {
	
	@Autowired
	private OcppTestFixture fixture;
	
	@Autowired
	private CentralSystemService16_Service csService;
	
	@Autowired
    private TaskExecutor taskExecutor;



	private String lastIdTag;

	@Override
	public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<GetLocalListVersionResponse>(createGetLocalListVersionResponse(task.getParams())));
	}

	

	@Override
	public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<SendLocalListResponse>(createSendLocalListResponse(task.getParams())));
	}
	
	

	

	@Override
	public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset(ChargePointSelect cp, ResetTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<ResetResponse>(createResetResponse(task.getParams())));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
		task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
				new ResponseWrapper<RemoteStartTransactionResponse>(createRemoteStartTransactionResponse(task.getParams())));
		
		taskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
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
			}
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
