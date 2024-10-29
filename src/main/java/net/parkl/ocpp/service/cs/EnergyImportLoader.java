package net.parkl.ocpp.service.cs;

import lombok.RequiredArgsConstructor;
import net.parkl.ocpp.entities.AbstractTransactionEnergyImport;
import net.parkl.ocpp.repositories.TransactionEnergyImportLegacyRepository;
import net.parkl.ocpp.repositories.TransactionEnergyImportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnergyImportLoader {
    private final TransactionEnergyImportRepository energyImportRepository;
    private final TransactionEnergyImportLegacyRepository energyImportLegacyRepository;

    public AbstractTransactionEnergyImport loadEnergyImport(int transactionPk) {
        AbstractTransactionEnergyImport energyImport =
                energyImportRepository.findById(transactionPk).orElse(null);
        if (energyImport==null) {
            //handle Mennekes type chargers (no measurand, no unit)
            energyImport = energyImportLegacyRepository.findById(transactionPk).orElse(null);
        }
        return energyImport;
    }
}
