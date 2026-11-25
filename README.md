# AI Floating Assistant — proyek Android + cloud build

Proyek ini memiliki antarmuka Android dan floating window yang dapat digeser. Workflow GitHub Actions disertakan untuk membuat APK debug di cloud.

## Batasan penting
- Ini bukan APK siap instal.
- AI belum tersambung sampai `BACKEND_URL` pada `FloatingService.java` diisi dengan backend HTTPS yang benar.
- API key AI harus disimpan di backend, bukan di aplikasi Android.
- Belum ada fitur unggah foto pada versi ini.

## Membuat APK dari HP melalui Chrome
1. Ekstrak ZIP di aplikasi Files/Manajer File HP.
2. Buka GitHub di Chrome dan buat repository baru bernama `AI-Floating-Assistant`.
3. Pada repository, pilih **Add file > Upload files**.
4. Unggah seluruh isi folder proyek (termasuk folder tersembunyi `.github/workflows` jika antarmuka memungkinkan). Pastikan `settings.gradle`, `build.gradle`, `app/`, dan `.github/` berada di root repository, bukan di dalam folder bertingkat.
5. Commit ke branch `main`.
6. Buka tab **Actions**, pilih workflow **Build Android APK**, lalu tekan **Run workflow** jika belum berjalan otomatis.
7. Setelah selesai, buka hasil run dan unduh artifact `AI-Floating-Assistant-debug`. Ekstrak artifact untuk memperoleh `app-debug.apk`.

Jika antarmuka unggah GitHub di HP tidak bisa mengunggah folder atau folder `.github`, gunakan tampilan Desktop site di Chrome atau unggah berkas satu per satu dengan struktur folder yang sama.

## Koneksi AI
Atur `BACKEND_URL` di `app/src/main/java/com/example/aifloatingassistant/FloatingService.java` ke endpoint milikmu. Backend menerima POST JSON `{"message":"..."}` dan mengembalikan `{"answer":"..."}`. Jangan menaruh API key penyedia AI di APK.
