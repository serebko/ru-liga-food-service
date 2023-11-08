-- БД для delivery service

create sequence if not exists courier_seq minvalue 100;
create sequence if not exists couriers_orders_seq minvalue 100;

create table if not exists courier
(
    id bigint not null default nextval('courier_seq'),
    uid varchar(36) not null,
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

create table if not exists couriers_orders
(
    id bigint not null default nextval('couriers_orders_seq'),
    courier_id bigint not null,
    order_uid varchar(36) not null,
    constraint couriers_orders_pk primary key (id),
    constraint courier_id_fk foreign key (courier_id) references courier (id)
    );

comment on table couriers_orders is 'Заказы курьеров';
comment on column couriers_orders.id is 'Идентификатор записи';
comment on column couriers_orders.order_uid is 'Идентификатор заказа';