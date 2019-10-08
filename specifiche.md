# Task 0

## Idea
Un sistema di messaggistica con degli utenti registrati che possono scambiarsi messaggi testuali e associarsi in gruppi.
Un utente semplice puo' decidere di iniziare una conversazione con un altro utente, puo' essere inserito all'interno di un gruppo da parte di un utente amministratore del gruppo (group admin), puo' inviare e ricevere messaggi con altri utenti e all'interno di un gruppo di cui fa parte, puo' uscire da un gruppo in cui e' stato invitato. Inoltre, puo' creare un gruppo (diventandone group admin). Non puo' cedere il potere di group admin e se esce dal gruppo, il gruppo viene cancellato. Puo' invitare e rimuovere utenti dal gruppo.
Ogni volta che l'utente accede ad una chat, sia di gruppo che personale, riceve i messaggi dal server e li visualizza in una schermata.

## Actors
Utente semplice, group admin e evento time-based.

## Specifiche funzionali

Utente semplice:

* Invio di un messaggio
* Lettura cronologia (tutti o solo non letti)
* Creazione utente
* Creazione chat
* Creazione gruppo

Group admin:

* Aggiunta utenti gruppo
* Rimozione utente gruppo
* Uscita gruppo => Cancellazione gruppo

Time-based event:

* Aggiornamento user-interface con nuovi messaggi ricevuti dall'utente connesso al server

---------------------
# Task 0

## Idea
A messaging system with registered users who can exchange text messages and connect in groups.
A simple user can decide to start a conversation with another user