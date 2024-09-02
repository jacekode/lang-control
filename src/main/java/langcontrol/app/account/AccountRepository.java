package langcontrol.app.account;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends ListCrudRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}
