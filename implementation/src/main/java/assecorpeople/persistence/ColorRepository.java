package assecorpeople.persistence;

import assecorpeople.entities.Color;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ColorRepository extends Repository<Color, Long> {
    Optional<Color> findById(Long id);
    Optional<Color> findByName(String name);
}
