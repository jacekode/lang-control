package langcontrol.app.usersettings;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "default_dynamic_sentences", nullable = false)
    private boolean dynamicSentencesOnByDefault;

    @Column(name = "zen_mode", nullable = false)
    private boolean zenModeEnabled;

    public UserSettings(Long id, boolean dynamicSentencesOnByDefault, boolean zenModeEnabled) {
        this.id = id;
        this.dynamicSentencesOnByDefault = dynamicSentencesOnByDefault;
        this.zenModeEnabled = zenModeEnabled;
    }

    /**
     * A factory method that creates a new UserSettings object with default settings. It's the preferred way of
     * instantiating the UserSettings class when creating/registering a new Account.
     *
     * @return A UserSettings object with pre-set default values
     */
    public static UserSettings withDefaults() {
        return new UserSettings(null, false, false);
    }
}
