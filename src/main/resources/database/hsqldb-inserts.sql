INSERT INTO signon (id,username,password) VALUES('b2835140-a986-beff-2d08-a17dddad1b4e', 'j2ee','j2ee');
INSERT INTO signon (id,username,password) VALUES('e554229a-c896-3d0c-1daf-1161e30baacc', 'ACID','ACID');

INSERT INTO account VALUES('bb1ad573-19b8-9cd8-68fb-0e6f684df992','b2835140-a986-beff-2d08-a17dddad1b4e', 'W.', 'MŸller');
INSERT INTO account VALUES('352cccfc-0946-b8f0-552c-f1e4a8ab85dd','e554229a-c896-3d0c-1daf-1161e30baacc', 'L.', 'Steiner');

insert into field_type (id,pattern) values( '8791fd99-efca-347c-eeb9-52988a95c453', 'pattern 2');
insert into field_type (id,pattern) values( '1bf59de9-dc56-ea4d-a7a7-7ff1eb450c4c', 'pattern 1');

insert into account_field (id,accountId,typeId,text) values( '0a6bebed-e81b-8c71-7f08-f1e2eb25b1a2', 'bb1ad573-19b8-9cd8-68fb-0e6f684df992', '1bf59de9-dc56-ea4d-a7a7-7ff1eb450c4c', 'email 1');
insert into account_field (id,accountId,typeId,text) values( '3a5fbfd8-a8ac-15e6-3102-c61e60b4e34e', 'bb1ad573-19b8-9cd8-68fb-0e6f684df992', '1bf59de9-dc56-ea4d-a7a7-7ff1eb450c4c', 'email 2');
insert into account_field (id,accountId,typeId,text) values( 'b74afb5e-9ac7-43f8-7647-2e68ef70bef3', 'bb1ad573-19b8-9cd8-68fb-0e6f684df992', '8791fd99-efca-347c-eeb9-52988a95c453', 'telefon 1');
insert into account_field (id,accountId,typeId,text) values( 'c3572d82-2793-318f-746f-5729e7020520', 'bb1ad573-19b8-9cd8-68fb-0e6f684df992', '8791fd99-efca-347c-eeb9-52988a95c453', 'telefon 2');

