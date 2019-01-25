CREATE TABLE post(
  id serial not null primary key,
  parent int,
  title varchar(200) not null,
  body text not null,
  author int not null,
  slug varchar(100) not null,
  created_on timestamptz not null default now(),
  updated_on timestamptz not null default now()
);