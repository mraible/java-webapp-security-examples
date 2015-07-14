create table users (
  user_name         varchar(30) not null primary key,
  user_pass         varchar(100) not null
);

create table user_roles (
  user_name         varchar(30) not null,
  role_name         varchar(30) not null,
  primary key (user_name, role_name)
);