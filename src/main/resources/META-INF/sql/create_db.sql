create sequence A2O_ACCESS_TOKEN_ID as integer start with 100;

create table A2O_RIGHT			(RI_ID      integer primary key, 	RI_NAME       	varchar (20) 	not null, 	RI_SCOPE      	varchar (20) 	not null,	RI_RIGHT      	varchar (1)  	not null);
create table A2O_ROLE			(RO_ID    	integer primary key,  	RO_NAME  		varchar (20) 	not null);
create table A2O_USER			(U_ID       integer	primary key,  	U_NAME          varchar (50)  	not null,  	U_EMAIL        	varchar (80) 	not null,  	U_FIRST_NAME    varchar (50) 	not null,  U_LAST_NAME     	varchar (50)  	not null,  U_PASSWORD      	varchar (200)	not null,  U_SALT          	varchar (40)	not null);
create table A2O_ROLERIGHT		(RR_RO_ID  	integer not null,  		RR_RI_ID  		integer 		not null);
create table A2O_USERROLE		(UR_U_ID   	integer not null,  		UR_RO_ID 		integer 		not null);
create table A2O_ACCESS_TOKEN	(AT_ID		integer primary key,  	AT_ACCESS_TOKEN varchar (255)	not null,  	AT_TOKEN_TYPE	varchar (10)	not null,  	AT_EXPIRES_IN 	integer			not null,  AT_REFRESH_TOKEN varchar (255)	not null,  AT_SESSION_ID	varchar (255)	not null,  AT_CREATED_AT	timestamp       not null,  AT_USER varchar (50) not null,  AT_SESSION_SCOPE blob (4k));
