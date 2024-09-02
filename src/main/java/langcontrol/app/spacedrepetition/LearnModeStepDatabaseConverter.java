package langcontrol.app.spacedrepetition;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LearnModeStepDatabaseConverter implements AttributeConverter<LearnModeStep, Short> {

    @Override
    public Short convertToDatabaseColumn(LearnModeStep learnModeStep) {
        return (short) learnModeStep.getNumeral();
    }

    @Override
    public LearnModeStep convertToEntityAttribute(Short columnVal) {
        return LearnModeStep.fromNumeral((int) columnVal);
    }
}
