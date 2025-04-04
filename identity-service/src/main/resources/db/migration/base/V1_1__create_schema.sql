create table users
(
    user_id bigserial primary key,
    username varchar(255) not null unique,
    email    varchar(255) not null unique,
    tag      varchar(255) not null unique,
    password varchar(255) not null,
    firstname varchar(255) not null,
    lastname varchar(255) not null,
    middlename varchar(255),
    avatar_id varchar(255)
);

create table roles
(
    role_id serial primary key,
    name varchar(50) not null
);

create table users_roles
(
    user_id bigint not null references users,
    role_id integer not null references roles,
    primary key (user_id, role_id)
);