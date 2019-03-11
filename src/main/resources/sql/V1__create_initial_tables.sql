CREATE TABLE post(
  id serial not null primary key,
  parent int,
  title varchar(200) not null,
  body text not null, -- TODO: add tsearch indexes and stuff for this
  author int not null,
  slug varchar(100) not null,
  published_on timestamptz not null default now(), -- default to publishing immediately
  -- TODO: add indexes for these
  created_on timestamptz not null default now(),
  updated_on timestamptz not null default now()
);

CREATE UNIQUE INDEX post_slug_idx ON post(slug);

CREATE INDEX posts_on_day ON post(date(published_on));