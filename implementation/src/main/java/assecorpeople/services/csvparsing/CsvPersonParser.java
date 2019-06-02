package assecorpeople.services.csvparsing;

import assecorpeople.entities.Person;
import assecorpeople.persistence.ColorRepository;

import java.io.IOException;
import java.util.Collection;

public interface CsvPersonParser {

    void parse(String fileLocation, ColorRepository colorRepository) throws IOException;

    Collection<Person> getPersons();

    Collection<? extends IndexedString> getTrash();
}
