create table if not exists users (
  username varchar(256),
  password varchar(256),
  enabled boolean
);

create table if not exists user_roles (
  username varchar(256),
  role_name varchar(256)
);