create sequence if not exists order_item_seq;

create table if not exists order_item
(
    id bigint not null default nextval('order_item_seq'),
    order_id bigint not null,
    restaurant_menu_item bigint not null,
    quantity integer not null default 0,
    price numeric(10,2) not null default 0.00,
    constraint order_item_pk primary key (id),
    constraint order_item_restaurant_menu_item_fk
        foreign key (restaurant_menu_item)
            references restaurant_menu_item (id),
    constraint order_item_order_fk foreign key (order_id)
        references orders (id)
);

comment on table order_item is 'Позиции заказа';
comment on column order_item.id is 'Идентификатор позиции заказа';
comment on column order_item.order_id is 'Идентификатор заказа';
comment on column order_item.restaurant_menu_item is 'Позиция из меню ресторана';
comment on column order_item.price is 'Общая цена позиции';
comment on column order_item.quantity is 'Количество товаров в позиции';