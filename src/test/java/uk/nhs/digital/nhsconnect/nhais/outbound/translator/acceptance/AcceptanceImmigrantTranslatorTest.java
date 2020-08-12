package uk.nhs.digital.nhsconnect.nhais.outbound.translator.acceptance;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptanceImmigrantTranslatorTest {

    @Mock private OptionalInputValidator validator;

    @InjectMocks
    private AcceptanceImmigrantTranslator acceptanceImmigrantTranslator;

    @Test
    void When_MissingNhsNumberAndBirthPlace_Then_ThrowFhirValidationException() {
        Parameters parameters = new Parameters();
        when(validator.nhsNumberIsMissing(parameters)).thenReturn(true);
        when(validator.placeOfBirthIsMissing(parameters)).thenReturn(true);

        assertThatThrownBy(() -> acceptanceImmigrantTranslator.translate(parameters))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage("Place of birth is mandatory when NHS number is missing");
    }

    @Test
    void when_MisssingSurname_Then_ThrowFhirValidationException() {
        Parameters parameters = new Parameters();
        when(validator.surnameIsMissing(parameters)).thenReturn(true);

        assertThatThrownBy(() -> acceptanceImmigrantTranslator.translate(parameters))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage("Surname of patient is missing");
    }

}