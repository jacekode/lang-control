package langcontrol.app.spacedrepetition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardRatedOverviewDTO {

    private long id;
    private boolean switchedToReviewMode;
    private boolean switchedToLearnMode;
}
