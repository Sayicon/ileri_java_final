# Risk Listesi & Mitigations

| # | Risk | Olasılık | Etki | Mitigation |
|---|---|---|---|---|
| 1 | Docker Compose Windows'ta yavaş/kararsız | Orta | Yüksek (Faz 8) | WSL2 backend kullan, image boyutlarını küçük tut, Faz 8'i erken dene |
| 2 | JavaFX TestFX headless test framework Windows'ta kırılgan | Orta | Düşük (UI logic ayrılmış) | UI'ı saf logic'ten ayır (model test edilir, render manuel demo) |
| 3 | Android emülatör + Docker aynı anda RAM yetmiyor | Orta | Orta | Android demo'su Docker indirilince yapılır, paralel değil |
| 4 | k6/JMeter raporları görsel hazırlanması zaman alır | Düşük | Düşük | k6 JSON → script ile Markdown tablo, gereksiz dashboard yok |
| 5 | Saf JDBC ile boilerplate fazla → tempo düşer | Yüksek | Orta | `BaseJdbcRepository` template + Lombok + rowMapper helper'lar; ilk repo (Faz 2) iyi yazılırsa diğerleri kopyala-uyarla |
| 6 | Cross-service test'leri flaky olabilir | Orta | Orta | Testcontainers ile izole DB/Redis; service-to-service mock (MockWebServer) |
| 7 | TDD disiplinini kırma (uygulamayı önce yazma) | Yüksek | **Yüksek (10 puan kaybı)** | Her fazda **commit timestamp** ile kanıt; A commit'i yapmadan B'ye geçme |
| 8 | İkili çalışmada scope yine de büyük — 4 servis + 2 client + dockerize | Orta | Yüksek | Paralelleştirilebilir fazlar (5+6, 7+8) — biri UI yazarken diğeri infra. Faz 0 → 2 erken bitmesi gerekli |
| 9 | Plagiarism (notu sıfırlar) | Düşük | **Kritik (NOT-1)** | Hiçbir AGENTS.md/kod parçasını başka ekiplerle paylaşma; GitHub private; Claude/AI ile üretilen kodlar gözden geçirilip anlaşılmadan commit'lenmez |
| 10 | Commit dağılımı dengesiz (NOT-2) | Orta | **Kritik** | Her gün en az 1 commit/kişi; faz sahibi commit eder ama review'da diğeri eklediği değişiklikleri kendi commit'iyle koyar. Haftada bir `git shortlog -sn` kontrol |
| 11 | Sunumda kendi yazmadığı modül sorulup cevap verilemiyor → ekibe 0 (NOT-5 ruh) | Orta | Yüksek | Her faz sonunda 30 dk pair-walkthrough: faz sahibi diğerine kodu anlatır. Her ikisi de Mermaid mimari diyagramını ezbere bilmeli |
