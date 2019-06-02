package assecorpeople.persistence.inmemory;

import assecorpeople.entities.Color;
import assecorpeople.entities.Person;
import assecorpeople.persistence.PersonRepository;

import java.util.*;

public class InMemoryPersonRepository implements PersonRepository {

    private Map<Long, Person> persons = new HashMap<>();
    private Map<Long, Collection<Person>> personsByColorId = new HashMap<>();


    @Override
    public Iterable<Person> findByFavoriteColor(Color color) {
        return personsByColorId.getOrDefault(color.getId(), Collections.emptyList());
    }

    @Override
    public <S extends Person> S save(S entity) {
        persons.put(entity.getId(), entity);
        if (entity.getFavoriteColor() != null) {
            long colorId = entity.getFavoriteColor().getId();
            if (!personsByColorId.containsKey(colorId)) {
                personsByColorId.put(colorId, new LinkedList<>());
            }
            personsByColorId.get(colorId).add(entity);
        }
        return entity;
    }

    @Override
    public Optional<Person> findById(Long primaryKey) {
        if (persons.containsKey(primaryKey)) {
            return Optional.of(persons.get(primaryKey));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Person> findAll() {
        return persons.values();
    }

    @Override
    public long count() {
        return persons.size();
    }

    @Override
    public boolean existsById(Long primaryKey) {
        return persons.containsKey(primaryKey);
    }

    @Override
    public <S extends Person> Iterable<S> saveAll(Iterable<S> entities) {
        Collection<S> savedEntites = new LinkedList<>();
        for (S entity : entities){
            savedEntites.add(save(entity));
        }
        return savedEntites;
    }
}
