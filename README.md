# Conference Registration System

Консольная информационная система **«Организация конференций — регистрация участника на конференцию»**.

---

## 👥 Команда (2 человека)

| Участник | Зона ответственности |
|----------|----------------------|
| **Участник 1** | Модель, БД, JDBC-репозитории, сервисы, бизнес-правила, Docker |
| **Участник 2** | Console UI, статистика, экспорт Excel, документация |

---

## 🛠️ Стек технологий

- **Java 17**
- **PostgreSQL 16** (запускается через Docker Compose)
- **JDBC** + `PreparedStatement` + `try-with-resources`
- **Apache POI 5.2.5** — экспорт в Excel
- **Docker Compose** — БД одной командой
- **Maven** — сборка

---

## 🏛️ Архитектура

```
Console UI  →  Service  →  Repository  →  JDBC  →  PostgreSQL
 (ui)          (service)   (repository)            (Docker)
```

- **model** — доменные сущности и enum'ы
- **repository** — доступ к БД (интерфейс `Repository<T, ID>` + 3 реализации)
- **service** — бизнес-логика, поиск, фильтрация, сортировка, статистика
- **ui** — консольные меню
- **util** — подключение к БД, ввод-вывод, экспорт в Excel
- **exception** — собственные исключения

---

## 🐳 Быстрый старт через Docker (рекомендуется)

### 1. Поднять PostgreSQL

```bash
# Создать локальный .env (можно скопировать из примера)
cp .env.example .env

# Запустить контейнер с БД
docker compose up -d
```

При **первом** запуске автоматически выполнится `db/schema.sql` — создадутся БД, таблицы и тестовые данные.

Проверить статус:

```bash
docker compose ps
docker compose logs -f postgres
```

Подключиться к БД:

```bash
docker exec -it conference_postgres psql -U postgres -d conference_db
```

### 2. Запустить приложение

```bash
mvn clean compile exec:java
```

### 3. Остановить

```bash
# Остановить, данные сохранятся в volume
docker compose down

# Полностью удалить (включая данные) — нужно после изменения schema.sql
docker compose down -v
```

---

## 🛠️ Ручной запуск без Docker

1. Установить PostgreSQL 15+ локально.
2. Создать БД `conference_db`.
3. Выполнить `db/schema.sql`:
   ```bash
   psql -U postgres -d conference_db -f db/schema.sql
   ```
4. Проверить параметры в `src/main/resources/db.properties`.
5. Запустить:
   ```bash
   mvn clean compile exec:java
   ```

---

## ⚙️ Параметры подключения

| Параметр | Значение по умолчанию |
|----------|----------------------|
| Host     | `localhost`          |
| Port     | `5432`               |
| Database | `conference_db`      |
| User     | `postgres`           |
| Password | `postgres`           |

Все параметры хранятся в `src/main/resources/db.properties` и в `.env`.

---

## 📋 Функциональность

### Участники
- Создание, чтение, изменение, удаление
- Поиск по ФИО и email
- Сортировка по ФИО и дате создания

### Конференции
- Создание, чтение, изменение, удаление
- Поиск по названию
- Фильтр по треку и диапазону дат
- Сортировка по дате начала и названию

### Регистрации
- Создание, чтение, изменение статуса, удаление
- Поиск по участнику и конференции
- Фильтр по статусу и конференции
- Сортировка по дате и статусу
- **Статистика** (7 показателей)
- **Экспорт в Excel** (`.xlsx`)

---

## ✅ Бизнес-правила (6 правил)

1. Email участника уникален.
2. Нельзя зарегистрировать одного участника на одну конференцию дважды.
3. Нельзя создать регистрацию на несуществующего участника или конференцию.
4. Нельзя регистрироваться на уже завершённую конференцию.
5. Запрещены недопустимые переходы статусов регистрации:
   ```
   CREATED   → CONFIRMED, CANCELLED
   CONFIRMED → PAID,      CANCELLED
   PAID      → ATTENDED,  CANCELLED
   ATTENDED  → (финальный)
   CANCELLED → (финальный)
   ```
6. Дата окончания конференции не может быть раньше даты начала.

---

## 📊 Статистика (7 показателей)

- Всего участников
- Всего конференций
- Всего регистраций
- Активных (`CONFIRMED` + `PAID`)
- Посетивших (`ATTENDED`)
- Отменённых (`CANCELLED`)
- Разбивка регистраций по трекам конференций


---

## 📦 Сборка в JAR

```bash
mvn clean package
java -jar target/conference-registration-1.0-SNAPSHOT.jar
```

---

## 📁 Структура репозитория

```
conference-registration/
├── .env.example
├── .gitignore
├── docker-compose.yml
├── pom.xml
├── README.md
├── db/
│   └── schema.sql
└── src/main/
    ├── java/ru/mirea/conference/
    │   ├── Main.java
    │   ├── exception/  (3 файла)
    │   ├── model/      (5 файлов)
    │   ├── repository/ (4 файла)
    │   ├── service/    (3 файла)
    │   ├── ui/         (3 файла)
    │   └── util/       (3 файла)
    └── resources/
        └── db.properties
```