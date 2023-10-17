create sequence if not exists customers_seq;

create table if not exists customers
(
    id bigint not null default nextval('customers_seq'),
    phone varchar(20) not null,
    email varchar(255),
    address varchar(255) not null,
    constraint customers_pk primary key (id)
);

comment on table customers is 'Заказчики';
comment on column customers.id is 'Идентификатор заказчика';
comment on column customers.phone is 'Номер телефона заказчика';
comment on column customers.email is 'Email заказчика';
comment on column customers.address is 'Адрес заказчика';