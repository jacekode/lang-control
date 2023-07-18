package langcontrol.app.user_settings;

import jakarta.persistence.*;
import langcontrol.app.user_profile.UserProfile;
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

    @Column(name = "default_dynamic_sentences")
    private boolean dynamicSentencesOnByDefault;

    @Column(name = "zen_mode")
    private boolean zenModeEnabled;

    public UserSettings(Long id, boolean dynamicSentencesOnByDefault, boolean zenModeEnabled) {
        this.id = id;
        this.dynamicSentencesOnByDefault = dynamicSentencesOnByDefault;
        this.zenModeEnabled = zenModeEnabled;
    }
}
