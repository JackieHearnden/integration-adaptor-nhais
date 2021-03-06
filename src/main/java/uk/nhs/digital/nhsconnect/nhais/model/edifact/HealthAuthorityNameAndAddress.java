package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

/**
 * Example NAD+FHS+XX1:954'
 */
@Getter @Setter @RequiredArgsConstructor
public class HealthAuthorityNameAndAddress extends Segment{

    public static final String KEY = "NAD";
    public static final String QUALIFIER = "FHS";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private @NonNull String identifier;
    private @NonNull String code;


    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER + "+" + identifier + ":" + code;
    }

    @Override
    protected void validateStateful() {
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (StringUtils.isEmpty(identifier)) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
        if (StringUtils.isEmpty(code)) {
            throw new EdifactValidationException(getKey() + ": Attribute code is required");
        }
    }

    public static HealthAuthorityNameAndAddress fromString(String edifactString) {
        if(!edifactString.startsWith(HealthAuthorityNameAndAddress.KEY_QUALIFIER)){
            throw new IllegalArgumentException("Can't create " + HealthAuthorityNameAndAddress.class.getSimpleName() + " from " + edifactString);
        }
        String[] keySplit = Split.byPlus(edifactString);
        String identifier = Split.byColon(keySplit[2])[0];
        String code = Split.byColon(keySplit[2])[1];
        return new HealthAuthorityNameAndAddress(identifier, code);
    }
}
