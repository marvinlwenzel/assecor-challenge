package assecorpeople.controllers;

import assecorpeople.properties.PersonsParsingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private PersonsParsingProperties personsParsingProperties;

    @Autowired
    public HelloController(PersonsParsingProperties personsParsingProperties) {
        this.personsParsingProperties = personsParsingProperties;
    }

    @RequestMapping("/")
    public String index() {
        return "Hello Assecor!";
    }

    @RequestMapping("/datacsv")
    public String datacsv() {
        return personsParsingProperties.getLocation();
    }

}
