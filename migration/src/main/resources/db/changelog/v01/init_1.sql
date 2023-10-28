create sequence if not exists customer_seq;

create table if not exists customer
(
    id bigint not null default nextval('customer_seq'),
    phone varchar(20) not null,
    email varchar(255) not null,
    address varchar(255) not null,
    constraint customers_pk primary key (id)
);

comment on table customer is 'Заказчики';
comment on column customer.id is 'Идентификатор заказчика';
comment on column customer.phone is 'Номер телефона заказчика';
comment on column customer.email is 'Email заказчика';
comment on column customer.address is 'Адрес заказчика (координаты)';