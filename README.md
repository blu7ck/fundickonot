# Fundickonot ✍️

Kullanıcıya özel, güvenli ve sade bir not alma uygulaması. JavaFX ve Supabase altyapısıyla geliştirildi.

## 🚀 Özellikler

- ✅ Supabase Auth ile kullanıcı kaydı ve girişi
- ✅ Not oluşturma, düzenleme, silme
- ✅ Kategori / klasör desteği
- ✅ Resim ekleme (Storage entegrasyonu yakında!)
- ✅ Tamamen token doğrulamalı güvenli veri erişimi

## 🧱 Teknolojiler

- Java 17
- JavaFX 23
- Supabase (Auth + REST + Storage)
- Maven

## ⚙️ Yapılandırma

Proje çalışmadan önce, `src/main/resources/` dizinine `config.properties` dosyasını ekleyin:

```properties
supabase.url=https://yourproject.supabase.co
supabase.key=your_anon_key
```

> NOT: `config.properties` dosyasını `.gitignore` içine ekleyerek gizli tutun.

## 🛠️ Kurulum

```bash
git clone https://github.com/kullaniciadi/fundickonot.git
cd fundickonot
mvn clean install
```

## 📦 Kurulum Paketi

Kurulum dosyaları `.exe / .msi` formatında üretilecektir. Bunun için `jpackage` kullanılacaktır. Detaylar `docs/installer.md` dosyasında paylaşılacaktır.

## 📁 Branch Yapısı

- `main` → Kararlı sürüm
- `dev` → Geliştirme ortamı
- `feature/*` → Yeni özellikler için dallar
- `bugfix/*` → Hata düzeltmeleri için

## 👥 Geliştirici

- 👤 blu4ck – Proje yöneticisi, geliştirici

## 📝 Lisans

MIT License

