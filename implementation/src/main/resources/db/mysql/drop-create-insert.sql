use assecorpeople1;

DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS color;


CREATE TABLE color(
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) UNIQUE
)ENGINE=INNODB;

CREATE TABLE person(
    id BIGINT PRIMARY KEY,
    firstname VARCHAR(200),
    lastname VARCHAR(200),
    zipcode VARCHAR(200),
    city VARCHAR(200),
    color_id BIGINT,
    FOREIGN KEY (color_id) REFERENCES color(id)
                    ON DELETE SET NULL
                    ON UPDATE CASCADE
)ENGINE=INNODB;

INSERT INTO color(id, name) VALUES
(1, 'Blau'),
(2, 'Grün'),
(3, 'Lila'),
(4, 'Rot'),
(5, 'Zitronengelb'),
(6, 'Türkis'),
(7, 'Weiß');
