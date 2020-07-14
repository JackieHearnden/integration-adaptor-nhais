package uk.nhs.digital.nhsconnect.nhais.translator;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.translator.acceptance.AcceptanceBirthTranslator;
import uk.nhs.digital.nhsconnect.nhais.translator.acceptance.AcceptanceFirstTranslator;
import uk.nhs.digital.nhsconnect.nhais.translator.acceptance.AcceptanceImmigrantTranslator;
import uk.nhs.digital.nhsconnect.nhais.translator.acceptance.AcceptanceTransferinTranslator;
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.AmendmentToEdifactTranslator;
import uk.nhs.digital.nhsconnect.nhais.translator.removal.RemovalTranslator;

import java.util.List;

/**
 * Delegates to a translator based on transaction type and other information
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirToEdifactSegmentTranslator {

    private final AcceptanceBirthTranslator acceptanceBirthTranslator;
    private final AcceptanceFirstTranslator acceptanceFirstTranslator;
    private final AcceptanceTransferinTranslator acceptanceTransferinTranslator;
    private final AcceptanceImmigrantTranslator acceptanceImmigrantTranslator;
    private final RemovalTranslator removalTranslator;
    private final StubTranslator stubTranslator;

    public List<Segment> createMessageSegments(Parameters parameters, ReferenceTransactionType.TransactionType transactionType) throws FhirValidationException {
        switch ((ReferenceTransactionType.Outbound) transactionType) {
            case ACCEPTANCE:
                return delegateAcceptance(parameters);
            case REMOVAL:
                return removalTranslator.translate(parameters);
            case AMENDMENT:
                throw new FhirValidationException("Amendment operation is not allowed here, it should consist of json patches");
            case DEDUCTION:
                //TODO the exception here should be thrown also for deduction, to be changed after adding deduction translator
            default:
                return stubTranslator.translate(parameters, transactionType);
        }
    }

    private List<Segment> delegateAcceptance(Parameters parameters) throws FhirValidationException {
        var acceptanceType = ParametersExtension.extractAcceptanceType(parameters);
        switch (acceptanceType) {
            case BIRTH:
                return acceptanceBirthTranslator.translate(parameters);
            case FIRST:
                return acceptanceFirstTranslator.translate(parameters);
            case TRANSFER_IN:
                return acceptanceTransferinTranslator.translate(parameters);
            case IMMIGRANT:
                return acceptanceImmigrantTranslator.translate(parameters);
            default:
                throw new UnsupportedOperationException(String.format("%s is not supported", acceptanceType));
        }
    }
}
