package langcontrol.app.usersettings;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends ListCrudRepository<UserSettings, Long> {
}