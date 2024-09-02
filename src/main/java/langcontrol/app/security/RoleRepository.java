package langcontrol.app.security;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends ListCrudRepository<Role, Long> {

    Optional<Role> findByValue(String value);
}
