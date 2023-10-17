create sequence if not exists order_items_seq;

create table if not exists order_items
(
    id bigint not null default nextval('order_items_seq'),
    order_id bigint not null,
    restaurant_menu_item bigint not null,
    quantity integer not null default 0,
    price numeric(10,2) not null default 0.00,
    constraint order_items_pk primary key (id),
    constraint order_items_restaurant_menu_items_fk
        foreign key (restaurant_menu_item)
            references restaurant_menu_items (id),
    constraint order_items_orders_fk foreign key (order_id)
        references orders (id)
);

comment on table order_items is 'Позиции заказа';
comment on column order_items.id is 'Идентификатор позиции заказа';
comment on column order_items.order_id is 'Идентификатор заказа';
comment on column order_items.restaurant_menu_item is 'Позиция из меню ресторана';
comment on column order_items.price is 'Общая цена позиции';
comment on column order_items.quantity is 'Количество товаров в позиции';