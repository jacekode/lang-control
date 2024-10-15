package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.admin.dto.DeleteAccountAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdatePasswordAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdateUserRequest;
import dev.jlynx.langcontrol.admin.dto.UserOverview;
import java.util.List;

public interface AdminService {

    List<UserOverview> getAllUsers();

    UserOverview getUserById(long accountId);

    void updateUser(long accountId, UpdateUserRequest body);

    void overwriteUserPassword(long accountId, UpdatePasswordAdminRequest body);

    void deleteAccount(long accountId, DeleteAccountAdminRequest body);
}
