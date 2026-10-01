DROP TABLE IF EXISTS registrations CASCADE;
DROP TABLE IF EXISTS conferences CASCADE;
DROP TABLE IF EXISTS participants CASCADE;

CREATE TABLE participants (
    id           BIGSERIAL PRIMARY KEY,
    full_name    VARCHAR(150) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    organization VARCHAR(150),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE conferences (
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    track      VARCHAR(30)  NOT NULL
        CHECK (track IN ('IT','SCIENCE','BUSINESS','MEDICINE','EDUCATION')),
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    location   VARCHAR(200),
    CHECK (end_date >= start_date)
);

CREATE TABLE registrations (
    id             BIGSERIAL PRIMARY KEY,
    participant_id BIGINT NOT NULL REFERENCES participants(id) ON DELETE CASCADE,
    conference_id  BIGINT NOT NULL REFERENCES conferences(id)  ON DELETE CASCADE,
    status         VARCHAR(20) NOT NULL
        CHECK (status IN ('CREATED','CONFIRMED','PAID','ATTENDED','CANCELLED')),
    registered_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (participant_id, conference_id)
);

INSERT INTO participants (full_name, email, organization) VALUES
('Иванов Иван Иванович',      'ivanov@mail.ru',   'МГУ'),
('Петрова Анна Сергеевна',    'petrova@mail.ru',  'МФТИ'),
('Сидоров Пётр Алексеевич',   'sidorov@mail.ru',  'МИРЭА'),
('Кузнецова Ольга Ивановна',  'kuznetsova@mail.ru','СПбГУ'),
('Смирнов Дмитрий Олегович',  'smirnov@mail.ru',  'ВШЭ');

INSERT INTO conferences (title, track, start_date, end_date, location) VALUES
('AI Conference 2025',       'IT',       '2025-06-01', '2025-06-03', 'Москва'),
('BioTech Summit',           'SCIENCE',  '2025-07-10', '2025-07-12', 'Санкт-Петербург'),
('Business Analytics Forum', 'BUSINESS', '2025-08-15', '2025-08-17', 'Казань'),
('MedInnovations 2025',      'MEDICINE', '2025-09-05', '2025-09-07', 'Новосибирск');

INSERT INTO registrations (participant_id, conference_id, status) VALUES
(1, 1, 'CONFIRMED'),
(2, 1, 'PAID'),
(3, 1, 'CREATED'),
(4, 2, 'CONFIRMED'),
(5, 2, 'ATTENDED'),
(1, 3, 'CREATED'),
(2, 3, 'CANCELLED'),
(3, 4, 'CONFIRMED'),
(4, 4, 'PAID'),
(5, 4, 'CREATED');