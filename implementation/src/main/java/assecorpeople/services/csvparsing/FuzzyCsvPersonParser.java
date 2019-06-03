package assecorpeople.services.csvparsing;


import assecorpeople.entities.Color;
import assecorpeople.entities.Person;
import assecorpeople.persistence.ColorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
public class FuzzyCsvPersonParser implements CsvPersonParser {

    private static final Collection<String> possibleDelimiters = Arrays.asList(",", ";");

    private String delimiter;
    private Collection<IndexedFields> parsedFields = new LinkedList<>();
    private Collection<DirtyCsvLine> trash = new LinkedList<>();
    private HashMap<Long, Long> parsedEntriesPerLine = new HashMap<>();
    private HashMap<Long, Long> convertedEntriesPerLine = new HashMap<>();
    private long idFactorPerLine = 0L;
    private Collection<Person> parsedPersons = new LinkedList<>();
    private Logger logger = LoggerFactory.getLogger(FuzzyCsvPersonParser.class);
    private boolean parsed = false;


    public void parse(String location, ColorRepository colorRepository) throws IOException {
        logger.info("Parsing csv from {}", location);
        Collection<DirtyCsvLine> unsortedLines = indexedLinesFromFile(location);
        logger.debug("Read {} lines", unsortedLines.size());
        this.delimiter = guessDelimiterFromLines(unsortedLines);
        for (DirtyCsvLine dirtyCsvLine : unsortedLines) {
            retrieveAsManyDataFromLineAsPossibleAndAddLeftoversToTrash(dirtyCsvLine);
        }
        for (Long lineNumber : parsedEntriesPerLine.keySet()) {
            convertedEntriesPerLine.put(lineNumber, 0L);
        }
        long maxAmountOfEntriesPerLine = Collections.max(parsedEntriesPerLine.values());

        setIdFactorPerLineBasedOnMaxEntriesPerLine(maxAmountOfEntriesPerLine);
        for (IndexedFields indexedFields : this.parsedFields) {
            convertIndexedFieldsToPersonsOrAddToTrash(indexedFields, colorRepository);
        }
        parsed = true;
    }

    private void setIdFactorPerLineBasedOnMaxEntriesPerLine(long maxAmountOfEntriesPerLine) {
        idFactorPerLine = 1;
        while (idFactorPerLine < maxAmountOfEntriesPerLine) {
            idFactorPerLine *= 10;
        }
    }

    private void retrieveAsManyDataFromLineAsPossibleAndAddLeftoversToTrash(DirtyCsvLine dirtyCsvLine) {
        LineRecoverer lineRecoverer = new LineRecoverer(dirtyCsvLine);
        while (lineRecoverer.hasNext()) {
            retrieveNextEntryFromCsvLine(lineRecoverer);
        }
        if (lineRecoverer.trash != null) {
            this.trash.add(lineRecoverer.trash);
        }
    }

    private void retrieveNextEntryFromCsvLine(LineRecoverer lineRecoverer) {
        IndexedFields nextIndexedFields = lineRecoverer.next();
        long i = nextIndexedFields.getIndex();
        parsedFields.add(nextIndexedFields);
        if (!parsedEntriesPerLine.containsKey(i)) {
            parsedEntriesPerLine.put(i, 0L);
        }
        parsedEntriesPerLine.put(i, parsedEntriesPerLine.get(i) + 1);
    }

    @Override
    public Collection<Person> getPersons() {
        if (!parsed) {
            throw new IllegalStateException("Tried to call getPerson() before parsing a CSV.");
        }
        return new LinkedList<>(this.parsedPersons);
    }

    @Override
    public Collection<? extends IndexedString> getTrash() {
        if (!parsed) {
            throw new IllegalStateException("Tried to call getTrash() before parsing a CSV.");
        }
        return new LinkedList<>(this.trash);
    }

    private void convertIndexedFieldsToPersonsOrAddToTrash(IndexedFields indexedFields, ColorRepository colorRepository) {
        long id = nextIdForLineIndex(indexedFields.getIndex());
        List<String> fields = indexedFields.getFields();
        String lastname = fields.get(0).trim();
        String givenname = fields.get(1).trim();
        String zipcity = fields.get(2).trim();
        int firstSpaceIndex = zipcity.indexOf(" ");
        String zip = "";
        String city = zipcity;
        if (firstSpaceIndex != -1) {
            zip = zipcity.substring(0, firstSpaceIndex);
            city = zipcity.substring((firstSpaceIndex + 1));
        }
        long colorId = Long.parseLong(fields.get(3).trim());
        Optional<Color> colorOptional = colorRepository.findById(colorId);
        if (colorOptional.isEmpty()) {
            this.parsedPersons.add(new Person(id, lastname, givenname, zip, null, city));
        } else {
            Person person = new Person(id, lastname, givenname, zip, colorOptional.get(), city);
            this.parsedPersons.add(person);
        }
    }

