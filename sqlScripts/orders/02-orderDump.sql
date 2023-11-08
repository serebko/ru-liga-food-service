
-- Наполнение restaurant

INSERT INTO public.restaurant
(id, address, status, "name")
VALUES(1, '56.88851626074396, 46.8856705552914', 'OPEN', 'PECTOPAH');
INSERT INTO public.restaurant
(id, address, status, "name")
VALUES(3, '54.00851626074396, 44.0056705552914', 'OPEN', 'Voyager');
INSERT INTO public.restaurant
(id, address, status, "name")
VALUES(4, '54.99851626074396, 44.9956705552914', 'OPEN', 'Alexa');
INSERT INTO public.restaurant
(id, address, status, "name")
VALUES(5, '54.12851626074396, 44.6856705552914', 'OPEN', 'Tripper Mag');
INSERT INTO public.restaurant
(id, address, status, "name")
VALUES(2, '54.88851626074396, 44.8856705552914', 'OPEN', 'Voyager-2');

-- Наполнение customer

INSERT INTO public.customer
(id, phone, email, address)
VALUES(1, '+79990782048', 'helium@yandex.ru', '56.26851626074396, 46.4656705552914');
INSERT INTO public.customer
(id, phone, email, address)
VALUES(2, '+79999992048', 'oxygen@yandex.ru', '56.00851626074396, 46.0056705552914');
INSERT INTO public.customer
(id, phone, email, address)
VALUES(3, '+79999999948', 'cuprum@rambler.ru', '56.99851626074396, 46.9956705552914');
INSERT INTO public.customer
(id, phone, email, address)
VALUES(4, '+79999999999', 'calcium@mail.ru', '56.55851626074396, 46.5556705552914');
INSERT INTO public.customer
(id, phone, email, address)
VALUES(5, '+79502604050', 'argentum@mail.ru', '56.88851626074396, 46.8856705552914');

-- Наполнение restaurant_menu_item

INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(1, 5, 'Солянка по-боярски', 790.00, 'URL-Solyanka', 'Роскошная солянка для настоящих бояр и боярынь');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(2, 5, 'Кура-гриль', 1500.00, 'URL-ChickenGrill', 'Премиальная кура-гриль на вертеле');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(3, 4, 'Варёное куриное яйцо', 850.00, 'URL-Egg', 'Изысканное куриное яичко варенное 10 минут в артезианской воде с морской солью');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(4, 4, 'Гвозди жареные', 199.90, 'URL-Nails', 'Отборные гвозди 100мм поджаренные на моторном масле 5w40');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(5, 3, 'Том-ям', 650.00, 'URL-Yam', 'Острый том-ям прямиком из Тайланда');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(6, 3, 'Шаурма', 220.00, 'URL-Shava', 'Та самая легендарная шавуха');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(7, 2, 'Le Pirozhoque', 560.00, 'URL-Pirozhok', 'Пирожок с трюфелем от нашего шефа Оливье Августина');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(8, 2, 'Чай из пакетика', 12.50, 'URL-Tea', 'Чай пакетированный + кипяток');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(9, 1, 'Пельмени покровские', 350.00, 'URL-Dumplings', 'Пельмешки в горшочке. Почувствуй себя барином!');
INSERT INTO public.restaurant_menu_item
(id, restaurant_id, "name", price, image, description)
VALUES(10, 1, 'Торт Тортила', 280.00, 'URL-Cake', 'Тортик масляный бисквит-фри');

-- Наполнение orders

INSERT INTO public.orders
(id, customer_id, restaurant_id, status, courier_uid, "timestamp", uid)
VALUES(3, 2, 2, 'CUSTOMER_CREATED', NULL, '2023-11-03 21:15:45.767', 'eaefe4ec-3208-436f-9359-fb33cd36d110');
INSERT INTO public.orders
(id, customer_id, restaurant_id, status, courier_uid, "timestamp", uid)
VALUES(8, 5, 3, 'CUSTOMER_CREATED', NULL, '2023-11-03 21:21:21.833', '89031a7e-440c-434a-a6dc-9a9618a19050');
INSERT INTO public.orders
(id, customer_id, restaurant_id, status, courier_uid, "timestamp", uid)
VALUES(14, 5, 5, 'CUSTOMER_PAID', NULL, '2023-11-06 23:47:20.112', 'e9153283-718b-4c48-b2ec-acd903dcac10');
INSERT INTO public.orders
(id, customer_id, restaurant_id, status, courier_uid, "timestamp", uid)
VALUES(4, 1, 3, 'CUSTOMER_CREATED', NULL, '2023-11-03 21:17:45.168', '40e60f97-e056-4c75-9dc4-60d14e6cc331');
INSERT INTO public.orders
(id, customer_id, restaurant_id, status, courier_uid, "timestamp", uid)
VALUES(5, 1, 4, 'CUSTOMER_CREATED', NULL, '2023-11-03 21:18:26.389', 'c6495d11-38fb-4699-aae4-75a66423afc2');
INSERT INTO public.orders
(id, customer_id, restaurant_id, status, courier_uid, "timestamp", uid)
VALUES(6, 3, 5, 'CUSTOMER_CREATED', NULL, '2023-11-03 21:19:21.744', 'f15aa0b6-28f9-4a7a-abe2-7a4b8745e8c0');
INSERT INTO public.orders
(id, customer_id, restaurant_id, status, courier_uid, "timestamp", uid)
VALUES(7, 4, 5, 'CUSTOMER_CREATED', NULL, '2023-11-03 21:20:37.743', '4db55e7b-4373-4b55-83f8-3656abd3d743');
INSERT INTO public.orders
(id, customer_id, restaurant_id, status, courier_uid, "timestamp", uid)
VALUES(21, 5, 1, 'CUSTOMER_PAID', NULL, '2023-11-08 23:58:41.045', 'c65ed33f-dbd9-4b43-943f-21c5639c732b');


-- Наполнение order_item

INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(12, 3, 8, 2, 25.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(13, 3, 7, 1, 560.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(14, 4, 5, 11, 7150.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(15, 4, 6, 12, 2640.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(16, 5, 3, 4, 3400.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(17, 5, 4, 2, 399.80);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(18, 6, 1, 20, 15800.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(19, 7, 2, 2, 3000.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(20, 8, 5, 1, 650.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(21, 14, 1, 2, 1580.00);
INSERT INTO public.order_item
(id, order_id, restaurant_menu_item, quantity, price)
VALUES(22, 21, 10, 2, 560.00);