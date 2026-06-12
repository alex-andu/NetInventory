# NetInventory

Aplicație Android pentru **inventarierea echipamentelor de rețea prin cod QR**. Fiecare echipament fizic (switch, router, firewall, server, UPS etc.) primește o etichetă QR unică. Scanezi eticheta cu telefonul și aplicația deschide instant fișa echipamentului: model, serial, poziție în rack, IP, VLAN-uri, firmware, istoric de mentenanță și incidente.

> **Totul rulează 100% local pe telefon.** Datele sunt salvate într-o bază de date SQLite pe dispozitiv (prin Room). Nu există server, backend sau API extern — aplicația funcționează complet offline. Singura permisiune cerută este camera, folosită exclusiv pentru scanarea codurilor QR.

## Funcționalități

- **Scanare QR** → deschide direct fișa echipamentului asociat etichetei.
- Dacă scanezi un cod **necunoscut**, aplicația propune crearea unui echipament nou cu acel ID.
- **Listă** cu toate echipamentele + **căutare** după nume, model, IP, rack sau serial.
- **Fișă detaliată** cu toate specificațiile echipamentului.
- **Jurnal de evenimente** per echipament: notițe, mentenanțe, incidente, cu dată și oră.
- **Generare etichetă QR** pentru orice echipament — o poți face captură de ecran sau tipări și lipi pe device.
- **Adăugare / editare / ștergere** echipamente.
- Date demo pre-încărcate la prima rulare (un switch Cisco, un FortiGate, un server Dell).

## Cum funcționează codul QR

Conținutul codului QR este un **UUID** generat automat la crearea echipamentului (ex: `3f29a1c4-...`). La scanare, aplicația caută acel UUID în baza de date locală:
- dacă există → deschide fișa;
- dacă nu există → oferă crearea unui echipament nou cu acel ID.

Astfel, eticheta nu expune date sensibile (doar un identificator opac), iar toate detaliile rămân în baza de date de pe telefon.

## Stack tehnic

| Componentă | Tehnologie |
|---|---|
| Limbaj | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Arhitectură | MVVM (ViewModel + Repository + Room) |
| Bază de date locală | Room (SQLite), offline |
| Scanare QR | ZXing (`zxing-android-embedded`) |
| Generare QR | ZXing core |
| Navigare | Navigation Compose |
| Build | Gradle (Kotlin DSL), AGP 8.7.3, Gradle 8.11.1 |
| minSdk / targetSdk | 24 / 35 |

## Cum obții APK-ul

Ai trei variante. Cea mai simplă este prima — nu trebuie să instalezi nimic local.

### Varianta 1 — Build automat pe GitHub (recomandat, fără Android Studio)

Proiectul include un workflow GitHub Actions (`.github/workflows/build.yml`) care compilează automat un APK la fiecare push.

1. Creează un repository nou pe GitHub.
2. Urcă proiectul (vezi secțiunea „Urcare pe GitHub" mai jos).
3. După push, mergi la tab-ul **Actions** din repository → deschide rularea **Build APK** → la final, descarcă artefactul **NetInventory-debug-apk**. Înăuntru e `app-debug.apk`.
4. Transferă APK-ul pe telefon și instalează-l (trebuie să activezi „Instalare din surse necunoscute").

Opțional: dacă creezi un tag (`git tag v1.0 && git push origin v1.0`), workflow-ul atașează automat APK-ul la un **GitHub Release**.

### Varianta 2 — Android Studio

1. Deschide proiectul în Android Studio (Ladybug sau mai nou).
2. Lasă Gradle să sincronizeze.
3. **Build → Build Bundle(s) / APK(s) → Build APK(s)**, sau rulează direct pe un telefon/emulator cu butonul ▶.
4. APK-ul apare în `app/build/outputs/apk/debug/app-debug.apk`.

### Varianta 3 — Linie de comandă

Ai nevoie de JDK 17 și Android SDK instalat (variabila `ANDROID_HOME` setată).

```bash
./gradlew assembleDebug
# rezultat: app/build/outputs/apk/debug/app-debug.apk
```

## Urcare pe GitHub

Din folderul proiectului:

```bash
git init
git add .
git commit -m "NetInventory: aplicație de inventar echipamente rețea cu QR"
git branch -M main
git remote add origin https://github.com/UTILIZATORUL_TAU/NetInventory.git
git push -u origin main
```

Înlocuiește `UTILIZATORUL_TAU` cu numele tău de utilizator GitHub. După push, workflow-ul de build pornește automat.

## Structura proiectului

```
NetInventory/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/netinventory/app/
│       │   ├── MainActivity.kt          # Activity + navigare (NavHost)
│       │   ├── NetInventoryApp.kt        # Application (creează DB + repository)
│       │   ├── data/                     # Stratul de date (Room)
│       │   │   ├── Equipment.kt          # Entitate echipament
│       │   │   ├── LogEntry.kt           # Entitate intrare jurnal
│       │   │   ├── EquipmentWithLogs.kt  # Relație 1-N
│       │   │   ├── EquipmentDao.kt       # Query-uri
│       │   │   ├── AppDatabase.kt        # Baza SQLite + date demo
│       │   │   └── EquipmentRepository.kt
│       │   ├── ui/                        # Ecrane Compose
│       │   │   ├── EquipmentListScreen.kt
│       │   │   ├── EquipmentDetailScreen.kt
│       │   │   ├── AddEditEquipmentScreen.kt
│       │   │   ├── QrLabelScreen.kt
│       │   │   ├── CommonComponents.kt
│       │   │   └── theme/
│       │   ├── viewmodel/EquipmentViewModel.kt
│       │   └── util/QrUtils.kt           # Generare QR + formatare date
│       └── res/                          # Resurse, iconițe
├── .github/workflows/build.yml           # CI: build automat APK
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/ (wrapper)
```

## Idei de extindere (pentru lucrare / proiect)

- Export / import inventar în CSV sau JSON (tot local).
- Integrare cu un sistem de monitorizare (ping live, status SNMP) pentru a arăta starea device-ului scanat.
- Generare PDF cu coli de etichete QR pentru tipărire în serie.
- Filtrare după tip / rack și statistici pe inventar.

## Licență

MIT — vezi [LICENSE](LICENSE).
