# Passief Inkomen App — Roadmap & To Do

Laatste update: 2026-06-08

---

## Principe
De bestaande app (`www/index.html` + Capacitor Android) blijft **ongewijzigd en live** in de Play Store.
De nieuwe versie wordt in een aparte map ontwikkeld en pas overgehaald naar Android Studio
zodra bewezen is dat hij een volwaardige vervanging is.

---

## FASE 0 — Huidige versie naar de Play Store
> Doel: versie 1.16 live zetten voordat we beginnen met de nieuwe architectuur.

- [ ] Controleer `android/app/build.gradle`: zet `versionCode 16` en `versionName "1.16"`
- [ ] Commit de gewijzigde build.gradle naar GitHub
- [ ] Android Studio: **Gradle Sync** uitvoeren
- [ ] Android Studio: `cap sync` uitvoeren (of via terminal: `npx cap sync android`)
- [ ] Android Studio: **Build → Generate Signed Bundle / APK → Android App Bundle**
- [ ] Play Console: nieuwe release uploaden in het interne/productiekanaal
- [ ] Review indienen en wachten op goedkeuring

**Gereed wanneer:** versie 1.16 is zichtbaar als actieve release in de Play Console.

---

## FASE 1 — Nieuwe projectstructuur opzetten (apart van bestaande app)
> Doel: een schone Svelte + Vite + Capacitor basis neerzetten in een nieuwe map.
> De bestaande app wordt **niet aangeraakt**.

- [ ] Nieuwe map aanmaken: `passive-income-app-v2/`
- [ ] Svelte + Vite project initialiseren: `npm create vite@latest . -- --template svelte`
- [ ] Capacitor toevoegen: `npm install @capacitor/core @capacitor/cli`
- [ ] Capacitor initialiseren: `npx cap init`
- [ ] Android platform toevoegen: `npx cap add android`
- [ ] Bestaande Capacitor plugins overzetten:
  - `@capacitor-community/admob`
  - `@capacitor/local-notifications`
- [ ] Firebase SDK toevoegen (via npm, niet via CDN-script tags)
- [ ] Vite build testen: `npm run build` → `dist/` map aanwezig
- [ ] `npx cap sync` → Android map gevuld
- [ ] Controleer dat een lege app draait in Android Studio emulator

**Gereed wanneer:** een kale Svelte app opent op de Android emulator.

---

## FASE 2 — Componenten en routing bouwen
> Doel: de UI-structuur van de huidige app nabootsen in Svelte-componenten.

### Projectstructuur
```
src/
  lib/
    components/
      Header.svelte
      TabBar.svelte
      AccountCard.svelte
      AssetCard.svelte
      TimerCard.svelte
      LiveCard.svelte
      Modal.svelte
    screens/
      ScreenLive.svelte
      ScreenTimers.svelte
      ScreenAccounts.svelte
      ScreenAssets.svelte
      ScreenAnalytics.svelte
    stores/
      app.js          (globale state: accounts, assets, settings)
      auth.js         (Firebase auth state)
    firebase.js       (Firebase initialisatie)
    i18n.js           (vertalingen)
    utils.js          (fmtD, berekeningen)
  App.svelte          (root: routing + tabbar)
  main.js
```

### To do per onderdeel
- [ ] `firebase.js` — Firebase initialiseren met bestaande config
- [ ] `stores/auth.js` — inlogstatus bijhouden, Firestore sync
- [ ] `stores/app.js` — accounts, assets, settings, persist naar Firestore
- [ ] `i18n.js` — bestaande vertalingen overzetten (NL/EN/DE/FR/ES)
- [ ] `utils.js` — `fmtD()`, `accYrInc()`, `assetYrInc()`, `toDisp()` overzetten
- [ ] `App.svelte` — tab-navigatie, actief scherm wisselen
- [ ] `Header.svelte` — totaalbedrag, chips, taalswitch
- [ ] `TabBar.svelte` — vijf tabs met iconen
- [ ] `ScreenLive.svelte` — live teller, per-seconde update
- [ ] `ScreenTimers.svelte` — timers aanmaken en bijhouden
- [ ] `ScreenAccounts.svelte` — rekeningen accordeon
- [ ] `ScreenAssets.svelte` — vermogen cards
- [ ] `ScreenAnalytics.svelte` — grafieken, doelen, projectie
- [ ] Login / Register scherm
- [ ] AdMob integreren (reward ad + banner)
- [ ] Widget data plugin intact houden (Java-kant blijft ongewijzigd)