    private long nextIdForLineIndex(long index) {
        long offset = convertedEntriesPerLine.get(index);
        convertedEntriesPerLine.put(index, offset + 1L);
        return index * idFactorPerLine + offset;
    }


    class LineRecoverer implements Iterator<IndexedFields> {

        private DirtyCsvLine workingLine;
        private final DirtyCsvLine unmodifiedOriginal;
        private boolean attemptedNextRecovery = false;
        private DirtyCsvLine nextToBeReturned = null;
        private DirtyCsvLine trash = null;

        private LineRecoverer(DirtyCsvLine workingLine) {
            this.workingLine = workingLine;
            this.unmodifiedOriginal = workingLine;
        }

        @Override
        public boolean hasNext() {
            if (!attemptedNextRecovery)
                attemptNextRecovery();
            return nextToBeReturned != null;
        }

        @Override
        public IndexedFields next() {
            if (!attemptedNextRecovery)
                attemptNextRecovery();
            ArrayList<String> fields = new ArrayList<>(Arrays.asList(nextToBeReturned.string.split(delimiter)));
            attemptedNextRecovery = false;
            return new IndexedFields(fields, nextToBeReturned.index, this.unmodifiedOriginal);
        }

        private void attemptNextRecovery() {
            // source should not be null... but hey, NPE
            if (workingLine == null) {
                nextToBeReturned = null;
                // Likely multiple lines in one line. try to split after color id
            } else if (this.workingLine.getOccurrencesOfMainDelimiter() > 3) {
                speculativelySplitAfterColorIdBeforeLetter();
                // Used mixed delimiters. will substitute with main delimiter and cut off after
            } else if (this.workingLine.getOccurrencesOfMainDelimiter() < 3 && this.workingLine.getOccurrencesOfAnyDelimiters() >= 3) {
                speculativelySplitOnAnyDelimiter();
                // a fine line as hoped for
            } else if (this.workingLine.getOccurrencesOfMainDelimiter() == 3) {
                nextToBeReturned = this.workingLine;
                this.workingLine = new DirtyCsvLine("", this.workingLine.index);
                // We are happyly done. the last whitespaces are unimportant
            } else if (this.workingLine.string.trim().isEmpty()) {
                trash = null;
                nextToBeReturned = null;
                workingLine = null;
                // No idea what to do. too dirty for recovery. Put it into trash to be picked up later.
            } else {
                trash = this.workingLine;
                nextToBeReturned = null;
                workingLine = null;
            }
            attemptedNextRecovery = true;
        }

        /**
         * Sets nextToBeReturned to a new DirtyCsvLine with the original index and a string built as follows:
         * From the very beginning, search for any possible delimiter as in defined in
         * FuzzyCsvPersonParser.possibleDelimiters. When found, substitute the delimiter with the detected main
         * delimiter for this FuzzyCsvPersonParser instance. Do this for the first 3 occurrences of any delimiter.
         * Sets the workingLine to a new DirtyCsvLine with the original index and empty string to end the iterator.
         */
        private void speculativelySplitOnAnyDelimiter() {
            int i = 0;
            String partOfLineNotPutIntoFieldsYet = this.workingLine.string;
            String rightSideString = partOfLineNotPutIntoFieldsYet;
            String leftSideString = "";
            List<String> fields = new ArrayList<>(4);
            OUTTER:
            while (i < partOfLineNotPutIntoFieldsYet.length()) {
                leftSideString = partOfLineNotPutIntoFieldsYet.substring(0, i);
                rightSideString = partOfLineNotPutIntoFieldsYet.substring(i);
                for (String aDelimiter : possibleDelimiters) {
                    if (rightSideString.startsWith(aDelimiter)) {
                        fields.add(leftSideString);
                        partOfLineNotPutIntoFieldsYet = rightSideString.substring(aDelimiter.length());
                        i = 0;
                        continue OUTTER;
                    }
                }
                i++;
            }
            fields.add(rightSideString);
            nextToBeReturned = new DirtyCsvLine(String.join(delimiter, fields), workingLine.index);
            workingLine = new DirtyCsvLine("", workingLine.index);
        }

