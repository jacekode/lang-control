package dev.jlynx.langcontrol.userprofile;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends ListCrudRepository<UserProfile, Long> {

    Optional<UserProfile> findByAccount_Id(Long id);
}
