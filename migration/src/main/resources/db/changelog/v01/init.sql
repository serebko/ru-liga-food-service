create sequence if not exists pib_right_seq;

create table if not exists pib_right
(
    right_id bigint not null default nextval('pib_right_seq'),
    rights jsonb,
    create_dttm timestamptz  not null default now(),
    modify_dttm timestamptz  not null default now(),
    constraint pib_right_pk primary key (right_id)
    );

comment on table pib_right is 'Права всех пользователей';
comment on column pib_right.right_id is 'Идентификатор';
comment on column pib_right.rights is 'Права';
comment on column pib_right.create_dttm is 'Дата время вставки записи в таблицу';
comment on column pib_right.modify_dttm is 'Дата время последнего изменения записи';

create sequence if not exists pib_user_seq;

create table if not exists pib_user
(
    user_id bigint not null default nextval ('pib_user_seq'),
    physical_person_id bigint,
    right_id bigint,
    login varchar(255),
    create_dttm timestamptz  not null default now(),
    modify_dttm timestamptz  not null default now(),
    constraint pib_user_pk primary key (user_id),
    constraint pib_user_pib_right_fk foreign key (right_id)
    references pib_right (right_id)
    );

comment on table pib_user is 'Пользователи';
comment on column pib_user.user_id is 'Идентификатор пользователя';
comment on column pib_user.physical_person_id is 'Идентификатор физ лица';
comment on column pib_user.right_id is 'Идентификатор прав';
comment on column pib_user.login is 'Логин';
comment on column pib_user.create_dttm is 'Дата время вставки записи в таблицу';
comment on column pib_user.modify_dttm is 'Дата время последнего изменения записи';