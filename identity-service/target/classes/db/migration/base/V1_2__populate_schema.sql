insert into users (username, email, password, tag, firstname, lastname, middlename, avatar_id)
values ('admin','admin@mail.org',
        '$2a$12$OLwZF7//Ze76hbSDL86AN.8Wn9N.mAwB6QkqlOLmSQ9Q.TYb4EKyO',
        'fizalise',
        'Максим',
        'Фоминцев',
        'Александрович',
        null),
       ('azazin','azazin@mail.org',
        '$2a$12$7R0anVY66Yw3aAP0xTcYgetuF9XPbeCngYUDNAuVZP0Nsy.VVRmBy',
        'azazlo',
        'Азазин',
        'Азазинов',
        'Кридович',
        null);

insert into roles (name)
values ('ROLE_ADMIN'), ('ROLE_USER');

insert into users_roles (user_id, role_id)
values (1, 1), (2, 2);