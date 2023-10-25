create sequence if not exists orders_seq;

create table if not exists orders
(
    id bigint not null default nextval('orders_seq'),
    customer_id bigint not null,
    restaurant_id bigint not null,
    status varchar(10),
    courier_id bigint not null,
    timestamp timestamptz not null default now(),
    constraint orders_pk primary key (id),
    constraint orders_customers_fk foreign key (customer_id)
        references customers (id),
    constraint orders_restaurants_fk foreign key (restaurant_id)
        references restaurants (id),
    constraint orders_couriers_fk foreign key (courier_id)
        references couriers (id)
);

comment on table orders is 'Заказы';
comment on column orders.id is 'Идентификатор заказа';
comment on column orders.customer_id is 'Идентификатор заказчика';
comment on column orders.restaurant_id is 'Идентификатор ресторана';
comment on column orders.status is 'Статус заказа';
comment on column orders.courier_id is 'Идентификатор курьера';
comment on column orders.timestamp is 'Дата время размещения заказа';