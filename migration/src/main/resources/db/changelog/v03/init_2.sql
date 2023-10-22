alter table orders alter column status type varchar(30);

alter table couriers alter column status type varchar(30);

alter table restaurants alter column status type varchar(30);

update orders set status = 'CUSTOMER_CREATED' where id = 3;

update orders set status = 'CUSTOMER_PAID' where id = 4;

update orders set status = 'KITCHEN_ACCEPTED' where id = 24;

update orders set status = 'KITCHEN_PREPARING' where id = 25;

update orders set status = 'DELIVERY_PENDING' where id = 26;

update orders set status = 'DELIVERY_PICKING' where id = 28;

update orders set status = 'DELIVERY_COMPLETE' where id = 31;

update restaurants set status = 'KITCHEN_ACCEPTED' where id = 3;

update restaurants set status = 'KITCHEN_PREPARING' where id = 4;

update couriers set status = 'DELIVERY_PICKING' where id = 3;

update couriers set status = 'DELIVERY_DELIVERING' where id = 4;