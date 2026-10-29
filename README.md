# Контрольная работа №2 — Conference Registration JavaFX

Готовая КР №2 является продолжением КР №1 `conference-registration`. Предметная область и схема БД сохранены без изменения: **участники → регистрации → конференции**.

## Что реализовано

- Java 21 + JavaFX 21 + Maven.
- Отдельное JDBC-подключение JavaFX к PostgreSQL КР №1.
- Слои `View/FXML → Controller → Service → Repository/DAO → Database`.
- `TableView` для участников, конференций и регистраций.
- Полный CRUD для всех трёх сущностей.
- Подтверждение удаления.
- Работа со связанными сущностями через `ComboBox`, без ввода FK вручную.
- Поиск по нескольким смысловым полям.
- Фильтрация: организации, треки, статусы, конференции.
- Сортировка минимум по двум параметрам для каждого списка.
- Валидация обязательных полей, длины строк, email, дат, связей и бизнес-переходов статусов.
- `ObservableList` для отображения данных.
- Отдельный блок статистики: участники, конференции, регистрации, активные, посетившие, отменённые, распределение по трекам.
- Экспорт текущего набора регистраций в `.xlsx` через `FileChooser`.
- FXML для главного окна и форм.
- CSS для оформления.
- Понятные JavaFX-диалоги вместо консоли.
- JUnit 5/AssertJ smoke-тесты моделей.

## Запуск PostgreSQL

Из корня проекта:

```bash
docker compose up -d
docker compose ps
```

База публикуется на `localhost:5433`, параметры находятся в `src/main/resources/db.properties`.

Важно: `schema.sql` монтируется как init-скрипт PostgreSQL. Если Docker volume уже был создан ранее, повторный запуск не пересоздаёт БД. Для чистого развёртывания КР №1:

```bash
docker compose down -v
docker compose up -d
```

`down -v` удаляет данные текущего Docker volume, поэтому использовать эту команду только если нужно начать с чистой БД.

## Запуск JavaFX

Требуется Java 21 и Maven 3.9+.

```bash
mvn clean compile
mvn test
mvn javafx:run
```

Windows также:

```powershell
.
un-javafx.ps1
```

JavaFX **не запускается в Docker**. Docker нужен для PostgreSQL; JavaFX запускается как обычное настольное приложение.

## Подключение

`src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5433/conference_db
db.user=postgres
db.password=postgres
```

Пароль не находится в Java-классах — настройки вынесены в ресурс.

## Соответствие требованиям КР №2

| Требование | Реализация |
|---|---|
| JavaFX | `ConferenceFxApplication` |
| Maven | `pom.xml` |
| PostgreSQL JDBC | dependency `org.postgresql:postgresql` |
| TableView | 3 таблицы |
| CRUD | Participants / Conferences / Registrations |
| Связанные сущности | RegistrationForm + ComboBox |
| Поиск | по нескольким полям |
| Фильтрация ≥2 | несколько фильтров в каждом разделе |
| Сортировка ≥2 | отдельные ComboBox сортировки |
| Валидация | Form Controllers + Services |
| FXML | `src/main/resources/fxml` |
| CSS | `src/main/resources/css/style.css` |
| ObservableList | списки JavaFX |
| Service | `ConferenceService`, `ParticipantService`, `RegistrationService` |
| DAO/Repository | существующие Repository из КР №1 |
| Статистика ≥5 | 7 показателей/категорий |
| Экспорт | XLSX через FileChooser |
| Подтверждение удаления | JavaFX Confirmation Alert |
| Обработка ошибок | Alert с понятным сообщением |

## Важное бизнес-правило регистрации

Сохранено правило КР №1: нельзя зарегистрировать участника на уже завершившуюся конференцию. Для статуса регистрации сохранены переходы:

`CREATED → CONFIRMED → PAID → ATTENDED`

Отмена разрешена до посещения. Некорректные переходы блокируются сервисным слоем.

## Структура

```text
src/main/java/ru/mirea/conference/
├── model/             # сущности КР №1
├── repository/        # JDBC/DAO
├── service/           # бизнес-логика
├── exception/         # исключения
├── util/              # DatabaseManager, ExcelExporter
└── fx/
    ├── ConferenceFxApplication.java
    ├── controller/    # JavaFX controllers
    └── util/          # UI validation

src/main/resources/
├── fxml/
│   ├── main.fxml
│   ├── participant-form.fxml
│   ├── conference-form.fxml
│   └── registration-form.fxml
├── css/style.css
└── db.properties

db/schema.sql
docker-compose.yml
pom.xml
```

## Демонстрация на защите

1. `docker compose up -d`.
2. Показать `docker compose ps` и PostgreSQL.
3. Запустить `mvn javafx:run`.
4. Показать таблицы и связь регистраций с участниками/конференциями.
5. Добавить, изменить и удалить запись с подтверждением.
6. Показать ошибку валидации.
7. Показать поиск, два и более фильтра и сортировку.
8. Показать статистику.
9. Выполнить экспорт XLSX и открыть полученный файл.





# 1. Перейти в корень проекта
cd "/d/Файлы/МИРЭА/Семестр 5/Программирование корпоративных систем/PKS-conference-registration-pr2"

# 2. Переключить Java на 21 в текущей сессии bash
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.12.101-hotspot"
export PATH="$JAVA_HOME/bin:$PATH"

# 3. Проверить, что Maven видит Java 21 (в строке Java version должно быть 21.x)
mvn -version

# 4. Проверить, что PostgreSQL запущен и отвечает
docker exec conference_postgres pg_isready -U postgres -d conference_db

# 5. (Если из шага 4 ответ не "accepting connections") — поднять контейнер
docker start conference_postgres

# 6. Собрать проект (без clean, чтобы не ловить блокировку от NVIDIA Overlay)
mvn compile

# 7. (Опционально) Прогнать тесты
mvn test

# 8. Запустить приложение (терминал "зависнет", пока окно открыто — это нормально)
mvn javafx:run

# 9. (После закрытия окна) Остановить БД, если больше не нужна
docker stop conference_postgres