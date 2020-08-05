package uk.nhs.digital.nhsconnect.nhais.outbound.controller;

import lombok.extern.slf4j.Slf4j;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class FhirControllerIntegrationTest {
    private static final String expectedOperationId = "c35b1432682a04ca77b287b11f240fbd8c46f22fe589b513a84e307b57dcb820";

    @Autowired
    OutboundStateRepository outboundStateRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FhirParser fhirParser;

    @Value("classpath:patient/not-json.xml")
    private Resource notJsonPayload;

    @Value("classpath:patient/deduction-missing-destination-ha-cipher.fhir.json")
    private Resource deductionNoHaCipher;

    @Value("classpath:patient/deduction-missing-gp-code.fhir.json")
    private Resource deductionNoGpCode;

    @Value("classpath:patient/deduction-missing-gp-trading-partner-code.fhir.json")
    private Resource deductionNoTradingPartnerCode;

    @Value("classpath:patient/removal-missing-destination-ha-cipher.fhir.json")
    private Resource removalNoHaCipher;

    @Value("classpath:patient/removal-missing-gp-code.fhir.json")
    private Resource removalNoGpCode;

    @Value("classpath:patient/removal-missing-gp-trading-partner-code.fhir.json")
    private Resource removalNoTradingPartnerCode;

    @Value("classpath:patient/deduction-invalid-json-structure.fhir.json")
    private Resource deductionInvalidJsonStructure;

    @Value("classpath:patient/removal-invalid-json-structure.fhir.json")
    private Resource removalInvalidJsonStructure;

    @Value("classpath:patient/acceptance-unknown-recipient.fhir.json")
    private Resource acceptanceUnknownRecipient;

    @Test
    void whenNotJson_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(notJsonPayload.getFile().toPath()));
        MvcResult result = mockMvc.perform(post("/fhir/Patient/$nhais.acceptance").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Content does not appear to be FHIR JSON");
    }

    @Test
    void whenDeductionNoDestinationHaCipher_thenRerturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionNoHaCipher.getFile().toPath()));
        MvcResult result = mockMvc.perform(post("/fhir/Patient/$nhais.deduction").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText())
            .contains("Invalid attribute value \"\": Attribute values must not be empty");
    }

    @Test
    void whenRemovalNoDestinationHaCipher_thenRerturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalNoHaCipher.getFile().toPath()));
        MvcResult result = mockMvc.perform(post("/fhir/Patient/$nhais.removal").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText())
            .contains("Invalid attribute value \"\": Attribute values must not be empty");
    }


    @Test
    void whenDeductionNoGpCode_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionNoGpCode.getFile().toPath()));
        MvcResult result = mockMvc.perform(post("/fhir/Patient/$nhais.deduction").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText())
            .contains("Invalid attribute value \"\": Attribute values must not be empty");
    }

    @Test
    void whenRemovalNoGpCode_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalNoGpCode.getFile().toPath()));
        MvcResult result = mockMvc.perform(post("/fhir/Patient/$nhais.removal").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText())
            .contains("Invalid attribute value \"\": Attribute values must not be empty");
    }

    @Test
    void whenDeductionNpGpTradingPartnerCode_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionNoTradingPartnerCode.getFile().toPath()));
        MvcResult result = mockMvc.perform(post("/fhir/Patient/$nhais.deduction").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText())
            .contains("Invalid attribute value \"\": Attribute values must not be empty");
    }

    @Test
    void whenRemovalNpGpTradingPartnerCode_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalNoTradingPartnerCode.getFile().toPath()));
        MvcResult result = mockMvc.perform(post("/fhir/Patient/$nhais.removal").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText())
            .contains("Invalid attribute value \"\": Attribute values must not be empty");
    }

    @Test
    void whenDeductionInvalidJsonStructure_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionInvalidJsonStructure.getFile().toPath()));
        mockMvc.perform(post("/fhir/Patient/$nhais.deduction").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
        //TODO 2020-07-30: to check the OperationOutcome when in NIAD-184 error status ir properly handled
    }

    @Test
    void whenRemovalInvalidJsonStructure_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalInvalidJsonStructure.getFile().toPath()));
        mockMvc.perform(post("/fhir/Patient/$nhais.removal").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
        //TODO 2020-07-30: to check the OperationOutcome when in NIAD-184 error status ir properly handled
    }

    @Test
    void whenUnknownRecipient_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(acceptanceUnknownRecipient.getFile().toPath()));
        MvcResult result = mockMvc.perform(post("/fhir/Patient/$nhais.acceptance").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Couldn't decode recipient: ABCD1");
    }

}