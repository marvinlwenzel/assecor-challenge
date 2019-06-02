package assecorpeople.entities;

import assecorpeople.controllers.serializers.PersonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "person")
@JsonSerialize(using = PersonSerializer.class)
public class Person {

    @Id
    private Long id;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "firstname")
    private String givenName;

    @Column(name = "zipcode")
    private String zipCode;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = true)
    private Color favoriteColor;

    @Column(name = "city")
    private String city;

    public Person(long id, String lastName, String givenName, String zipCode, Color favoriteColor, String city) {
        this.id = id;
        this.lastName = lastName;
        this.givenName = givenName;
        this.zipCode = zipCode;
        this.favoriteColor = favoriteColor;
        this.city = city;
    }

    public Person() {
    }

    public String getCity() {
        return city;
    }

    public long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Color getFavoriteColor() {
        return favoriteColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return getId() == person.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", favoriteColor=" + favoriteColor +
                '}';
    }
}
