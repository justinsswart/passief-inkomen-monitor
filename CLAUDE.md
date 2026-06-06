# Passief Inkomen App — Claude instructies

## Project
Capacitor Android app als één enkel HTML-bestand: `www/index.html`.
Firebase Auth + Firestore voor gebruikersdata.

## Regelantal check (elke sessie uitvoeren)
Tel het aantal regels in `www/index.html`. Huidig: ~2800 regels.
Zodra het **4000+ regels** bereikt → gebruiker herinneren aan de afgesproken refactor:
opsplitsen in `index.html` (shell) + `styles.css` + `app.js`.

## Update workflow (verplicht, altijd volgen)

1. Gebruiker beschrijft de gewenste wijziging
2. Ik pas `www/index.html` aan
3. Ik open een preview in de browser zodat de gebruiker het kan beoordelen
4. **Alleen na expliciete goedkeuring** van de gebruiker push ik naar GitHub

Nooit pushen zonder toestemming. Nooit stappen overslaan.
