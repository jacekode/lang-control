package langcontrol.app.security;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "string_value")
    private String value;

    public Role(Long id, DefinedRoleValue definedRoleValue) {
        this.id = id;
        this.value = definedRoleValue.getValue();
    }

    @Override
    public String getAuthority() {
        return this.getValue();
    }
}
