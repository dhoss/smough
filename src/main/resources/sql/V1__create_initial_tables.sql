CREATE TABLE post(
  id serial not null primary key,
  parent int,
  title varchar(200) not null,
  body text not null, -- TODO: add tsearch indexes and stuff for this
  author int not null,
  slug varchar(100) not null, -- TODO: add a unique constraint here
  -- TODO: add indexes for these
  created_on timestamptz not null default now(),
  updated_on timestamptz not null default now()
);