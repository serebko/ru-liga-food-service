create sequence if not exists restaurant_menu_item_seq;

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