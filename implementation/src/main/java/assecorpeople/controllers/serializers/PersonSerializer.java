package assecorpeople.controllers.serializers;

import assecorpeople.entities.Person;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;


public class PersonSerializer extends StdSerializer<Person> {

    public PersonSerializer() {
        this(null);
    }

    public PersonSerializer(Class<Person> t) {
        super(t);
    }

    @Override
    public void serialize(Person person, JsonGenerator jGen, SerializerProvider serializerProvider) throws IOException {
        jGen.writeStartObject();
        jGen.writeNumberField("id", person.getId());
        jGen.writeStringField("vorname", person.getGivenName());
        jGen.writeStringField("nachname", person.getLastName());
        jGen.writeStringField("zipcode", person.getZipCode());
        jGen.writeStringField("city", person.getCity());
        if (person.getFavoriteColor() == null){
            jGen.writeStringField("color", null);
        } else {
            jGen.writeStringField("color", person.getFavoriteColor().getName());
        }
        jGen.writeEndObject();
    }
}
