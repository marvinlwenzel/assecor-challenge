package assecorpeople.services;

import assecorpeople.entities.Person;

import java.util.Optional;

//@Service
public interface PersonsService {

    Iterable<Person> getAllPersons();

    Optional<Person> personById(long id);

    Iterable<Person> personsByColorId(long colorId);

    Optional<Person>  createNewPerson(String nachname, String vorname, String zipcode, String city, String colorName);

    Optional<Person>  createNewPerson(long personId, String nachname, String vorname, String zipcode, String city, String colorName);
}
