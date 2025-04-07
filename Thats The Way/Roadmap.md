# roadmap.md

Bu belge, Fundickonot uygulamasının geliştirme süreçlerinde bir yol haritası sunar. Projenin mevcut durumu, yapılması planlanan iyileştirmeler ve potansiyel güncellemeler aşağıda listelenmiştir.

---

## 🚀 Mevcut Durum
- JavaFX ile gelişen masaüstü uygulama
- Supabase kullanılarak:
    - Kullanıcı kayıt/giriş
    - Not ekleme/güncelleme/silme
    - `auth.uid()` ile kişiye özel veri
    - JSON-REST ile veri akışı

---

## ✅ Kısa Vadeli Planlar (1-2 Hafta)
- [ ] Notları Supabase Storage'a dosya olarak yükleyebilme (resim URL yerine dosya kaydı)
- [ ] Kullanıcı oturum bilgilerinin yerel dosyada saklanması (auto login)
- [ ] `refresh_token` desteği ekleme
- [ ] "favori notlar" için etiketleme veya bayrak sistemi

---

## ⚖️ Orta Vadeli Planlar (2-4 Hafta)
- [ ] Tema desteği: karanlık/açık modlar (görsel ayarlar)
- [ ] Filtreleme sistemi: kategoriye, tarihe veya başlığa göre
- [ ] Kullanıcı profili ekranı (e-posta, oturum bilgisi görüntüleme)
- [ ] JSON dosyası olarak notları dışa aktar / içe al

---

## 🚧 Uzun Vadeli Planlar
- [ ] Web versiyonu (Java Spring Boot + React/Tailwind)
- [ ] Mobil versiyon (Flutter veya Kotlin Multiplatform)
- [ ] Supabase üzerinde bildirim desteği (Realtime DB ya da Trigger tabanlı)
- [ ] Notların ortak kullanımı (başka kullanıcılarla paylaşma)
- [ ] AI destekli not önerileri veya otomatik kategori belirleme

---

## ✉ Ekstra Fikirler
- Supabase Edge Functions kullanarak sunucu taraflı özel mantıklar
- Dil desteği: TR/EN çok dillilik
- PDF olarak not dışa aktar

---

> Not: Roadmap esnektir. Yeni ihtiyaçlar doğrultusunda öncelikler güncellenebilir.

