package langcontrol.app.deck;

import langcontrol.app.user_profile.UserProfile;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends ListCrudRepository<Deck, Long> {

    List<DeckView> findByUserProfile(UserProfile userProfile);

    Optional<Deck> findByName(String name);

}
