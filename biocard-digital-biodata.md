# Portfolio Project Data — BioCard - Digital Biodata

- **Category:** `desktop-mobile` (`Mobile App` / `Desktop App` / `Cross-Platform App`)
- **Category Label:** `Mobile App`
- **Accent Color:** `amber`
- **Target Assets Folder:** `public/images/projects/biocard_digital_biodata/`
- **Required Local Images:** `banner.webp`, `image01.webp` .. `image03.webp`

## TypeScript Data (`ProjectCaseStudy`)

```typescript
{
  "id": "biocard-digital-biodata",
  "code": "PRO-06 // APP-02",
  "title": "BioCard - Digital Biodata",
  "category": "desktop-mobile",
  "categoryLabel": "Mobile App",
  "year": "2022",
  "status": "Completed",
  "accentColor": "amber",
  "repoUrl": "https://github.com/weztcy/BioCard-Digital-Biodata",
  "license": "MIT License",
  "banner": "/images/projects/biocard_digital_biodata/banner.webp",
  "summary": "Offline-first Android biodata app built with Java, MVVM, and Room SQLite to store, manage, and share structured personal profile cards.",
  "projectDescription": "BioCard is a native Java Android application for creating, storing, and sharing digital biodata cards offline. Built with MVVM, LiveData, Room SQLite, and Material 3, it manages personal, contact, medical, education, career, and portfolio data locally without cloud dependencies.",
  "architectureSummary": "Single-module Android MVVM architecture with ViewBinding activities observing LiveData from BiodataViewModel backed by BiodataRepository, single-threaded IO_EXECUTOR, Room SQLite, and private internal photo storage.",
  "techStack": [
    "Java",
    "Android SDK",
    "Room",
    "SQLite",
    "AndroidX ViewModel",
    "LiveData",
    "Material Components",
    "ViewBinding",
    "ConstraintLayout",
    "Gradle",
    "JUnit"
  ],
  "telemetryMatrix": {
    "projectType": "Native Android Mobile App",
    "targetPlatform": "Android 8.0+ (API 26 to 36)",
    "primaryDomain": "Personal Identity & Offline Biodata",
    "architecturePipeline": "MVVM -> Repository -> Room SQLite",
    "testingAndAudit": "Verified",
    "projectStatus": "Completed",
    "projectYear": "2022"
  },
  "desktopMobileProjectInfo": {
    "applicationType": "Native Android Mobile Application",
    "targetPlatform": "Android",
    "developmentStatus": "Completed",
    "developmentYear": "2022",
    "applicationCategory": "Personal Productivity & Digital Identity"
  },
  "desktopMobileBackground": "Paper biodata forms and static PDF resumes are tedious to update, hard to carry on mobile devices, and cumbersome to share during job applications or emergencies. BioCard addresses this by providing a private, offline-first Android card storing structured personal, career, and medical details.",
  "desktopMobileProblemStatements": [
    {
      "title": "Fragmented Personal & Career Records",
      "description": "Users often scatter contact info, academic history, job credentials, and emergency medical details across notes apps, paper forms, and static documents that quickly become outdated.",
      "issueCode": "ISSUE_01"
    },
    {
      "title": "Privacy Exposure on Cloud Profile Tools",
      "description": "Many digital business card platforms require user registration and remote server storage for sensitive identity, address, and In-Case-of-Emergency (ICE) medical information.",
      "issueCode": "ISSUE_02"
    },
    {
      "title": "Inconsistent Plain-Text Sharing",
      "description": "Manually copying and formatting biodata fields into messaging or email apps introduces typos, leaves blank placeholders, and slows down routine applications or networking.",
      "issueCode": "ISSUE_03"
    }
  ],
  "desktopMobileObjectives": [
    {
      "title": "Zero-Server Offline Persistence",
      "description": "Store structured biodata and profile photos strictly in on-device Room SQLite and private internal storage with cloud backup and device transfer disabled.",
      "targetTag": "TARGET_01"
    },
    {
      "title": "Permissionless Media Handling",
      "description": "Adopt the Android system Photo Picker with automatic EXIF orientation correction, 1024 px downscaling, and unused file cleanup without requesting storage permissions.",
      "targetTag": "TARGET_02"
    },
    {
      "title": "Reactive Card Rendering & Clean Export",
      "description": "Observe LiveData changes to update the Digital BioCard dynamically, omit empty optional sections, and generate clean plain-text shares via Android Intent.",
      "targetTag": "TARGET_03"
    }
  ],
  "desktopMobileFeatures": [
    {
      "title": "Sectioned Digital BioCard Display",
      "description": "Presents a gradient profile header alongside collapsible sections for Contact, ICE Medical, Personal, Education, Work, Certifications, Projects, Skills, Languages, and Social links.",
      "badge": "UI Card"
    },
    {
      "title": "Validated Biodata Editor & Date Picker",
      "description": "Provides real-time inline TextInputLayout validation for required fields, email, E.164 phone length, graduation year, and URLs plus a backward-constrained MaterialDatePicker.",
      "badge": "Validation"
    },
    {
      "title": "Private Profile Photo Pipeline",
      "description": "Imports images via PickVisualMedia, applies ExifInterface rotation, scales bitmaps to 1024 px, compresses to JPEG in private storage, and removes replaced photos automatically.",
      "badge": "Media"
    },
    {
      "title": "One-Tap System Share Sheet",
      "description": "Compiles all populated fields via ShareUtils while skipping blank optional attributes and dispatches a plain-text ACTION_SEND chooser to any installed app.",
      "badge": "Sharing"
    }
  ],
  "desktopMobileInterfaceShowcase": [
    {
      "imageUrl": "/images/projects/biocard_digital_biodata/image01.webp"
    },
    {
      "imageUrl": "/images/projects/biocard_digital_biodata/image02.webp"
    },
    {
      "imageUrl": "/images/projects/biocard_digital_biodata/image03.webp"
    }
  ],
  "desktopMobileUserFlow": {
    "summary": "From the animated splash screen, users enter the Home dashboard to create or preview their BioCard. The structured editor validates inputs, imports profile photos privately, and persists records to Room SQLite, updating the Digital BioCard view and plain-text share sheet via LiveData.",
    "steps": [
      {
        "step": "01",
        "title": "Splash Launch & Dashboard Check",
        "description": "SplashActivity animates branding for 1200 ms and routes to MainActivity, which observes BiodataViewModel to show either an onboarding empty state or the BioCard summary.",
        "actorOrNode": "User / SplashActivity / MainActivity"
      },
      {
        "step": "02",
        "title": "Biodata Entry & Photo Selection",
        "description": "BiodataActivity captures multi-section inputs, launches PickVisualMedia for private photo import on IO_EXECUTOR, and picks birth dates through MaterialDatePicker.",
        "actorOrNode": "User / BiodataActivity / ImageUtils"
      },
      {
        "step": "03",
        "title": "Client Validation & Room Upsert",
        "description": "ValidationUtils checks required and formatted fields; valid data is normalized and persisted to Room SQLite via BiodataRepository off the main thread.",
        "actorOrNode": "BiodataRepository / IO_EXECUTOR"
      },
      {
        "step": "04",
        "title": "Digital Card View & Plain-Text Share",
        "description": "BioCardActivity renders populated sections with Linkify links and Material chips, while ShareUtils formats non-empty fields into an Android ACTION_SEND share intent.",
        "actorOrNode": "BioCardActivity / ShareUtils"
      }
    ]
  },
  "desktopMobileArchitecture": {
    "summary": "BioCard implements a single-module Android MVVM architecture with ViewBinding activities observing LiveData from BiodataViewModel. BiodataRepository delegates all Room DAO persistence and photo file operations to a single-threaded IO_EXECUTOR, keeping UI rendering responsive and strictly offline.",
    "layers": [
      {
        "name": "Presentation & UI Layer",
        "role": "Renders Material 3 screens, handles edge-to-edge system bar insets, validates form inputs, and observes LiveData state.",
        "tech": "Android SDK / ViewBinding / Material Components",
        "components": [
          "BaseActivity",
          "SplashActivity",
          "MainActivity",
          "BiodataActivity",
          "BioCardActivity",
          "AboutActivity"
        ]
      },
      {
        "name": "ViewModel & State Layer",
        "role": "Survives configuration changes and exposes reactive Biodata LiveData plus save and delete commands.",
        "tech": "AndroidX ViewModel / LiveData",
        "components": [
          "BiodataViewModel"
        ]
      },
      {
        "name": "Repository & Concurrency Layer",
        "role": "Coordinates Room DAO operations and private photo cleanup on a single background executor, returning callbacks on the main looper.",
        "tech": "Java / ExecutorService",
        "components": [
          "BiodataRepository",
          "AppDatabase.IO_EXECUTOR"
        ]
      },
      {
        "name": "Local Persistence & Utility Layer",
        "role": "Stores the single-row biodata record in SQLite, processes EXIF-rotated profile JPEGs, and handles dates, validation, and text sharing.",
        "tech": "Room / SQLite / ExifInterface",
        "components": [
          "AppDatabase",
          "BiodataDao",
          "Biodata",
          "ImageUtils",
          "ValidationUtils",
          "DateUtils",
          "ShareUtils"
        ]
      }
    ]
  },
  "desktopMobileDataManagement": {
    "storageType": "Room SQLite (biocard.db) and app-private internal storage (filesDir/photos)",
    "stateEngine": "AndroidX ViewModel with reactive Room LiveData observation",
    "offlineSyncStrategy": "Strictly offline-first single-device storage with cloud backup and device transfer explicitly excluded via data_extraction_rules.xml",
    "cachingMechanism": "Subsampled BitmapFactory decoding (512 px display target, 1024 px stored JPEG) with automatic deletion of unreferenced photo files"
  },
  "desktopMobilePlatformImplementation": [
    {
      "title": "Edge-to-Edge Window Insets & IME Handling",
      "description": "BaseActivity enables EdgeToEdge and applies ViewCompat window inset padding for systemBars, displayCutout, and IME keyboard bounds across all screens.",
      "badge": "Android UI"
    },
    {
      "title": "Single-Thread Serialized Disk I/O",
      "description": "AppDatabase.IO_EXECUTOR serializes Room upserts, full deletions, and private photo cleanup on one background thread while delivering UI callbacks via the main Handler.",
      "badge": "Concurrency"
    },
    {
      "title": "Zero-Permission Photo Picker & Privacy Rules",
      "description": "Uses AndroidX PickVisualMedia without READ_MEDIA_IMAGES or storage permissions, paired with allowBackup=false and full data_extraction_rules exclusions.",
      "badge": "OS Privacy"
    },
    {
      "title": "R8 Minification & Resource Shrinking",
      "description": "Release Gradle builds enable minifyEnabled and shrinkResources with ProGuard rules to compact the compiled Android APK down to 1.81 MB.",
      "badge": "Build Pipeline"
    }
  ],
  "desktopMobileTechSpecs": {
    "programmingLanguage": "Java",
    "framework": "Android SDK",
    "localStorage": "Room SQLite"
  },
  "desktopMobilePlatformSpecs": {
    "targetPlatform": "Android Smartphones",
    "packageFormat": "Android APK",
    "targetArchitecture": "Universal Android ABI",
    "buildFootprint": "1.81 MB Release APK"
  }
}
```
