package langcontrol.app.deck.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeckDetailsDTO {

    private long id;
    private String name;
    private int totalCardsNumber;
    private int cardsForReviewNumber;
}
