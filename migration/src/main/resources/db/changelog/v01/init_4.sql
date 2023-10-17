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