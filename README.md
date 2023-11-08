## Архитектура приложения

![img.png](res/img.png)
### Типы сообщений в RabbitMQ:
1) CUSTOMER_PAID
2) KITCHEN_DENYED
3) KITCHEN_ACCEPTED
4) DELIVERY_PENDING -- заказ готов/ожидает курьера
5) DELIVERY_PICKING -- заказ в процессе доставки
6) DELIVERY_COMPLETE -- заказ доставлен
7) KITCHEN_REFUNDED -- ресторан вернул деньги

### Потребители типов сообщений:
- 1 -> Kitch
- 2, 3, 5, 6, 7 -> Order
- 4 -> Order, Deliv


### Security
Security реализовано для каждого сервиса собстенное, используется Basic Auth (inMemory). Друг у друга сервисы аутентифицируются по техническим учёткам (в конфиге прописаны)

### Docker-compose 
Docker-compose используется для запуска 2х БД и RabbitMQ. Добавление своих сервисов сделать пока не успел (notification в docker-compose.yml закомментирован)

====
### Инструкция по тестированию положительного сценария
1. Запуск docker-compose (он содержит в себе две БД и RabbitMq, notification-service закомментирован)
   * Должно появиться две базы: order и courier и они должны быть наполнены данными, на портах 5433 и 5434 соответственно.
   * Должен подняться rabbitMQ без очередей на порту 15672.
   * Запуск notification-service из IDE. В RabbitMq появятся 3 очереди: toOrder, to Kitchen, toDelivery
   * Запуск остальных сервисов из IDE (order, kitchen, delivery)
2. Заказ создаётся посредством:
   curl --location 'http://localhost:8081/orders' \
   --header 'Content-Type: application/json' \
   --header 'Cookie: JSESSIONID=EE2C9D68B0A04BEA6F8CDEA46A5182F5' \
   --data '{
   "restaurantId": 1,
   "menuItems": [
   {
   "quantity": 2,
   "menuItemId": 10
   }
   ]
   }'
3. В лог order-service отбивается:   // точные цитаты
   - Поиск ресторана по id =
   - Поиск заказчика по ид
   - Сохранение заказа в БД
   - Заказ сохранён
   - Маппинг ДТО позиций заказа на сущность
   - Успешный маппинг. Сохранение их в БД
   - Позиции сохранены 
4. Заказу присваивается UUID и статус CUSTOMER_CREATED. Это видно в таблице public.orders
   - Получаем ответ 200 (Заказ создан, ожидаем оплату). В ответе получаем UID заказа
5. "Оплачиваем" заказ посредством (подставить UID заказа):
   curl --location 'localhost:8081/pay/4db55e7b-4373-4b55-83f8-3656abd3d743' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Basic cmFtZXNoOnBhc3N3b3Jk' \
   --header 'Cookie: JSESSIONID=1D2335275F216E821D837CCC32E41438; JSESSIONID=9C4B509E07592B8054A825C4D03F8015'
   - Присвоение заказу статуса CUSTOMER_PAID (статус быстро поменяется на KITCHEN_ACCEPTED), Это видно в таблице public.orders
   - Отправка сообщения в notification-service (в логах можно найти по UID)
6. Приём нового сообщения c информацией о заказе в notification service и его пересылка в kitchen-service	через RabbitMq (очередь toKitchen)
   В лог notification-service отбивается "has been sanded with key"
7. Приём сообщения в kitchen-service, в лог отбивается "Новый заказ: "
   - Кухня одобряет заказ и меняет статус заказа на KITCHEN_ACCEPTED и отправляет сообщение в notification-service, который перенаправит его в order-service для изменения статуса.
   - В лог notification-service отбивается "Отправляется сообщение: ", а в логе order-service отбивается содержимое сообщения, в таблице public.orders видно изменение статуса
8. Завершаем "приготовление" заказа посредством (заменить UID на актуальный):
   curl --location --request POST 'localhost:8082/kitchen/c65ed33f-dbd9-4b43-943f-21c5639c732b/ready' \
   --header 'Cookie: JSESSIONID=9C4B509E07592B8054A825C4D03F8015'
9. Статус заказа меняется на DELIVERY_PENDING (см в БД, но статус быстро меняется на DELIVERY_PICKING).
10. Заказ отправляется в notification service
11. Приём нового сообщения c информацией о заказе в notification service и его пересылка в delivery-service, а так же в order-service, для обновления статуса.
   В лог notification-service отбивается "Отправляется сообщение: ", а в логе order-service отбивается содержимое сообщения, в таблице public.orders видно изменение
12. Приём сообщения в delivery-service
    - В лог отбивается "New delivery!"
    - Находится ближайший свободный курьер, который принимает заказ.
    - Статус заказа меняется на DELIVERY_PICKING (см в БД)
13. Заказ отправляется в notification-service с UUID курьера, чтобы присвоить его заказу. 
    - в лог delivery-service отбивается: "Статус заказа id=" + messageFromRabbit.getOrderItems() + " изменён на: " + status.name()"
14. Приём нового сообщения c информацией о заказе в notification service и его пересылка в order-service
    - В лог notification-service отбивается "Отправляется сообщение: ", а в логе order-service отбивается содержимое сообщения, в таблице public.orders видно изменение
15. Order-service принимает сообщение и отбивает в лог содержимое сообщения
    - Заказу присваивается UUID курьера (см в БД), который взял этот заказ на доставку и обновляется статус заказа на DELIVERY_PICKING. Это видно в таблице public.orders
16. "Завершить" доставку заказа можно посредством (UID заменить на актуальный):
   curl --location --request POST 'localhost:8080/delivery/40e60f97-e056-4c75-9dc4-60d14e6cc331/complete' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Basic cmFtZXNoOnBhc3N3b3Jk' \
   --header 'Cookie: JSESSIONID=1D2335275F216E821D837CCC32E41438; JSESSIONID=9C4B509E07592B8054A825C4D03F8015'
17. Сообщение об этом отправляется в notification-service, заказу присваивается статус DELIVERY_COMPLETE
18. Приём нового сообщения c информацией о заказе в notification service и его пересылка в order-service
    В лог notification-service отбивается "Отправляется сообщение: ",  а в логе order-service отбивается содержимое сообщения, в таблице public.orders видно изменение
