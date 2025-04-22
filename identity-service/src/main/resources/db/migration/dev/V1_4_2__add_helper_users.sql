insert into users (username, email, password, tag, firstname, lastname, middlename, avatar_id)
values ('support1','support1@mail.org',
        '$2a$12$lENhG208ZA.XAm7q.maJ9O4jl5dv3ecmZj0VNmqSTHkJT813fm.3C',
        'support1',
        'Саппорт_',
        'Саппортов_',
        'Саппортович_',
        null),
       ('support2','support2@mail.org',
        '$2a$12$7e8uvBFlDeNMrAu74.ZDYuxXre4Q9p3yS.dDIZyn7priOCv1ygOqS',
        'support2',
        'Саппорт__',
        'Саппортов__',
        'Саппортович__',
        null);

insert into users_roles (user_id, role_id)
values (4, 2),
       (4, 4),
       (5, 2),
       (5, 4);