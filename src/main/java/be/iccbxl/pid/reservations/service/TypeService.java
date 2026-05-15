package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.Type;
import be.iccbxl.pid.reservations.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour gérer les types d'artistes.
 * Encapsule la logique au-dessus du repository.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // lecture par défaut, on override pour les écritures
public class TypeService {

    private final TypeRepository typeRepository;

    /**
     * Récupère tous les types disponibles.
     */
    public List<Type> findAll() {
        return typeRepository.findAll();
    }

    /**
     * Recherche par ID.
     */
    public Optional<Type> findById(Long id) {
        return typeRepository.findById(id);
    }

    /**
     * Recherche par libellé.
     */
    public Optional<Type> findByType(String type) {
        return typeRepository.findByType(type);
    }

    /**
     * Crée un nouveau type s'il n'existe pas déjà.
     * Renvoie le type existant ou nouvellement créé.
     */
    @Transactional
    public Type findOrCreate(String typeLabel) {
        return typeRepository.findByType(typeLabel)
                .orElseGet(() -> typeRepository.save(new Type(typeLabel)));
    }
}