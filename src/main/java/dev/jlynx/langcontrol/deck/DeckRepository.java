package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.view.DeckView;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends ListCrudRepository<Deck, Long> {

    List<DeckView> findByUserProfile(UserProfile userProfile);

    Optional<Deck> findByName(String name);

    @Transactional
    @Modifying
    @Query("update Deck d set d.name = :name where d.id = :id")
    int updateById(@Param("id") Long id, @Param("name") String name);
}
