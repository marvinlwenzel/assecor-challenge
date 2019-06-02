package assecorpeople.unit;


import assecorpeople.entities.Color;
import assecorpeople.entities.Person;
import assecorpeople.persistence.inmemory.InMemoryPersonRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class InMemoryPersonRepositoryTest {

    private InMemoryPersonRepository inMemoryPersonRepository;

    public InMemoryPersonRepositoryTest() {

    }
    //long id, String lastName, String givenName, String zipCode, Color favoriteColor, String city

    @Before
    public void init() {
        inMemoryPersonRepository = new InMemoryPersonRepository();
        inMemoryPersonRepository.save(
                new Person(1L,
                        "LastName1",
                        "GivenName1",
                        "12341",
                        new Color(1, "Blau"),
                        "City1"));
        inMemoryPersonRepository.save(
                new Person(2L,
                        "LastName2",
                        "GivenName2",
                        "12341",
                        new Color(2, "Grün"),
                        "City1"));
    }

    @Test
    public void testReadAll() {
        Iterable<Person> result = inMemoryPersonRepository.findAll();
        Collection<Person> resultCollection = new LinkedList<>();
        result.forEach(resultCollection::add);
        assertThat(resultCollection, hasSize(2));
    }

    @Test
    public void testReadByIdValidId() {
        Long id = 1L;
        Optional<Person> optionalPerson = inMemoryPersonRepository.findById(id);
        assertThat(optionalPerson.isPresent(), is(true));
        assertThat(optionalPerson.get().getId(), is(id));
    }

    @Test
    public void testReadByIdInvalidId() {
        Long id = -1L;
        Optional<Person> optionalPerson = inMemoryPersonRepository.findById(id);
        assertThat(optionalPerson.isPresent(), is(false));
    }

    @Test
    public void testReadByColorValidcolor() {
        Color c = new Color(1, "Blau");
        Iterable<Person> result = inMemoryPersonRepository.findByFavoriteColor(c);
        Collection<Person> resultCollection = new LinkedList<>();
        result.forEach(resultCollection::add);
        assertThat(resultCollection, hasSize(1));
        for (Person p : resultCollection) {
            assertThat(p.getFavoriteColor(), is(not(nullValue())));
            assertThat(p.getFavoriteColor().getName(), is("Blau"));
        }

    }

    @Test
    public void testReadByColorInvalidColor() {
        Color c = new Color(-1, "xtcfbhnjk,lö");
        Iterable<Person> result = inMemoryPersonRepository.findByFavoriteColor(c);
        Collection<Person> resultCollection = new LinkedList<>();
        result.forEach(resultCollection::add);
        assertThat(resultCollection, hasSize(0));
    }

    @Test
    public void testCount() {
        assertThat(inMemoryPersonRepository.count(), is(2L));
    }

    @Test
    public void testSave() {
        Person newPerson = new Person(1337L,
                "LastName1337",
                "GivenName1337",
                "12341",
                new Color(1, "Blau"),
                "City1337");


        long amountBefore = inMemoryPersonRepository.count();
        inMemoryPersonRepository.save(newPerson);
        assertThat(inMemoryPersonRepository.count(), is(amountBefore + 1));
        Color c = new Color(1, "Blau");
        Iterable<Person> result = inMemoryPersonRepository.findByFavoriteColor(c);
        Collection<Person> resultCollection = new LinkedList<>();
        result.forEach(resultCollection::add);
        assertThat(resultCollection, hasSize(2));
    }


}
