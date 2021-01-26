package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.OcppChargingProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OcppChargingProfileRepository extends CrudRepository<OcppChargingProfile, Integer> {
    @Query("SELECT OBJECT(p) FROM OcppChargingProfile p WHERE (?1 IS NULL OR p.chargingProfilePurpose=?1) AND (?2 IS NULL OR p.stackLevel=?2)")
    List<OcppChargingProfile> findByPurposeOrNullAndStackLevelOrNull(String purpose, Integer stackLevel);

    @Query("SELECT OBJECT(p) FROM OcppChargingProfile p WHERE (?1 IS NULL OR p.chargingProfilePk=?1) AND (?2 IS NULL OR p.stackLevel=?2) AND (?3 IS NULL OR p.description LIKE ?3) AND (?4 IS NULL OR p.chargingProfilePurpose=?4) AND (?5 IS NULL OR p.chargingProfileKind=?5) AND (?6 IS NULL OR p.recurrencyKind=?6) AND (?7 IS NULL OR p.validFrom >= ?7) AND (?8 IS NULL OR p.validTo <= ?8)")
    List<OcppChargingProfile> search(Integer chargingProfilePk, Integer stackLevel, String description, String profilePurpose, String profileKind, String recurrencyKind, Date validFrom, Date validTo);

}
