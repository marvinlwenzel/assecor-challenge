package assecorpeople.persistence.inmemory;

import assecorpeople.entities.Color;
import assecorpeople.persistence.ColorRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryColorRepository implements ColorRepository {

    private final Map<Long, Color> colors = new HashMap<>();
    private final Map<String, Color> colorsByName = new HashMap<>();

    public InMemoryColorRepository() {
        Color c1 = new Color(1L, "Blau");
        colors.put(1L, c1);
        colorsByName.put("Blau", c1);
        Color c2 = new Color(2L, "Grün");
        colors.put(2L, c2);
        colorsByName.put("Grün", c2);
        Color c3 = new Color(3L, "Lila");
        colorsByName.put("Lila", c3);
        colors.put(3L, c3);
        Color c4 = new Color(4L, "Rot");
        colorsByName.put("Rot", c4);
        colors.put(4L, c4);
        Color c5 = new Color(5L, "Zitronengelb");
        colorsByName.put("Zitronengelb", c5);
        colors.put(5L, c5);
        Color c6 = new Color(6L, "Türkis");
        colorsByName.put("Türkis", c6);
        colors.put(6L, c6);
        Color c7 = new Color(7L, "Weiß");
        colorsByName.put("Weiß", c7);
        colors.put(7L, c7);
    }

    @Override
    public Optional<Color> findById(Long id) {
        Color c = this.colors.get(id);
        if (c == null){
            return Optional.empty();
        } else {
            return Optional.of(c);
        }
    }

    @Override
    public Optional<Color> findByName(String name) {
        Color c = this.colorsByName.get(name);
        if (c == null){
            return Optional.empty();
        } else {
            return Optional.of(c);
        }
    }
}
