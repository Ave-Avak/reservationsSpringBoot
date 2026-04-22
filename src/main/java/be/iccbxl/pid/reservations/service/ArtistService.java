package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.Artist;
import be.iccbxl.pid.reservations.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Optional<Artist> getArtist(Long id) {
        return artistRepository.findById(id);
    }

    public Artist addArtist(Artist artist) {
        return artistRepository.save(artist);
    }

    public Artist updateArtist(Long id, Artist artist) {
        artist.setId(id);
        return artistRepository.save(artist);
    }

    public void deleteArtist(Long id) {
        artistRepository.deleteById(id);
    }

    public List<Artist> searchArtists(String query) {
        return artistRepository
            .findByFirstnameContainingOrLastnameContaining(query, query);
    }
}