        /**
         * Sets nextToBeReturned to a new DirtyCsvLine with the original index and a string built as follows:
         * After the third occurrence of delimiter, in the supposedly 4th field
         * Search for the first sequence of digits. After them, split the workingLine in two. The lefthandside
         * becomes the nextToBeReturned.
         * <p>
         * Sets the workingLine to a new DirtyCsvLine with the original index and the right-hand side as string
         * for further splitting and iterating.
         */
        private void speculativelySplitAfterColorIdBeforeLetter() {
            int i = 0;
            int currentFieldNr = 0;
            String line = workingLine.string;
            // Get into third field
            while (currentFieldNr < 3 && i < line.length()) {
                int index = line.indexOf(delimiter, i);
                if (index == -1) {
                    // We return null as we are not able to find the third field
                    throw new RuntimeException("Attempted to speculativelySplitAfterColorIdBeforeLetter in line with not enough delimiters. You should not have called this function");
                }
                currentFieldNr += 1;
                i = index + delimiter.length();
            }
            boolean isPastLastDigitOfColorId = false;
            boolean isOnColorId = false;
            // Get to color id
            while (i < line.length() && !isOnColorId) {
                i++;
                int asciiNr = (int) line.charAt(i);
                isOnColorId = asciiNr >= 48 && asciiNr <= 57;

            }
            // Get past color id
            while (i < line.length() && !isPastLastDigitOfColorId) {
                i++;
                int asciiNr = (int) line.charAt(i);
                isPastLastDigitOfColorId = !(asciiNr >= 48 && asciiNr <= 57);

            }
            // Make first part the next line returned and safe the leftovers for later
            this.nextToBeReturned = new DirtyCsvLine(line.substring(0, i), this.workingLine.index);

            String leftover = line.substring(i);
            this.workingLine = new DirtyCsvLine(leftover, this.workingLine.index);
        }

    }

    private static String guessDelimiterFromLines(Collection<DirtyCsvLine> lines) {
        Map<String, Long> delimiterOccurencesMap = occurrencesOfStringsInStrings(lines);
        String mostFrequentDelimiter = keyWithMaximumValue(delimiterOccurencesMap);
        return mostFrequentDelimiter;
    }

    private static <T> T keyWithMaximumValue(Map<T, Long> map) {
        T maxKey = null;
        Long maxVal = Long.MIN_VALUE;
        for (Map.Entry<T, Long> entry : map.entrySet()) {
            if (entry.getValue() >= maxVal) {
                maxKey = entry.getKey();
                maxVal = entry.getValue();
            }
        }
        return maxKey;
    }

    private static Map<String, Long> occurrencesOfStringsInStrings(Collection<DirtyCsvLine> lines) {
        Map<String, Long> delimiterOccurencesMap = new HashMap<>();
        for (String delimiter : possibleDelimiters) {
            delimiterOccurencesMap.put(delimiter, 0L);
        }
        for (DirtyCsvLine dirtyCsvLine : lines) {
            String line = dirtyCsvLine.string;
            for (String delimiter : possibleDelimiters) {
                int occurencesInLine = occurrencesOfSubstringInString(delimiter, line);
                delimiterOccurencesMap.put(delimiter, delimiterOccurencesMap.get(delimiter) + occurencesInLine);
            }
        }
        return delimiterOccurencesMap;
    }


    private static int occurrencesOfSubstringInString(String delimiter, String line) {
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = line.indexOf(delimiter, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += delimiter.length();
            }
        }
        return count;
    }

    private List<DirtyCsvLine> indexedLinesFromFile(String location) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(location));
        List<DirtyCsvLine> result = new LinkedList<>();
        long index = 0;
        String line = null;
        while ((line = br.readLine()) != null) {
            result.add(new DirtyCsvLine(line, index));
            index++;
        }
        return result;
    }

    private class DirtyCsvLine extends IndexedString {
        private Integer occurrencesOfMainDelimiter;
        private Integer occurrencesOfAnyDelimiters;

        @Override
        public String toString() {
            return "DirtyCsvLine{" +
                    "index=" + index + ", string='" + string + "\'}";
        }

        private int getOccurrencesOfMainDelimiter() {
            if (occurrencesOfMainDelimiter == null)
                countOccurrencesOfMainDelimiter();
            return occurrencesOfMainDelimiter;
        }

        private void countOccurrencesOfMainDelimiter() {
            occurrencesOfMainDelimiter = occurrencesOfSubstringInString(delimiter, this.string);
        }


        private int getOccurrencesOfAnyDelimiters() {
            if (occurrencesOfAnyDelimiters == null)
                countOccurrencesOfAnyDelimiters();
            return occurrencesOfAnyDelimiters;
        }

        private void countOccurrencesOfAnyDelimiters() {
            occurrencesOfAnyDelimiters = 0;
            for (String aDelimiter : possibleDelimiters) {
                occurrencesOfAnyDelimiters += occurrencesOfSubstringInString(aDelimiter, this.string);
            }
        }

        private DirtyCsvLine(String string, long index) {
            super(string, index);
        }

    }


    static class IndexedFields {
        private ArrayList<String> fields;
        private long index;
        private final DirtyCsvLine origin;

        public IndexedFields(ArrayList<String> fields, long index, DirtyCsvLine origin) {
            this.fields = fields;
            this.index = index;
            this.origin = origin;
        }

        public ArrayList<String> getFields() {
            return fields;
        }

        public long getIndex() {
            return index;
        }
    }
}
