create sequence if not exists order_seq;

create table if not exists orders
(
    id bigint not null default nextval('order_seq'),
    customer_id bigint not null,
    restaurant_id bigint not null,
    status varchar(30) not null default 'CUSTOMER_CREATED',
    courier_id bigint,
    timestamp timestamptz not null default now(),
    constraint order_pk primary key (id),
    constraint order_customer_fk foreign key (customer_id)
        references customer (id),
    constraint order_restaurant_fk foreign key (restaurant_id)
        references restaurant (id),
    constraint order_courier_fk foreign key (courier_id)
        references courier (id)
);

comment on table orders is 'Заказы';
comment on column orders.id is 'Идентификатор заказа';
comment on column orders.customer_id is 'Идентификатор заказчика';
comment on column orders.restaurant_id is 'Идентификатор ресторана';
comment on column orders.status is 'Статус заказа';
comment on column orders.courier_id is 'Идентификатор курьера';
comment on column orders.timestamp is 'Дата время размещения заказа';