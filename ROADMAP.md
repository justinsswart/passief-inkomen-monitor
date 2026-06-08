# Passief Inkomen App — Roadmap & To Do

Laatste update: 2026-06-08 (UI-herinrichting besluit verwerkt)

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
> Doel: een **verbeterde** UI-structuur bouwen in Svelte — niet 1-op-1 nagebouwd,
> maar heringedeeld op basis van wat de nieuwe architectuur mogelijk maakt.

### Besluit: nieuwe tabindeling (4 tabs i.p.v. 5)

| Tab | Oud | Nieuw |
|---|---|---|
| 📊 | Live | Live **+ Timers** (uitklapbaar onderaan) |
| 💼 | Rekeningen | **Portfolio** (rekeningen + vermogen gecombineerd) |
| 📈 | Vermogen | *(vervalt als aparte tab)* |
| 📊 | Analyse | Analyse **met echte grafieken** (ApexCharts) |
| ⚙️ | *(geen)* | **Instellingen** (nieuw — taal, valuta, thema, account) |
| ⏱ | Timers | *(vervalt als aparte tab → gaat in Live-scherm)* |

**Rationale:**
- Timers zijn contextgebonden aan de live-teller → horen bij hetzelfde scherm
- Rekeningen en Vermogen zijn beide "bezittingen" → één Portfolio-scherm is logischer
- Analyse verdient echte interactieve grafieken nu dat mogelijk is via npm-pakketten
- Instellingen waren nergens goed vindbaar → verdienen een eigen tab
- De vrijgekomen tab (Fase 6) → wordt "Bank" wanneer GoCardless is geïntegreerd

### Projectstructuur (definitief)
```
src/
  lib/
    components/
      Header.svelte         (totaalbedrag + chips)
      TabBar.svelte         (4 tabs)
      AccountCard.svelte    (rekening-kaart, gebruikt in Portfolio)
      AssetCard.svelte      (vermogen-kaart, gebruikt in Portfolio)
      TimerCard.svelte      (timer-kaart, gebruikt in Live)
      Modal.svelte          (gedeelde modal-shell)
      DonutChart.svelte     (SVG-donut voor Portfolio en Analyse)
    screens/
      ScreenLive.svelte     (live teller + uitklapbare timers)
      ScreenPortfolio.svelte(rekeningen + vermogen, segment-control + donut)
      ScreenAnalytics.svelte(ApexCharts: lijn, bar, donut, doel)
      ScreenSettings.svelte (taal, valuta, thema, notificaties, account)
      AuthScreen.svelte     (login / register / reset)
    stores/
      app.js                (accounts, assets, timers, history-snapshots)
      auth.js               (Firebase auth state)
    firebase.js
    i18n.js
    utils.js
    constants.js
  App.svelte
  main.js
```

### Extra: datamodel-uitbreiding voor grafieken
De Analyse-tab heeft tijdreeksdata nodig (inkomen door de tijd).
Dit vereist een kleine toevoeging aan het datamodel:

```js
// In Firestore: users/{uid}
history: [
  { date: '2026-06-08', totalYrInc: 4200, netWorth: 82000 },
  // max 365 entries, oudste wordt overschreven
]
```

Strategie: bij elke app-open controleren of de laatste snapshot ouder is dan 24u.
Zo ja → huidige waarden wegschrijven. Geen extra user-actie nodig.

### Chart-bibliotheek: ApexCharts
- `npm install apexcharts` (puur JS, geen React-dependency)
- Uitstekende mobiele touch-support (pinch, swipe)
- Theming via CSS-variabelen mogelijk
- Bundlegrootte (~450 KB) is acceptabel voor een Capacitor-app (zit in de APK)
- Benodigde charts:
  - **Lijn**: inkomen per maand over tijd (uit history-snapshots)
  - **Donut**: vermogensverdeling rekeningen vs. assets vs. categorieën
  - **Bar (horizontaal)**: inkomen per bron (vervangt huidige CSS-bars)
  - **Radial gauge**: doel-voortgang (vervangt huidige progress-bar)

### To do per onderdeel

**Foundation (al gedaan ✅)**
- [x] `firebase.js` — Firebase v9 modular SDK
- [x] `stores/auth.js` — login/logout/register
- [x] `stores/app.js` — accounts, assets, timers, persist, syncToCloud
- [x] `i18n.js` — NL/EN/DE/FR/ES vertalingen
- [x] `utils.js` — alle rekenfuncties overgezet
- [x] `constants.js` — CURRENCIES, ASSET_CATS, THEMES
- [x] `App.svelte` — tab-routing + auth check
- [x] `Header.svelte` — totaalbedrag + chips
- [x] `TabBar.svelte` — tabs met actieve state
- [x] `AuthScreen.svelte` — login/register/reset

**Live-scherm (timers geïntegreerd)**
- [ ] `ScreenLive.svelte` uitbreiden — uitklapbaar timers-blok onderaan
- [ ] `TimerCard.svelte` — herbruikbaar timer-component
- [ ] Svelte `slide`-transitie voor uitklappen/inklappen

