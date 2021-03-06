package uk.nhs.digital.nhsconnect.nhais.outbound.translator.deduction;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RegistrationMessageDateTime;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.DeductionDateMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.DeductionReasonCodeMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.FhirToEdifactTranslator;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.acceptance.OptionalInputValidator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.nhs.digital.nhsconnect.nhais.outbound.mapper.FromFhirToEdifactMapper.mapSegment;
import static uk.nhs.digital.nhsconnect.nhais.outbound.mapper.FromFhirToEdifactMapper.optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeductionTranslator implements FhirToEdifactTranslator {

    private final PartyQualifierMapper partyQualifierMapper;
    private final GpNameAndAddressMapper gpNameAndAddressMapper;
    private final PersonNameMapper personNameMapper;
    private final FreeTextMapper freeTextMapper;
    private final DeductionDateMapper deductionDateMapper;
    private final DeductionReasonCodeMapper deductionReasonCodeMapper;
    private final OptionalInputValidator validator;

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        if(validator.nhsNumberIsMissing(parameters)) {
            throw new FhirValidationException("Patient resource property /identifier/0/value (NHS Number) is required");
        }
        List<Segment> segments = Stream.of(
            //BGM
            mapSegment(new BeginningOfMessage()),
            //NAD+FHS
            partyQualifierMapper,
            //DTM+137
            mapSegment(new RegistrationMessageDateTime()),
            //RFF+950
            mapSegment(new ReferenceTransactionType(ReferenceTransactionType.Outbound.DEDUCTION)),
            //S01
            mapSegment(new SegmentGroup(1)),
            //RFF+TN
            mapSegment(new ReferenceTransactionNumber()),
            //NAD+GP
            gpNameAndAddressMapper,
            //GIS+
            deductionReasonCodeMapper,
            // DTM+961
            deductionDateMapper,
            // FTX+RGI
            optional(freeTextMapper, parameters),
            //HEA+ACD
            //S02
            mapSegment(new SegmentGroup(2)),
            // PNA+PAT
            personNameMapper)
            .map(mapper -> mapper.map(parameters))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return segments;
    }
}
