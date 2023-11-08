create sequence if not exists customer_seq minvalue 100;

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

create sequence if not exists restaurant_seq minvalue 100;

create table if not exists restaurant
(
    id bigint not null default nextval('restaurant_seq'),
    address varchar(255) not null,
    status varchar(30) not null default 'OPEN',
    name varchar(50) not null,
    constraint restaurant_pk primary key (id)
    );

comment on table restaurant is 'Рестораны';
comment on column restaurant.id is 'Идентификатор ресторана';
comment on column restaurant.address is 'Адрес ресторана (координаты)';
comment on column restaurant.status is 'Статус кухни';

create sequence if not exists order_seq minvalue 100;

create table if not exists orders
(
    id bigint not null default nextval('order_seq'),
    uid varchar(36) not null,
    customer_id bigint not null,
    restaurant_id bigint not null,
    status varchar(30) not null default 'CUSTOMER_CREATED',
    courier_uid varchar(36),
    timestamp timestamptz not null default now(),
    constraint order_pk primary key (id),
    constraint order_customer_fk foreign key (customer_id)
    references customer (id),
    constraint order_restaurant_fk foreign key (restaurant_id)
    references restaurant (id)
    );

comment on table orders is 'Заказы';
comment on column orders.id is 'Идентификатор заказа';
comment on column orders.customer_id is 'Идентификатор заказчика';
comment on column orders.restaurant_id is 'Идентификатор ресторана';
comment on column orders.status is 'Статус заказа';
comment on column orders.courier_uid is 'Идентификатор курьера';
comment on column orders.timestamp is 'Дата время размещения заказа';

create sequence if not exists restaurant_menu_item_seq minvalue 100;

create table if not exists restaurant_menu_item
(
    id bigint not null default nextval('restaurant_menu_item_seq'),
    restaurant_id bigint not null,
    name varchar(255) not null,
    price numeric(10,2) not null default 0.00,
    image text,
    description varchar(255),
    constraint restaurant_menu_item_pk primary key (id),
    constraint restaurant_menu_item_restaurant_fk
    foreign key (restaurant_id) references restaurant (id)
    );

comment on table restaurant_menu_item is 'Позиции в меню ресторана';
comment on column restaurant_menu_item.id is 'Идентификатор позиции меню';
comment on column restaurant_menu_item.restaurant_id is 'Идентификатор ресторана';
comment on column restaurant_menu_item.name is 'Имя позиции';
comment on column restaurant_menu_item.price is 'Цена позиции';
comment on column restaurant_menu_item.image is 'Изображение позиции';
comment on column restaurant_menu_item.description is 'Описание позиции';

create sequence if not exists order_item_seq minvalue 100;

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