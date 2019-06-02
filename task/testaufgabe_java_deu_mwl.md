# Testaufgabe - Entwickler

Umzusetzen ist eine REST-Schnittstelle mittels JEE oder SpringBoot, welche folgende Anforderungen erfüllt:


*   Über die Schnittstelle sollen Personen und ihre Lieblingsfarbe verwaltet werden.
*   Diese Daten sollen aus einer CSV-Datei ausgelesen werden, diese soll nicht angepasst werden.
*   Über die Schnittstelle können alle Personen mit einer spezifischen Lieblingsfarbe ermittelt werden.

Beispiel für eine CSV-Datei:

```
Müller, Hans, 67742 Lauterecken, 1
Petersen, Peter, 188439 Stralsund, 2
Johnsen, Johnny, 88888 Ausgedacht, 3
Millenium, Milly, 77777 Auch ausgedachtm, 4 Wenzel, Marvin Lukas, 14770 Brandenburg/Havel, 6
...
```

Die Zahlen aus der 4. Spalte entsprechen folgenden Farben:

```
1, Blau
2, Grün
3, Lila
4, Rot
5, Zitronengelb
6, Türkis
7, Weiß
```

## Schnittstellen

### GET /persons

Methode, um alle in der CSV vorhandenen User zu holen

```json
[
  {
    "id": 1,
    "vorname": "Hans",
    "nachname": "Müller",
    "zipcode": "67742",
    "city": "Lauterecken",
    "color": "Blau"
  },
  {
    "id": 2,
    "vorname": "Hans2",
    "nachname": "Müller2",
    "zipcode": "67742",
    "city": "Lauterecken2",
    "color": "Grün"
  }
]
```

### GET /persons/{id}

Ziehe spezifischen User. Id ist abhängig von Zeile in der CSV. ```id >= 0```

```json
{
  "id": 1,
  "vorname": "Hans",
  "nachname": "Müller",
  "zipcode": "67742",
  "city": "Lauterecken",
  "color": "Blau"
}
```

### GET /persons/color/{color}

Alle Personen mit gegebener Lieblingsfarbe.

```json
[
  {
    "id": 1,
    "vorname": "Hans",
    "nachname": "Müller",
    "zipcode": "67742",
    "city": "Lauterecken",
    "color": "Blau"
  },
  {
    "id": 10,
    "vorname": "Hanas2",
    "nachname": "Müllaer2",
    "zipcode": "67742",
    "city": "Lautereckaaaen2",
    "color": "Blau"
  }
]
```

## Aufgaben

1.  Auslesen der CSV-Datei und Zwischenspeichern in einer zum JSON passenden Model-Klassen ***(selbst zu implementieren)***. Diese soll im Idealfall in einer Klasse geschehen, welche den Zugriff auf die Datei wegkapselt, um diese später einfach durch eine Datenbank ersetzen zu können.
2.  Implementieren der angegebenen REST-Schnittstelle. Diese greift per Dependency Injection auf die Persistenz-Klasse zu.
3.  Schreiben sie passende Unit-Tests für die Schnittstellen (z.B. testGetUsersWithColor(), testGetAllUsers())
4.  Bonus-Aufgaben:
    1.  Umsetzung als Maven-Projekt
    2.  Implementieren einer zusätzlichen ```POST /persons``` welche einen neuen Datensatz zur CSV-Datei hinzufügt.
    3.  JPA-Anbindung zu einer MySQL-Datenbank zur Persistierung der Daten.
