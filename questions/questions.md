# Fragen

Einige Fragen haben Einfluss auf Andere. Perfekt kriege ich die nicht getrennt. Zudem sind einige Fragen deutlich schwerwiegender als Andere. In Kontext eines Projektes mit anderen Entwicklern für einem Kunden will ich nur alles einmal ausgesprochen haben.  

## Generelle Fragen

1.  Wie soll der Code abgegeben werden? Derzeit hoste ich ein privates Git-Repository On-Premise, wofür ich gerne die Zugangsdaten (Repo-Link, Nutzername, SSH-Key) zur Verfügung stellen kann. Ich kann das ganze auch auf ein privates GitHub-Repository migrieren. Alternativ die klassiische ZIP oder TarBall?
2.  Gibt es Einschränkung in der Auswahl der JVM, Java-Version, SpringBoot-Version, etc?
3.  Gibt es weitere Einschränkungen in der Nutzung von Bibliotheken? Generell keine; generell erlaubt; nur Lizenzen, die Commercial Use erlauben?
4.  Gibt es Einschränkung, wie das Farb-FarbID-Mapping implementiert werden soll?
5.  Deployment. Wie weit soll man sich darum gedanken machen. Das ganze könnte man noch bis ins Continous Deployment bringen (Jenkins, TravisCI, ...), nur irgendwann wächst das meiner Einsicht nach Out Of Scope.
6.  Naming Conventions. Die Beschreibung ist deutsch, die REST-Endpunkte sind englisch: in welcher Sprache soll der Code sein? Die Domänen-Objekte werden mit verschiedenen Begriffen gehandhabt ("User" und "Person"). Freie Wahl der Benennungen oder gibt es Präferenzen?

## CSV-Datei

1.  Ist die CSV valide, oder fehlgebildet wie im Beispiel in der PDF? Die PDF enthält ein Beispiel (das Erste, Seite 1) das eine einzige Zeile mit mehreren Datensätzen enthält, ohne Zeilenumbruch.
2.  Sind fehlerhafte CSV-Dateien zu erwarten? Wenn ja, soll versucht werden, so viele Daten zu interpretieren, wie möglich? Soll die Anwendung starten, die Schnittstellen bedienen und interner Server-Fehler melden? Soll die Anwendung abbrechen?
3.  Wird die CSV nach Start der Anwendung modifiziert von außerhalb?
4.  Welche Größenordnung kann die CSV Datei erreichen?
5.  Soll die Anwendung "dreckige" Datensätze antizipieren? Z.B. leere Felder, fehlenden/zusätzliche Ziffern in der PLZ, ...
6.  Welche/s Encoding/s?
7.  Muss Variabilität des Formats (mit/ohne Header-Zeile, wechselnde Trennzeichen) gearbeitet werden? Ist eine Einstelllung in einer Config als Lösung angebracht? Soll ohne Konfiguration pro CSV jede Möglichkeit betrachtet werden?

## Schnittstellen

1.  ```POST /persons```
    1.  Was nimmt die Schnittstelle entgegen? Eine einzelne Person ist in der Aufgabe formuliert, aber nicht das Format: Request-Parameter, eine JSON, eine einzelne Zeile CSV?
2.  ```GET /persons/color/{color}```
    1.  Ist die Farbe per ID oder per Name referenziert?
