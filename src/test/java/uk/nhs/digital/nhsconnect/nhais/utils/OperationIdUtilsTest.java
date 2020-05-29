package uk.nhs.digital.nhsconnect.nhais.utils;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;

import static org.junit.jupiter.api.Assertions.*;

class OperationIdUtilsTest {

    @Test
    void whenBuildingOperationId_thenHashIsCreated() {
        assertEquals(
            "5d6c5b2009aa0a3a88ca8bd8ba339df16a831c35196136cba56f13b742461231",
            OperationIdUtils.buildOperationId("some_sender", 123L));

        assertEquals(
            "0c8fc3217244a8a5ad21869bed877a73fc844eeb00d28ad72e936c19fac03455",
            OperationIdUtils.buildOperationId("some_other_sender", 234L));
    }
}
