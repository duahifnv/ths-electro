insert into users (username, email, password, tag, firstname, lastname, middlename, avatar_id)
values ('support','support@mail.org',
        '$2a$12$0DtkijhfrWmtroDKySMUkuywYObMrrMikxZwOFGOtaTASONfgiWbW',
        'support',
        'Саппорт',
        'Саппортов',
        'Саппортович',
        null);

insert into users_roles (user_id, role_id)
values (3, 2),
       (3, 4);