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
comment on column orders.restaurant_id is 'Идентификатор ресорана';
comment on column orders.status is 'Статус заказа';
comment on column orders.courier_id is 'Идентификатор курьера';
comment on column orders.timestamp is 'Дата время размещения заказа';

create sequence if not exists restaurant_menu_items_seq;

create table if not exists restaurant_menu_items
(
    id bigint not null default nextval('restaurant_menu_items_seq'),
    restaurant_id bigint not null,
    name varchar(255) not null,
    price numeric(10,2) not null default 0.00,
    image text,
    description varchar(255),
    constraint restaurant_menu_items_pk primary key (id),
    constraint restaurant_menu_items_restaurants_fk
    foreign key (restaurant_id) references restaurants (id)
);

comment on table restaurant_menu_items is 'Позиции в меню ресторана';
comment on column restaurant_menu_items.id is 'Идентификатор позиции меню';
comment on column restaurant_menu_items.restaurant_id is 'Идентификатор ресторана';
comment on column restaurant_menu_items.name is 'Имя позиции';
comment on column restaurant_menu_items.price is 'Цена позиции';
comment on column restaurant_menu_items.image is 'Изображение позиции';
comment on column restaurant_menu_items.description is 'Описание позиции';


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