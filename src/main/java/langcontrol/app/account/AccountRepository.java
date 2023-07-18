package langcontrol.app.account;

import langcontrol.app.security.Role;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends ListCrudRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}
