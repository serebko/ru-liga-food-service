create sequence if not exists courier_seq;

create table if not exists courier
(
    id bigint not null default nextval('courier_seq'),
    phone varchar(20) not null,
    status varchar(30) not null default 'PENDING',
    coordinates varchar(50),
    constraint couriers_pk primary key (id)
);

comment on table courier is 'Курьеры';
comment on column courier.id is 'Идентификатор курьера';
comment on column courier.phone is 'Номер телефона курьера';
comment on column courier.status is 'Статус курьера';
comment on column courier.coordinates is 'Координаты курьера';