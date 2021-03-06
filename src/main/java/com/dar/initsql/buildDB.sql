-- Dans ce fichier, nous créons toutes les bases de données. Nous n'en avons besoin que d'une fois (normalement !)
-- Postgres data types : https://www.postgresql.org/docs/9.2/static/datatype.html
CREATE TABLE Users
(
  id_user serial not null, -- serial : autoincrementing four-byte integer
  login VARCHAR(100) unique,
  password CHAR(64), --SHA-256 encryption, see : http://stackoverflow.com/questions/247304/what-data-type-to-use-for-hashed-password-field-and-what-length
  salt CHAR(32),
  firstname VARCHAR(100),
  lastname VARCHAR(100),
  birthday DATE,
  country VARCHAR(100),
  email VARCHAR(255) unique,
  picture bytea, -- see : https://www.postgresql.org/docs/7.3/static/jdbc-binary-data.html
  description text,
  PRIMARY KEY(id_user)
);

CREATE TABLE Events
(
  id_event VARCHAR(64) not null unique,
  name TEXT,
  url VARCHAR(512),
  location TEXT,
  eventbegin DATE,
  eventend DATE,
  picture bytea,
  description text,
  PRIMARY KEY(id_event)
);

CREATE TABLE Trips
(
  id_trip serial not null,
  name VARCHAR(100),
  picture bytea,
  description text,
  begins date,
  ends date,
  PRIMARY KEY (id_trip)
);

CREATE TABLE Categories
(
  id_category serial not null,
  name VARCHAR(100),
  picture bytea,
  description text,
  PRIMARY KEY (id_category)
);

CREATE TABLE Votes
(
  id_user integer, -- integer : signed four-byte integer
  id_event VARCHAR(64),
  id_trip integer,
  is_like boolean,
  FOREIGN KEY (id_user) REFERENCES Users(id_user) ON DELETE CASCADE,
  FOREIGN KEY (id_event) REFERENCES Events(id_event) ON DELETE CASCADE,
  FOREIGN KEY (id_trip) REFERENCES Trips(id_trip) ON DELETE CASCADE
);

CREATE TABLE Messages
(
  id_msg integer,
  id_user integer,
  id_trip integer,
  posted DATE,
  description text,
  FOREIGN KEY (id_user) REFERENCES Users(id_user) ON DELETE CASCADE,
  FOREIGN KEY (id_trip) REFERENCES Trips(id_trip) ON DELETE CASCADE
);

CREATE TABLE Involded
(
  id_user integer,
  id_trip integer,
  is_admin boolean,
  FOREIGN KEY (id_user) REFERENCES Users(id_user) ON DELETE CASCADE,
  FOREIGN KEY (id_trip) REFERENCES Trips(id_trip) ON DELETE CASCADE
);

CREATE TABLE Tagged
(
  id_event VARCHAR(64),
  id_category integer,
  FOREIGN KEY (id_event) REFERENCES Events(id_event) ON DELETE CASCADE,
  FOREIGN KEY (id_category) REFERENCES Categories(id_category) ON DELETE CASCADE
);

CREATE TABLE Friendships
(
  id_user1 integer,
  id_user2 integer,
  is_pending_request boolean,
  FOREIGN KEY (id_user1) REFERENCES Users(id_user) ON DELETE CASCADE,
  FOREIGN KEY (id_user2) REFERENCES Users(id_user) ON DELETE CASCADE
);