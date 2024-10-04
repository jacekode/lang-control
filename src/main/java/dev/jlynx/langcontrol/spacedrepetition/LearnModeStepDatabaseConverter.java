package dev.jlynx.langcontrol.spacedrepetition;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LearnModeStepDatabaseConverter implements AttributeConverter<LearnModeStep, Short> {

    @Override
    public Short convertToDatabaseColumn(LearnModeStep learnModeStep) {
        if (learnModeStep == null) {
            return null;
        }
        return (short) learnModeStep.getNumeral();
    }

    @Override
    public LearnModeStep convertToEntityAttribute(Short columnVal) {
        if (columnVal == null) {
            return null;
        }
        return LearnModeStep.fromNumeral((int) columnVal);
    }
}
