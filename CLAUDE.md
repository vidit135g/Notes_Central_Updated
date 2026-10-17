# Notes Central — Claude Code continuation

## Objective

Finish the native Android Soma redesign as a faithful implementation of:

`https://claude.ai/artifact/LdUwByhwXHsdxjFzwPxj75`

The design maps a 300px reference phone to a 360dp Android phone (all guide dimensions use a 1.2 scale). Do not introduce Material defaults, unrelated branding, or category-colour full-card fills.

## Current implementation

- Six full themes are defined in `notes/src/main/res/values/colors_soma.xml` and `styles_soma.xml`: Vata dawn, Kapha morning, Pitta midday, Vata afternoon, Kapha evening, Pitta night.
- Global Poppins body and Fraunces heading treatment is applied through `OmniNotesTheme.SomaBase` and screen-specific styles.
- The list has Soma cards, filter chips, streak pill, persistent pinning, non-destructive swipe Pin/Archive actions, reminder pills, and inline checklist completion.
- Detail editor and voice recording are redesigned. The recorder creates a real audio attachment only after Save.
- Theme coverage includes main, drawer, settings, category editor, stats, note info, password, attachment/location/backup dialogs, gallery, sketch, widget configuration, loading, onboarding, and About.

## Important source files

- `notes/src/main/res/values/styles_soma.xml`
- `notes/src/main/res/values/colors_soma.xml`
- `notes/src/main/res/layout/fragment_list.xml`
- `notes/src/main/res/layout/note_layout.xml`
- `notes/src/main/res/layout/fragment_detail.xml`
- `notes/src/main/res/layout/dialog_voice_record.xml`
- `notes/src/main/java/com/absolute/android/notescentral/models/views/NoteSwipeLayout.java`
- `notes/src/main/java/com/absolute/android/notescentral/models/adapters/NoteAdapter.java`

## Build and validation

```sh
JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
  ./gradlew :notes:assemblePlayDebug --offline
```

The built APK is:

`notes/build/outputs/apk/play/debug/notes-play-debug.apk`

Before changing source, preserve the existing working-tree modifications. Do not reset or checkout unrelated files. Validate each of the six theme periods in Android UI once an emulator surface is available.
