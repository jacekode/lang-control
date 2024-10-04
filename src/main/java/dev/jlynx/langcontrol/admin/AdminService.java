package dev.jlynx.langcontrol.admin;

import java.util.List;

public interface AdminService {

    List<UserOverviewDTO> getAllUsers();

    void editUser(long accountId, EditUserDTO dto);

    void deleteUser(long accountId);
}
