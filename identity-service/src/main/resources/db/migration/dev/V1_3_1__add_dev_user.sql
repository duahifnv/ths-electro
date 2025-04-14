insert into users (user_id, username, email, password, tag, firstname, lastname, middlename, avatar_id)
values (99, 'developer','dev@mail.org',
        '$2a$12$dorGevPbfpbzmfjoSWK.VuuV1wGO35KPIGQb5AG9EV6.mHdZqGNAu',
        'dev',
        'Игорь',
        'Мейби',
        null,
        null);

insert into users_roles (user_id, role_id)
values (99, 2),
       (99, 3);