package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.SingleChargePointSelect;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.cglib.core.Local;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

@Setter
@Getter
public class SetChargingProfileParams  extends SingleChargePointSelect
{
    //Required
    @NotNull(message = "Connector ID is required")
    private int connectorId;

    //Required
    private ChargingProfile csChargingProfiles;

    public boolean isSetCsChargingProfiles() {return csChargingProfiles != null && !csChargingProfiles.equals(""); }




    @NotNull(message = "Charging Profile ID is required")
    private int chargingProfileId;

    //Optional
    //private Integer transactionId;

    @Min(value = 0, message = "Stack Level must be at least {value}")
    private int stackLevel;

    //Not able to set Charging profile in transaction momentarily.
    @NotNull(message = "ChargingProfilePurposeType cannot be TX_Profile outside transaction")
    private ChargingProfilePurposeType chargingProfilePurpose;

    @NotNull(message = "Charging Profile Kind is required")
    private ChargingProfileKindType chargingProfileKind;

    //Optional
    private RecurrencyKindType recurrencyKind;

    //Optional
    private LocalDateTime validFrom;

    //Optional
    private LocalDateTime validTo;

    //Not able to set Charging profile in transaction momentarily.
    public void setChargingProfilePurpose(ChargingProfilePurposeType chargingProfilePurpose)
    {
        if (chargingProfilePurpose == ChargingProfilePurposeType.TX_PROFILE)
            this.chargingProfilePurpose = null;
        else
            this.chargingProfilePurpose = chargingProfilePurpose;
    }

    //Required
    private ChargingSchedule chargingSchedule;




    //Optional (Charging Schedule)
    private Integer duration;

    //Optional (Charging Schedule)
    private LocalDateTime startSchedule;


    @NotNull(message = "Charging Rate Unit Required") //(Charging Schedule)
    private ChargingRateUnitType chargingRateUnit;

    //Required (Charging Schedule)
    private List<ChargingSchedulePeriod> chargingSchedulePeriod;

    //Optional (Charging Schedule)
    private BigDecimal minChargingRate; //BigDecimal

    public void setMinChargingRate(BigDecimal minChargingRate)
    {
        if (minChargingRate != null)
        {
            minChargingRate = minChargingRate.setScale(1, RoundingMode.HALF_UP);
            this.minChargingRate = minChargingRate;
        }
    }



    @NotNull(message = "Start Period Required") //Required Charging Schedule Period
    private int startPeriod;

    @NotNull(message = "Limit Required") //Required Charging Schedule Period
    private BigDecimal limit; //BigDecimal

    public void setLimit(BigDecimal limit)
    {
        if (CheckFractions(limit.toString()) >= 1)
        {
            limit = limit.setScale(1, RoundingMode.HALF_UP);
            this.limit = limit;
        }
        else
        {
            this.limit = limit;
        }
    }

    private int CheckFractions(String string)
    {
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }

    //Optional Charging Schedule Period
    private Integer numberPhases;
}
