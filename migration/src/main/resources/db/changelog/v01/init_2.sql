create sequence if not exists restaurant_seq;

create table if not exists restaurant
(
    id bigint not null default nextval('restaurant_seq'),
    address varchar(255) not null,
    status varchar(30) not null,
    name varchar(50) not null,
    constraint restaurant_pk primary key (id)
);

comment on table restaurant is 'Рестораны';
comment on column restaurant.id is 'Идентификатор ресторана';
comment on column restaurant.address is 'Адрес ресторана (координаты)';
comment on column restaurant.status is 'Статус кухни';