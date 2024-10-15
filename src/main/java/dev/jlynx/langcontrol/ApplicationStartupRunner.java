package dev.jlynx.langcontrol;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.role.RoleRepository;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.usersettings.UserSettings;
import dev.jlynx.langcontrol.usersettings.UserSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ApplicationStartupRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStartupRunner.class);

    @Value("${admin.properties.username}")
    private String adminUsername;

    @Value("${admin.properties.password}")
    private String adminPassword;

    private final RoleRepository roleRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public ApplicationStartupRunner(
            RoleRepository roleRepository,
            UserSettingsRepository userSettingsRepository,
            AccountRepository accountRepository,
            PasswordEncoder encoder
    ) {
        this.roleRepository = roleRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.accountRepository = accountRepository;
        this.encoder = encoder;
    }

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (accountRepository.existsByUsername(adminUsername)) {
            LOG.debug("Admin account with username='root' not created (already exists).");
            return;
        }

        Optional<Role> adminRoleOptional = roleRepository.findByValue(DefinedRoleValue.ADMIN.getValue());
        Role adminRole;
        if (adminRoleOptional.isEmpty()) {
            Role newAdminRole = new Role(null, DefinedRoleValue.ADMIN);
            adminRole = roleRepository.save(newAdminRole);
        } else {
            adminRole = adminRoleOptional.get();
        }
        Optional<Role> userRoleOptional = roleRepository.findByValue(DefinedRoleValue.USER.getValue());
        Role userRole;
        if (userRoleOptional.isEmpty()) {
            Role newAdminRole = new Role(null, DefinedRoleValue.USER);
            userRole = roleRepository.save(newAdminRole);
        } else {
            userRole = userRoleOptional.get();
        }
        List<Role> roles = List.of(adminRole, userRole);

        Account accountToCreate = new Account(
                null,
                adminUsername,
                encoder.encode(adminPassword),
                roles,
                true,
                true,
                true,
                true);

        UserProfile userProfileToCreate = new UserProfile(
                null,
                adminUsername);

        UserSettings userSettings = UserSettings.withDefaults();
        userSettingsRepository.save(userSettings);

        userProfileToCreate.setUserSettings(userSettings);
        userProfileToCreate.setDecks(new ArrayList<>());
        userProfileToCreate.setAccount(accountToCreate);
        accountToCreate.setUserProfile(userProfileToCreate);
        accountRepository.save(accountToCreate);
        LOG.debug("Admin account with username='root' was created.");

    }
}
