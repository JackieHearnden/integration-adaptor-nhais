package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonGPPrevious;

import java.util.Objects;

public class PersonGPPreviousMapper implements FromFhirToEdifactMapper<PersonGPPrevious> {
    private final static String GP_CODE = "900";
    private final static String PREVIOUS_GP_PARAM = "previousGPName";

    public PersonGPPrevious map(Parameters parameters) {
        return PersonGPPrevious.builder()
            .identifier(getPersonPreviousGP(parameters))
            .code(GP_CODE)
            .build();
    }

    private String getPersonPreviousGP(Parameters parameters) {
        return parameters.getParameter()
            .stream()
            .filter(param -> PREVIOUS_GP_PARAM.equalsIgnoreCase(param.getName()))
            .map(Parameters.ParametersParameterComponent::getValue)
            .map(Objects::toString)
            .map(this::splitPractitionerString)
            .findFirst()
            .orElseThrow(() -> new FhirValidationException("Error while parsing param: " + PREVIOUS_GP_PARAM));
    }

    private String splitPractitionerString(String value) {
        return value.split("/")[1];
    }
}