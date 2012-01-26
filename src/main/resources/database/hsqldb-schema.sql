
create table signon (
	id varchar(36) not null,
    username varchar(25) not null,
    password varchar(25)  not null,
    constraint pk_signon primary key (id)
);

create unique index singon_username on signon (username);

create table account (
	id varchar(36) not null,
    signonId varchar(37) not null,
    firstname varchar(80) not null,
    lastname varchar(80) not null,
    constraint pk_account primary key (id),
    constraint fk_signonId foreign key (signonId)
        references signon (id)
);

create table field_type (
    id varchar(37) not null,
    pattern varchar(8),
    constraint pk_field_type primary key (id)
);


create table account_field (
    id varchar(37) not null,
    accountId varchar(37) not null,
    typeId varchar(37) not null,
    text varchar(80) null,
    constraint pk_account_field primary key (id),
    constraint fk_fieldTypeId foreign key (typeId)
        references field_type (id)
);

