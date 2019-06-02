package assecorpeople.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Pattern;

@Component
@ConfigurationProperties(prefix = "app.persons.parsing")
public class PersonsParsingProperties {


    private String location;

    @Pattern(regexp = "(import-no-wipe|if-empty|never)")
    private String mode = "import-no-wipe";

    public PersonsParsingProperties() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "AppProperties{" +
                "location='" + location + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
