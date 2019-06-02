package assecorpeople.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "color")
public class Color {

    @Id
    @Column
    private Long id;

    @Column(unique = true)
    private String name;

    public Color(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Color() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color)) return false;
        Color color = (Color) o;
        return getId() == color.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