**Gereed wanneer:** alle schermen werken in de browser en data wordt correct opgeslagen in Firestore.

---

## FASE 3 — Vergelijking en validatie
> Doel: bewijzen dat de nieuwe versie een volwaardige vervanging is.

- [ ] Naast elkaar testen: bestaande app vs. nieuwe app op hetzelfde Firebase-account
- [ ] Controleer alle berekeningen op identieke uitkomsten
- [ ] Controleer vertalingen in alle 5 talen
- [ ] Controleer AdMob (reward ad, banner)
- [ ] Controleer widget data doorstroom
- [ ] Controleer notificaties
- [ ] Performance vergelijken: scroll, animaties, live teller
- [ ] Laat iemand anders (of jezelf op een schoon apparaat) de nieuwe versie gebruiken

**Gereed wanneer:** geen functionele regressies gevonden t.o.v. de huidige app.

---

## FASE 4 — Overzetten naar Android Studio en Play Store
> Doel: nieuwe versie vervangen de huidige in de Play Store.
> Dit is de enige fase waarbij de bestaande app wordt vervangen.

- [ ] `versionCode` en `versionName` ophogen (v2.0)
- [ ] Svelte build: `npm run build`
- [ ] `npx cap sync android`
- [ ] Android Studio: Gradle sync + Signed App Bundle bouwen
- [ ] Intern testen via Play Console (internal testing track)
- [ ] Goedkeuring → uitrollen naar productie
- [ ] Oude `www/index.html` archiveren (niet verwijderen)

---

## FASE 5 — Backend: Firebase Cloud Functions
> Doel: veilige backend voor toekomstige API-integraties.
> Bank API-sleutels mogen nooit in de frontend-app staan.

- [ ] Firebase CLI installeren: `npm install -g firebase-tools`
- [ ] `firebase init functions` in het project
- [ ] Eerste Cloud Function: scheduled dagelijkse sync (placeholder)
- [ ] Firestore Security Rules aanscherpen (users lezen/schrijven alleen eigen data)
- [ ] Secrets opslaan via Firebase Secret Manager (niet als env-variabele in code)

**Gereed wanneer:** een Cloud Function draait en Firestore rules zijn getest.

---

## FASE 6 — Bankkoppeling via Open Banking
> Doel: automatische saldo- en transactie-updates vanuit de bank.

**Provider:** GoCardless (voorheen Nordigen) — gratis tot 50 req/dag, ondersteunt ING/Rabo/ABN en 2300+ andere Europese banken.

- [ ] GoCardless account aanmaken op `bankaccountdata.com`
- [ ] Cloud Function: `initiateBank()` — start OAuth flow
- [ ] Cloud Function: `completeBankAuth()` — ontvangt token, slaat op in Firestore
- [ ] Cloud Function: `syncBankAccounts()` — haalt saldo + transacties op (dagelijks geplande taak)
- [ ] Frontend: "Koppel bank"-knop in Rekeningen-scherm
- [ ] Gekoppelde rekeningen tonen met automatisch bijgewerkt saldo
- [ ] Handmatige rekeningen blijven werken naast gekoppelde

---

## FASE 7 — Portfolio-integratie
> Doel: automatische portfolio-updates vanuit broker of tracking-app.

**Opties:**
- [ ] **DEGIRO**: CSV export importeren (makkelijkst om mee te beginnen)
- [ ] **Trade Republic**: PDF/CSV export
- [ ] **Getquin**: geen officiële API — alternatief is directe brokerkoppeling
- [ ] **Scalable Capital**: onderzoeken of API beschikbaar is

Aanpak: eerst handmatige CSV-import bouwen, daarna automatische sync als de broker een API biedt.

---

## Beslismoment per fase

| Fase | Start na | Go/No-go criterium |
|---|---|---|
| Fase 0 | Nu | build.gradle gecorrigeerd |
| Fase 1 | Na Fase 0 | Play Store versie 1.16 live |
| Fase 2 | Na Fase 1 | Svelte basis draait op emulator |
| Fase 3 | Na Fase 2 | Alle schermen gebouwd |
| Fase 4 | Na Fase 3 | Geen regressies gevonden |
| Fase 5 | Na Fase 4 | Nieuwe app live in Play Store |
| Fase 6 | Na Fase 5 | Cloud Functions operationeel |
| Fase 7 | Na Fase 6 | Bank-sync werkt stabiel |
