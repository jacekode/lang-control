package langcontrol.app.user_profile;

import langcontrol.app.account.Account;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends ListCrudRepository<UserProfile, Long> {

    Optional<UserProfile> findByAccount_Id(Long id);
}
