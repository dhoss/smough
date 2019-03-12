CREATE TABLE category(
  id serial not null primary key,
  name varchar(200) not null
);

CREATE INDEX category_name ON category(name);

CREATE TABLE post(
  id serial not null primary key,
  parent int,
  title varchar(200) not null,
  body text not null, -- TODO: add tsearch indexes and stuff for this
  author int not null,
  slug varchar(100) not null,
  published_on timestamp not null default now(), -- default to publishing immediately
  category int REFERENCES category(id),
  created_on timestamp not null default now(),
  updated_on timestamp not null default now()
);

CREATE UNIQUE INDEX post_slug ON post(slug);

CREATE INDEX post_published_on_day ON post(date(published_on));

CREATE INDEX post_created_on ON post(created_on DESC);
CREATE INDEX post_updated_on ON post(updated_on DESC);