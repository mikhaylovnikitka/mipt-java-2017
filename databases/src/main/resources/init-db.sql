-- Очистка БД
DROP TABLE IF EXISTS Books;
DROP TABLE IF EXISTS Authors;
DROP TABLE IF EXISTS Books_Authors;

CREATE TABLE Books (
  id INTEGER PRIMARY KEY AUTOINCREMENT ,
  ISBN DECIMAL(13, 0) NOT NULL ,
  title VARCHAR(100) NOT NULL,
  cover VARCHAR(400)
);

CREATE TABLE Authors (
  id INTEGER PRIMARY KEY AUTOINCREMENT ,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE Books_Authors(
  id INTEGER PRIMARY KEY AUTOINCREMENT ,
  books_id INTEGER NOT NULL ,
  authors_id INTEGER NOT NULL ,
  num INTEGER NOT NULL
);

INSERT INTO Books (title, ISBN)
VALUES ('NeverWoWoWwhere',
        9782846262347);

INSERT INTO Books (title, cover, ISBN)
VALUES ('SandWoman: Dream Hunters HC',
        'Its smth about physics',
        9781401224240);

INSERT INTO Books (title, ISBN)
VALUES ('Coraline',
        9788897141037);

INSERT INTO Books (title, cover, ISBN)
VALUES ('Stories', 'I dont love stories',
        9780755336609);

INSERT INTO Books (title, ISBN, cover)
VALUES ('Exile (Five Worlds)',
        9780451455215, 'Very Interesting Book');

INSERT INTO Books (title, ISBN)
VALUES ('999. Festmahl des Grauens',
        9783453177536);

INSERT INTO Books (title, ISBN, cover)
VALUES ('Halloween',
        9781607012832, 'Aka kak podnyat babla');

INSERT INTO Books (title, ISBN, cover)
VALUES ('Looking Glass (Cemetery Dance Signature Series #1) [Signed and limited]',
        9781587671258, 'Very Rare book!');

INSERT INTO Books (title, ISBN, cover)
VALUES ('Stories all new tales',
        9780061230929, 'impressive dream');

INSERT INTO Books (title, ISBN, cover)
VALUES ('Trick or Treat: A Collection of Halloween Novellas',
        9781587670480, 'I dont know why');

INSERT INTO Books (title, ISBN, cover)
VALUES ('100 Hair-Raising Little Horror Stories',
        9781566190565, 'demons');

INSERT INTO Books (title, ISBN, cover)
VALUES ('Ghastly beyond belief',
        9780099368304, 'A good book');

INSERT INTO Books (title, ISBN, cover)
VALUES ('As the Sun Goes Down',
        9781892389084, 'face what ty flexish');

INSERT INTO Books (title, ISBN, cover)
VALUES ('Dueling Minds',
        9781587672279, 'dimooon');

INSERT INTO Books (title, ISBN)
VALUES ('Night Visions 11 (Night Visions)',
        9781931081948);
