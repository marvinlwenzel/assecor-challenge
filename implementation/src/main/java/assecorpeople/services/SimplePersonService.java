package assecorpeople.services;

import assecorpeople.entities.Color;
import assecorpeople.entities.Person;
import assecorpeople.persistence.ColorRepository;
import assecorpeople.persistence.PersonRepository;
import assecorpeople.properties.PersonsParsingProperties;
import assecorpeople.services.csvparsing.CsvPersonParser;
import assecorpeople.services.csvparsing.IndexedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SimplePersonService implements PersonsService {

    private ColorRepository colorRepository;
    private PersonRepository personRepository;
    private AtomicLong nextPossiblePersonId = new AtomicLong(0L);
    private CsvPersonParser csvPersonParser;
    private PersonsParsingProperties personsParsingProperties;
    private Logger logger = LoggerFactory.getLogger(SimplePersonService.class);

    @Autowired
    public SimplePersonService(ColorRepository colorRepository, PersonRepository personRepository, CsvPersonParser csvPersonParser, PersonsParsingProperties personsParsingProperties) {
        this.colorRepository = colorRepository;
        this.personRepository = personRepository;
        this.csvPersonParser = csvPersonParser;
        this.personsParsingProperties = personsParsingProperties;
    }

    @PostConstruct
    public void init() throws IOException {
        parseCsvIfNecessary();
    }

    private void parseCsvIfNecessary() throws IOException {
        logger.info("Persons parse mode set to {}", personsParsingProperties.getMode());
        if (shouldParsePersonsFromCsv()) {
            csvPersonParser.parse(personsParsingProperties.getLocation(), colorRepository);
            saveAllParsedPersons();
            logTrash();
        } else {
            logger.info("Skipping parsing due to properties.");
        }
    }

    private boolean shouldParsePersonsFromCsv() {
        String mode = personsParsingProperties.getMode();
        if (mode.equals("import-no-wipe")) {
            return true;
        } else if (mode.equals("if-empty")) {
            if (personRepository.count() == 0) {
                return true;
            } else {
                return false;
            }
        } else if (mode.equals("never")) {
            return false;
        } else {
            throw new RuntimeException("Unknown mode for parsing persons csv: " + mode);
        }
    }

    private void saveAllParsedPersons() {
        Iterable<Person> parsedPersons = csvPersonParser.getPersons();
        logger.info("Number of Persons in Repository before adding CSV: {}", personRepository.count());
        personRepository.saveAll(parsedPersons);
        logger.info("Number of Persons in Repository after adding CSV: {}", personRepository.count());
    }

    private void logTrash() {
        Collection<? extends IndexedString> trash = csvPersonParser.getTrash();
        logger.warn("Number of trashed entries: {}", trash.size());
        for (IndexedString indexedString : trash) {
            logger.info("Can not parse {}", indexedString);
        }
    }

    @Override
    public Iterable<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @Override
    public Optional<Person> personById(long id) {
        return personRepository.findById(id);
    }

    @Override
    public Iterable<Person> personsByColorId(long colorId) {
        Optional<Color> optionalColor = colorRepository.findById(colorId);
        if (optionalColor.isEmpty()) {
            return Collections.emptyList();
        }
        return personRepository.findByFavoriteColor(optionalColor.get());
    }

    @Override
    @Transactional
    public Optional<Person> createNewPerson(String nachname, String vorname, String zipcode, String city, String colorName) {
        long personId = nextPersonIdAvailable();
        return createPersonWithGivenGoodId(nachname, vorname, zipcode, city, colorName, personId);
    }

    @Override
    @Transactional
    public Optional<Person> createNewPerson(long personId, String nachname, String vorname, String zipcode, String city, String colorName) {
        if (personRepository.existsById(personId)) {
            throw new IdOccupiedException("Person id " + personId + " is already occupied");
        }
        return createPersonWithGivenGoodId(nachname, vorname, zipcode, city, colorName, personId);
    }

    private Optional<Person> createPersonWithGivenGoodId(String nachname, String vorname, String zipcode, String city, String colorName, long personId) {
        Optional<Color> optionalColor = colorRepository.findByName(colorName);
        Person newPerson;
        if (optionalColor.isEmpty()) {
            newPerson = new Person(personId, nachname, vorname, zipcode, null, city);
        } else {
            newPerson = new Person(personId, nachname, vorname, zipcode, optionalColor.get(), city);
        }
        Person savedPerson = personRepository.save(newPerson);
        return Optional.of(savedPerson);
    }

    private long nextPersonIdAvailable() {
        while (true) {
            long maybeNextId = nextPossiblePersonId.getAndIncrement();
            if (!personRepository.existsById(maybeNextId)) {
                return maybeNextId;
            }
        }
    }


}
