# Değişiklikler.md

Bu dosya, Fundickonot JavaFX uygulaması üzerinde yapılan güvenlik ve yapı iyileştirme düzenlemelerini özetlemektedir. Amaç, Supabase entegrasyonunun daha güvenli, dinamik ve kullanıcıya özel çalışmasını sağlamaktır.

---

## ✅ Genel Amaç

- Supabase ile **her kullanıcının yalnızca kendi verisine erişmesini** sağlamak
- `auth.uid()` kullanarak `user_id` kolonunu otomatik atamak
- Tüm isteklerde **JWT access token** kullanmak
- Kodları daha modüler, okunabilir ve güvenli hale getirmek

---

## ✉ Yapılan Temel Değişiklikler

### 1. `NoteService.java`
- Tüm HTTP isteklerine `accessToken` parametresi eklendi
- `Authorization: Bearer <token>` headerı düzenlendi
- `user_id` artık client tarafından gönderilmiyor, Supabase `auth.uid()` kullanıyor

### 2. `AppController.java`
- `accessToken` alanı eklendi
- `setUserContext(String userId, String accessToken)` metodu tanımlandı
- Not yükleme, silme, arama gibi tüm servis çağrıları güvenli token ile yapıldı
- `CreateController` ve `EditController`'lara token aktarıldı

### 3. `CreateController.java`
- `accessToken` alanı ve `setAccessToken(...)` metodu eklendi
- `NoteService.createNote(note)` çağrısı token ile güvenli hale getirildi

### 4. `EditController.java`
- `accessToken` alanı ve `setAccessToken(...)` metodu eklendi
- `NoteService.updateNote(note)` çağrısı token ile yapıldı

### 5. `LoginController.java`
- Supabase token endpoint'ten `access_token` ve `user_id` alındı
- `AppController`'a bu bilgiler `setUserContext(...)` ile gönderildi

### 6. Supabase Veritabanı
- `user_id` kolonu için `DEFAULT auth.uid()` atandı:
```sql
alter table notes
alter column user_id set default auth.uid();
```
- RLS kuralları zaten açıktı ve `user_id = auth.uid()` şeklinde düzenlenmişti

---

## ⚡ Ekstra
- `image_url` için base64 veya Supabase Storage entegrasyonu sonraki adım olabilir
- API KEY kullanımı minimumda tutuldu, sadece `apikey` header'ında
- Otomatik login/refresh için `refresh_token` ileride entegre edilebilir

