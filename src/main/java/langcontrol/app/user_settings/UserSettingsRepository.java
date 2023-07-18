package langcontrol.app.user_settings;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends ListCrudRepository<UserSettings, Long> {
}