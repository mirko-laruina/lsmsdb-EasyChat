# Task 0: istruzioni per run
## Spring
Una volta compilato il server Java, si lancia Spring (che fornisce le API) da dentro la cartella del server con:

    mvn spring-boot:run
Il server risponde su `localhost:8080` .

## React
La webapp React, al momento, e' vuota. In ogni caso, per lanciarla (da dentro la cartella `frontend`):

    npm start
La webapp e' disponibile su `localhost:3000` .

## Git
Per evitare problemi, prima di fare un commit conviene fare un pull

    git pull origin master
    git add file1 file2 etc
    git commit -m "messaggio"
    git push origin master