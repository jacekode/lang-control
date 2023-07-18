package langcontrol.app.flashcard;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public enum LearnModeStep {
    ONE(1, ChronoUnit.MINUTES, 1), TWO(2, ChronoUnit.MINUTES, 10),
    THREE(3, ChronoUnit.DAYS, 1);

    private final int numeral;
    private final TemporalUnit temporalUnit;
    private final long amountToAdd;

    LearnModeStep(int numeral, TemporalUnit temporalUnit, long amountToAdd) {
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

    public long getAmountToAdd() {
        return amountToAdd;
    }

    public static LearnModeStep getByNumeral(int numeral) {
        for (LearnModeStep step : LearnModeStep.values()) {
            if (step.getNumeral() == numeral) {
                return step;
            }
        }
        return null;
    }
}
