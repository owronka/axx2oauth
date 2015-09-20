insert into A2O_RIGHT values (11, 'CREATE_CUSTOMER', 'CUSTOMER', 'C');
insert into A2O_RIGHT values (12, 'READ_CUSTOMER',   'CUSTOMER', 'R');
insert into A2O_RIGHT values (13, 'UPDATE_CUSTOMER', 'CUSTOMER', 'U');
insert into A2O_RIGHT values (14, 'DELETE_CUSTOMER', 'CUSTOMER', 'D');
insert into A2O_RIGHT values (21, 'CREATE_ORDER',    'ORDER',    'C');
insert into A2O_RIGHT values (22, 'READ_ORDER',      'ORDER',    'R');
insert into A2O_RIGHT values (23, 'UPDATE_ORDER',    'ORDER',    'U');
insert into A2O_RIGHT values (24, 'DELETE_ORDER',    'ORDER',    'D');

insert into A2O_ROLE values (1, 'ADMIN');
insert into A2O_ROLE values (2, 'MANAGER');
insert into A2O_ROLE values (3, 'USER');
insert into A2O_ROLE values (4, 'GUEST');

-- user / password : admin / admin
insert into A2O_USER values (1, 'admin',   'admin@axxessio.com',   'Achim',   'Admin',   '69f4a14c684e5fe6ce885d7c88cfd60a853907034b8d2c94a94510be50b6d0f5896b12517a18b63fcc480023f727ebd6', '8d19e652a8421220d67ebaee0865413e');
-- user / password : manager / manager
insert into A2O_USER values (2, 'manager', 'manager@axxessio.com', 'Manfred', 'Manager', 'a24ff4f5cdc6b591ff73b921bda85f79e3407186a7c64aabe6225bdc101a91759e3e2d8242f23268c0b1fe6b0ef92c80', 'bcb314a2e508fffd14ef90a46ee535bb');
-- user / password :user /user
insert into A2O_USER values (3, 'user',    'user@axxessio.com',    'Udo',     'User',    '75801dc54e7c87e3ea94ef180f71cc37d61ee04f9a1f7758a3d094f0ae2e2a9d0b037acafde1d2ae4dfb3d17a116002b', 'd5451004e416691d14862ea1981fe4b2');
 
insert into A2O_ROLERIGHT values (1, 11);
insert into A2O_ROLERIGHT values (1, 12);
insert into A2O_ROLERIGHT values (1, 13);
insert into A2O_ROLERIGHT values (1, 14);
insert into A2O_ROLERIGHT values (1, 21);
insert into A2O_ROLERIGHT values (1, 22);
insert into A2O_ROLERIGHT values (1, 23);
insert into A2O_ROLERIGHT values (1, 24);
insert into A2O_ROLERIGHT values (2, 11);
insert into A2O_ROLERIGHT values (2, 12);
insert into A2O_ROLERIGHT values (2, 13);
insert into A2O_ROLERIGHT values (2, 21);
insert into A2O_ROLERIGHT values (2, 22);
insert into A2O_ROLERIGHT values (2, 23);
insert into A2O_ROLERIGHT values (3, 12);
insert into A2O_ROLERIGHT values (3, 22);

insert into A2O_USERROLE values (1, 1);
insert into A2O_USERROLE values (2, 2);
insert into A2O_USERROLE values (3, 3);

