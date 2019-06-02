package assecorpeople.controllers;


import assecorpeople.entities.Person;
import assecorpeople.services.PersonsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
public class PersonController {

    private final PersonsService personsService;

    @Autowired
    public PersonController(PersonsService personsService) {
        this.personsService = personsService;
    }

    @RequestMapping(value = "/persons", method = RequestMethod.GET)
    public Iterable<Person> allPersons() {
        return personsService.getAllPersons();
    }

    @RequestMapping(value = "/persons/{id}", method = RequestMethod.GET)
    public ResponseEntity<Person> personById(@PathVariable long id) {
        Optional<Person> result = personsService.personById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.get());
    }

    // POST? PUT? It's closer to the glory of REST than the Swamp of POX
    @RequestMapping(value = "/persons", method = RequestMethod.POST)
    public ResponseEntity<Person> createNewPerson(
            @RequestParam(name = "nachname") String lastname,
            @RequestParam(name = "vorname") String givenname,
            @RequestParam String zipcode,
            @RequestParam String city,
            @RequestParam(name = "color") String colorName,
            @RequestParam(required = false) Long id

    ) {
        Optional<Person> optionalPerson;
        if (id == null) {
            optionalPerson = personsService.createNewPerson(lastname, givenname, zipcode, city, colorName);
        } else {
            optionalPerson = personsService.createNewPerson(id, lastname, givenname, zipcode, city, colorName);
        }
        if (optionalPerson.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Person newPerson = optionalPerson.get();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(newPerson.getId()).toUri();
        return ResponseEntity.created(location).body(newPerson);
    }

    @RequestMapping(value = "/persons/color/{colorId}", method = RequestMethod.GET)
    public Iterable<Person> personsByColorId(@PathVariable long colorId) {
        return personsService.personsByColorId(colorId);
    }

}
