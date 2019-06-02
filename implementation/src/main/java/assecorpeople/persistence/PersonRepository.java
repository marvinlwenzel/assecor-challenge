package assecorpeople.persistence;

import assecorpeople.entities.Color;
import assecorpeople.entities.Person;
import org.springframework.data.repository.Repository;

import java.util.Optional;


public interface PersonRepository extends Repository<Person, Long> {

    Iterable<Person> findByFavoriteColor(Color color);

    <S extends Person> S save(S entity);

    Optional<Person> findById(Long primaryKey);

    Iterable<Person> findAll();

    long count();

    boolean existsById(Long primaryKey);

    <S extends Person> Iterable<S> saveAll(Iterable<S> entities);
}
