update orders set status = 'pending' where id = 3;

update orders set status = 'pending' where id = 4;

update orders set status = 'created' where id = 24;

update orders set status = 'created' where id = 25;

update orders set status = 'created' where id = 26;

update orders set status = 'pending' where id = 28;

update orders set status = 'pending' where id = 31;

update restaurants set status = 'pending' where id = 3;

update restaurants set status = 'denied' where id = 4;

update couriers set status = 'pending' where id = 3;

update couriers set status = 'picking' where id = 4;

alter table restaurants alter column status type varchar(10);

alter table couriers alter column status type varchar(10);

alter table orders alter column status type varchar(10);
