package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfEntry;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonDateOfEntryMapperTest {
    private static final String DATE_STRING = "1991-11-06";
    private static final LocalDate LOCAL_DATE = LocalDate.parse(DATE_STRING);
    private static final Instant FIXED_TIME = LOCAL_DATE.atStartOfDay(ZoneId.of("Europe/London")).toInstant();

    @Test
    void When_MappingDateOfEntry_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.ENTRY_DATE)
            .setValue(new StringType(DATE_STRING));

        var personDateOfEntryMapper = new PersonDateOfEntryMapper();
        PersonDateOfEntry personDateOfEntry = personDateOfEntryMapper.map(parameters);

        var expectedPersonDateOfEntry = PersonDateOfEntry
            .builder()
            .timestamp(FIXED_TIME)
            .build();

        assertEquals(expectedPersonDateOfEntry, personDateOfEntry);

    }

    @Test
    public void When_MappingWithWrongDate_Then_DateTimeParseExceptionIsThrown() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.ENTRY_DATE)
            .setValue(new StringType(""));

        var personDateOfEntryMapper = new PersonDateOfEntryMapper();
        assertThrows(DateTimeParseException.class, () -> personDateOfEntryMapper.map(parameters));
    }

    @Test
    public void When_MappingWithoutDateParam_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personDateOfEntryMapper = new PersonDateOfEntryMapper();
        assertThrows(FhirValidationException.class, () -> personDateOfEntryMapper.map(parameters));
    }
}
