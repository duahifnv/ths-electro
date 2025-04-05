create table helpers(
    helper_id bigserial primary key,
    tg_id varchar(100) not null,
    firstname varchar(255),
    lastname varchar(255)
);

create table users_requests(
    request_id bigserial primary key,
    user_id bigint not null,
    message text not null,
    timestamp timestamp default current_timestamp not null
);

create table dialog_messages(
    message_id bigserial primary key,
    helper_id bigint references helpers(helper_id) not null,
    user_id bigint not null,
    message_to text check ( message_to in ('helper', 'user') ) not null,
    message text not null,
    timestamp timestamp not null
);