**Portfolio-scherm (rekeningen + vermogen)**
- [ ] `ScreenPortfolio.svelte` — segment-control "Rekeningen" / "Vermogen"
- [ ] `AccountCard.svelte` — rekening-kaart met accordeon + mutatie-support
- [ ] `AssetCard.svelte` — asset-kaart met portfolio-posities support
- [ ] `DonutChart.svelte` — SVG-donut voor vermogensverdeling bovenaan
- [ ] Mutaties toevoegen/bewerken volledig werkend
- [ ] Portfolio-posities (aandelen) toevoegen/bewerken

**Analyse-scherm (echte grafieken)**
- [ ] `npm install apexcharts`
- [ ] History-snapshot systeem bouwen in `stores/app.js`
- [ ] `ScreenAnalytics.svelte` herbouwen met ApexCharts
  - [ ] Lijndiagram: jaarinkomen over tijd
  - [ ] Donutdiagram: verdeling per bron
  - [ ] Horizontale staafgrafiek: top-inkomstenbronnen
  - [ ] Radiale gauge: doel-voortgang
  - [ ] Mijlpalen-overzicht

**Instellingen-scherm**
- [ ] `ScreenSettings.svelte` — taal, valuta, thema-kiezer
- [ ] Notificatie-instellingen (frequentie, aan/uit)
- [ ] Account-info + uitloggen
- [ ] Exporteren (JSON backup van alle data)

**Overig**
- [ ] AdMob integreren (reward ad + banner)
- [ ] Widget data plugin intact houden (Java-kant blijft ongewijzigd)
- [ ] Lokale notificaties via `@capacitor/local-notifications`

**Gereed wanneer:** alle 4 tabs werken in de browser, grafieken tonen data,
instellingen worden opgeslagen, en Firestore-sync is bevestigd.

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
> Prioriteit op Getquin omdat die al dividenddata bijhoudt per positie.

### Getquin — onderzoek (2026-06-08)

**Wat Getquin biedt dat relevant is:**
- Dividendoverzicht per aandeel (bedrag, ex-datum, betaaldatum)
- Portfolio-posities (ticker, aantal, aankoopprijs)
- Performance per positie

**Koppelingsopties:**

| Optie | Haalbaarheid | Aanpak |
|---|---|---|
| Officiële API | ❌ Geen publieke API | n.v.t. |
| CSV-export | ✅ Getquin heeft export-functie | Importeer in app via bestandskiezer |
| Web scraping | ⚠️ Mogelijk, maar breekt bij UI-wijzigingen | Cloud Function die inlogt als gebruiker — **risicovol** |
| Broker-koppeling | ✅ Beter alternatief | DEGIRO/TR API rechtstreeks (dezelfde data als Getquin, maar stabieler) |

**Aanbevolen aanpak voor Getquin:**
1. **CSV-import** als eerste stap — Getquin laat transacties/dividenden exporteren als CSV.
   De app leest dit in via `@capacitor/filesystem` + een bestandskiezer.
   Posities en dividenden worden ingelezen en omgezet naar het app-datamodel.
2. **Geen scraping** — te fragiel en in strijd met Getquin's gebruiksvoorwaarden.
3. **Toekomst**: als Getquin ooit een API openstelt, is de Cloud Function klaar om die te gebruiken.

**CSV-import flow (te bouwen in Fase 7):**
```
Getquin → Export CSV → Gebruiker kiest bestand in app
→ Cloud Function of frontend parseert CSV
→ Posities worden toegevoegd aan bestaande assets (category: 'stocks')
→ Dividenden worden toegevoegd als mutaties
```

**Overige brokers:**
- [ ] **DEGIRO**: CSV export importeren (transactie-overzicht + dividendhistorie)
- [ ] **Trade Republic**: PDF/CSV export (minder gestructureerd)
- [ ] **Scalable Capital**: onderzoeken of API beschikbaar is

Aanpak: eerst CSV-import voor Getquin + DEGIRO, daarna automatische sync als een broker een API biedt.

---

## Beslismoment per fase

| Fase | Start na | Go/No-go criterium |
|---|---|---|
| Fase 0 | Nu | build.gradle gecorrigeerd ✅ |
| Fase 1 | Na Fase 0 | Play Store versie 1.16 live ✅ |
| Fase 2 | Na Fase 1 | Svelte basis draait op emulator ✅ (foundation klaar) |
| Fase 3 | Na Fase 2 | Alle 4 tabs + grafieken werken in browser |
| Fase 4 | Na Fase 3 | Geen regressies gevonden t.o.v. v1 |
| Fase 5 | Na Fase 4 | Nieuwe app live in Play Store |
| Fase 6 | Na Fase 5 | Cloud Functions operationeel |
| Fase 7 | Na Fase 6 | Bank-sync werkt stabiel |
| Fase 7b | Parallel aan Fase 7 | Getquin CSV-import werkt |
