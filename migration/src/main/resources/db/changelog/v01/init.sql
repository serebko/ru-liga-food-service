create sequence if not exists couriers_seq;

create table if not exists couriers
(
    id bigint not null default nextval('couriers_seq'),
    phone varchar(20) not null,
    status varchar(10),
    coordinates varchar(30),
    constraint couriers_pk primary key (id)
);

comment on table couriers is 'Доставки';
comment on column couriers.id is 'Идентификатор курьера';
comment on column couriers.phone is 'Номер телефона курьера';
comment on column couriers.status is 'Статус доставки';
comment on column couriers.coordinates is 'Координаты курьера';