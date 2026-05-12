# Mimari Diyagramlar

> Faz 10'da Mermaid diyagramları ile doldurulacak.

## Component Diagram (placeholder)

```
[Android App] ──┐
                ├──► [Gateway :8080]
[JavaFX App]  ──┘         │
                    ┌──────┼──────┬──────────┐
                    ▼      ▼      ▼          ▼
                 [Auth] [Event] [Ticket] [Notification]
                  :8081  :8082   :8083      :8084
                    │      │       │
                    └──────┴───────┴──► [PostgreSQL]
                                    └──► [Redis]
```
