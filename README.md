Restaurant "La Andrei" - Sistem de Gestiune

Acesta este proiectul meu de semestru pentru cursul de Medii și Instrumente de Programare. Este o aplicație desktop completă pentru administrarea unui restaurant, construită de la zero în Java.

Proiectul a evoluat pe parcursul a 7 iterații, plecând de la o simplă aplicație în consolă și ajungând la o interfață grafică modernă conectată la o bază de date reală.

Despre Proiect

Aplicația rezolvă problema haosului din restaurante prin digitalizare. În loc de carnețele și calcule manuale, totul e automatizat. Există trei moduri de utilizare, în funcție de cine folosește tableta:

1. Modul Client (Guest)

Este interfața pe care o văd clienții la masă.

Pot răsfoi meniul digital.

Filtrare inteligentă: Dacă ești vegetarian sau ai un buget fix, aplicația îți arată doar ce poți mânca.

Văd poze și detalii (gramaj, ingrediente), dar nu pot trimite comanda direct la bucătărie (pentru a evita glumele proaste).

2. Modul Ospătar (Staff)

Interfața de lucru pentru angajați (necesită login).

Preluare comenzi rapidă.

Calculator automat de oferte: Nu trebuie să calculeze nimeni reducerile manual. Sistemul aplică singur:

Happy Hour (reduceri la băuturi).

Party Pack (iei 4 pizza, plătești 3).

Meal Deal (reduceri la desert).

Văd totalul de plată în timp real.

3. Modul Manager (Admin)

Panoul de control pentru proprietar.

Poate angaja sau concedia ospătari.

Poate modifica meniul (adăugare/ștergere produse) direct din aplicație.

Are butoane simple pentru a activa/dezactiva ofertele zilei.

Backup: Poate exporta/importa meniul în format JSON ca să nu piardă datele.

Ce tehnologii am folosit

Am vrut să folosesc un stack modern de Java:

Java 21: Am folosit feature-uri noi precum record, switch expressions și sealed classes.

JavaFX: Pentru interfața grafică (folosind o arhitectură modulară, nu tot codul într-un singur fișier).

Hibernate (JPA) & PostgreSQL: Pentru a salva datele permanent într-o bază de date SQL.

Maven: Pentru gestionarea dependențelor.

Jackson: Pentru lucrul cu fișiere JSON.

Design Patterns (Structura codului)

Ca să nu iasă un cod "spaghetti", am implementat câteva modele de proiectare:

Builder Pattern: Folosit la Pizza, pentru că are multe ingrediente opționale.

Strategy Pattern: Pentru reduceri. Pot adăuga o regulă nouă de reducere fără să stric restul codului.

Singleton: Pentru conexiunea la baza de date (să nu deschid 100 de conexiuni degeaba).

Repository Pattern: Separă logica de salvare de interfața grafică.
