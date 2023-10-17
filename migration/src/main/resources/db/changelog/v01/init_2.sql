create sequence if not exists restaurants_seq;

create table if not exists restaurants
(
    id bigint not null default nextval('restaurants_seq'),
    address varchar(255) not null,
    status varchar(10),
    constraint restaurant_pk primary key (id)
);

comment on table restaurants is 'Рестораны';
comment on column restaurants.id is 'Идентификатор ресторана';
comment on column restaurants.address is 'Адрес ресторана';
comment on column restaurants.status is 'Статус кухни';