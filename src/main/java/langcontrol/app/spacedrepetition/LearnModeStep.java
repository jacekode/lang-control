package langcontrol.app.spacedrepetition;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public enum LearnModeStep {
    ONE(1, ChronoUnit.MINUTES, 1), TWO(2, ChronoUnit.MINUTES, 10);

    private final int numeral;
    private final TemporalUnit temporalUnit;
    private final int amountToAdd;

    LearnModeStep(int numeral, TemporalUnit temporalUnit, int amountToAdd) {
        this.numeral = numeral;
        this.temporalUnit = temporalUnit;
        this.amountToAdd = amountToAdd;
    }

    public int getNumeral() {
        return numeral;
    }

    public TemporalUnit getTemporalUnit() {
        return temporalUnit;
    }

    public int getAmountToAdd() {
        return amountToAdd;
    }

    public static LearnModeStep fromNumeral(int numeral) {
        for (LearnModeStep step : LearnModeStep.values()) {
            if (step.getNumeral() == numeral) {
                return step;
            }
        }
        return null;
    }
}
