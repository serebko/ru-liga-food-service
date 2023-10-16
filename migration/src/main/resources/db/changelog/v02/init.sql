insert into couriers (phone, status, coordinates) values
    ('+79990785698', 'complete', '564564565'),
    ('+79081694579', 'active', '9898989212');

insert into customers (phone, email, address) values
    ('+79505551234', 'argon@yandex.ru', 'г. Богородск, ул. Юргенса, 4А'),
    ('+79107856419', 'cuprum@gmail.com', 'ул. Нестерова, 9');

insert into restaurants (address, status) values
    ('ул. Рождественская, 32', 'active'),
    ('ул. Вторчермета, 146Б стр.1 лит.А', 'complete');

insert into orders (customer_id, restaurant_id, status, courier_id) values
    ((select id from customers where phone = '+79505551234'),
        (select id from restaurants where restaurants.status = 'active'),
            'active', (select id from couriers where couriers.status = 'active')),
    ((select id from customers where phone = '+79107856419'),
        (select id from restaurants where restaurants.status = 'complete'),
            'complete', (select id from couriers where couriers.status = 'complete'));

insert into restaurant_menu_items (restaurant_id, name, price, image, description) values
    ((select id from restaurants where status = 'active'), 'Кура-гриль', 1500.00,
        'url or whatever', 'Премиальная кура-гриль на вертеле'),
    ((select id from restaurants where status = 'complete'), 'Варёное куриное яйцо', 850.00,
        'url', 'Изысканное куриное яичко варенное 10 минут в артезианской воде с морской солью');

insert into order_items (order_id, restaurant_menu_item, quantity, price) values
    ((select id from orders where status = 'active'),
        (select id from restaurant_menu_items where name = 'Кура-гриль'), 2,
            ((select price from restaurant_menu_items where name = 'Кура-гриль') * 2)),
    ((select id from orders where status = 'complete'),
        (select id from restaurant_menu_items where name = 'Варёное куриное яйцо'), 10,
            ((select price from restaurant_menu_items where name = 'Варёное куриное яйцо') * 10));