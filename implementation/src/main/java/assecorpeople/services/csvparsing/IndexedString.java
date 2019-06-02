package assecorpeople.services.csvparsing;

public class IndexedString {
    protected final String string;
    protected final long index;

    public IndexedString(String string, long index) {
        this.string = string;
        this.index = index;
    }

    public String getString() {
        return string;
    }

    public long getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "IndexedString{" +
                "index=" + index +
                ", string='" + string + '\'' +
                '}';
    }
}
