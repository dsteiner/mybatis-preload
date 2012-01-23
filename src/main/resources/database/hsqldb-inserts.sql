INSERT INTO signon (id,username,password) VALUES('b2835140-a986-beff-2d08-a17dddad1b4e', 'j2ee','j2ee');
INSERT INTO signon (id,username,password) VALUES('e554229a-c896-3d0c-1daf-1161e30baacc', 'ACID','ACID');

INSERT INTO account VALUES('bb1ad573-19b8-9cd8-68fb-0e6f684df992','b2835140-a986-beff-2d08-a17dddad1b4e', 'W.', 'MŸller');
INSERT INTO account VALUES('352cccfc-0946-b8f0-552c-f1e4a8ab85dd','e554229a-c896-3d0c-1daf-1161e30baacc', 'L.', 'Steiner');


insert into account_field (id,accountId,type,text) values( '0a6bebed-e81b-8c71-7f08-f1e2eb25b1a2', 'bb1ad573-19b8-9cd8-68fb-0e6f684df992', 0, 'email 1');
insert into account_field (id,accountId,type,text) values( '3a5fbfd8-a8ac-15e6-3102-c61e60b4e34e', 'bb1ad573-19b8-9cd8-68fb-0e6f684df992', 0, 'email 2');

