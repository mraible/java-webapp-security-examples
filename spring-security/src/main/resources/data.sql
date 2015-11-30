-- user/password
insert into users (username, password, enabled) values ('user', '$2a$10$OAhL3e1unzuYdSHfRTtwxu9ofhJxa8JQ01XzZhZ3zfXWygvDF6lOS', true);

insert into authorities (username, authority) values ('user', 'ROLE_ADMIN');
