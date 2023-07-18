package langcontrol.app.admin;

import java.util.List;

public interface AdminService {

    List<UserOverviewDTO> getAllUsers();

    void editUser(long accountId, EditUserDTO editUserDTO);

    void deleteUser(long accountId);
}
