package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    List<Artist> findByLastname(String lastname);

    List<Artist> findByFirstnameContainingOrLastnameContaining(
        String firstname, String lastname);
}