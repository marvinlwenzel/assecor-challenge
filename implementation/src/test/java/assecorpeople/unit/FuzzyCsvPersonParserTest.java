package assecorpeople.unit;

import assecorpeople.persistence.ColorRepository;
import assecorpeople.services.csvparsing.FuzzyCsvPersonParser;
import assecorpeople.entities.Person;
import assecorpeople.services.csvparsing.IndexedString;
import assecorpeople.unit.mocks.MockInMemoryTestColorRepository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FuzzyCsvPersonParserTest {

    @Test
    public void testRead() throws IOException {
        System.out.println(System.getProperty("java.version"));
        ColorRepository colorRepository = new MockInMemoryTestColorRepository();
        FuzzyCsvPersonParser fuzzyCsvPersonParser = new FuzzyCsvPersonParser();
        fuzzyCsvPersonParser.parse("/home/mlw/dev/assecor-challenge-mlw/implementation/src/test/resources/persons_clean_short.csv", colorRepository);
        Collection<Person> parsedPaersons = fuzzyCsvPersonParser.getPersons();
        Collection<? extends IndexedString> trash = fuzzyCsvPersonParser.getTrash();

        assertThat(parsedPaersons, is(not(nullValue())));
        assertThat(parsedPaersons, hasSize(7));
        assertThat(trash, is(not(nullValue())));
        assertThat(trash, is(empty()));

    }


    @Test
    public void testReadBad() throws IOException {
        ColorRepository colorRepository = new MockInMemoryTestColorRepository();
        FuzzyCsvPersonParser fuzzyCsvPersonParser = new FuzzyCsvPersonParser();
        fuzzyCsvPersonParser.parse("/home/mlw/dev/assecor-challenge-mlw/implementation/src/test/resources/persons_dirty_linescramble_medium.csv",colorRepository);
        Collection<Person> parsedPaersons = fuzzyCsvPersonParser.getPersons();
        Collection<? extends IndexedString> trash = fuzzyCsvPersonParser.getTrash();

        assertThat(parsedPaersons, is(not(nullValue())));
        assertThat(parsedPaersons, hasSize(48));
        assertThat(trash, is(not(nullValue())));
        assertThat(trash, hasSize(2));
    }



}
