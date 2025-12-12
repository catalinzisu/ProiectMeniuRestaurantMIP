Restaurant "La Andrei" - Sistem de Gestiune

Acesta este proiectul meu de semestru pentru cursul de Medii și Instrumente de Programare. Este o aplicație desktop completă pentru administrarea unui restaurant, construită de la zero în Java.

Proiectul a evoluat pe parcursul a 7 iterații, plecând de la o simplă aplicație în consolă și ajungând la o interfață grafică modernă conectată la o bază de date reală.

Despre Proiect

Aplicația rezolvă problema haosului din restaurante prin digitalizare. În loc de carnețele și calcule manuale, totul e automatizat. Există trei moduri de utilizare, în funcție de cine folosește tableta:

1. Modul Client (Guest)

Este interfața pe care o văd clienții la masă.

Pot răsfoi meniul digital.

Filtrare inteligentă: Utilizatorii pot filtra produsele în funcție de preferințe (vegetarian, tip produs) sau buget.

Vizualizare detalii: Se pot consulta ingredientele, gramajul și prețul, însă plasarea comenzii este restricționată pentru acest rol.

2. Modul Ospătar (Staff)

Interfața de lucru pentru angajați (necesită autentificare).

Preluare rapidă a comenzilor.

Calculator automat de oferte: Sistemul aplică automat reguli de reducere complexe:

Happy Hour (reduceri la băuturi).

Party Pack (la 4 pizza comandate, una este gratuită).

Meal Deal (reduceri la desert la comandarea unui fel principal).

Vizualizare total de plată în timp real.

3. Modul Manager (Admin)

Panoul de control pentru administrator.

Gestiune personal (angajare/concediere).

Modificare meniu (CRUD - adăugare, editare, ștergere produse).

Activare/dezactivare rapidă a ofertelor globale.

Backup: Funcționalitate de export/import a meniului în format JSON pentru siguranța datelor.

Tehnologii Utilizate

Proiectul utilizează un stack modern de tehnologii Java:

Limbaj: Java 21 (utilizând facilități moderne precum record, switch expressions, sealed classes).

Interfață Grafică: JavaFX (arhitectură modulară).

Persistență: Hibernate (JPA) & PostgreSQL.

Dependency Management: Maven.

Procesare Date: Jackson (pentru manipularea fișierelor JSON).

Arhitectură și Design Patterns

Codul este structurat pentru a fi modular și ușor de întreținut, implementând următoarele modele de proiectare:

Builder Pattern: Utilizat pentru construirea obiectelor complexe (ex. clasa Pizza cu ingrediente opționale).

Strategy Pattern: Implementat pentru calculul dinamic al reducerilor, permițând adăugarea de noi reguli fără modificarea codului existent.

Singleton Pattern: Folosit pentru gestionarea eficientă a conexiunii la baza de date.

Repository Pattern: Separă logica de acces la date de interfața grafică.
