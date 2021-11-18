create table if not exists t_user(
    id varchar(100) primary key,
    name varchar(100) not null,
    pass varchar(100) not null,
    stamps integer default 0
);

create table if not exists t_product(
    id integer primary key autoincrement,
    name varchar(100) not null,
    type varchar(20) not null,
    price integer not null,
    img varchar(100) not null
);

create  table if not exists t_order(
    o_id  integer primary key autoincrement,
    user_id varchar(100) not null,
    order_table varchar(20),
    order_time timestamp default CURRENT_TIMESTAMP,
    completed char(1) default 'N',
    constraint fk_order_user foreign key (user_id) references t_user(id) on delete cascade
);

create table if not exists  t_order_detail(
    d_id integer primary key autoincrement,
    order_id integer not null,
    product_id integer not null,
    quantity integer not null default 1,
    constraint fk_order_detail_product foreign key (product_id) references t_product(id) on delete cascade,
    constraint fk_order_detail_order foreign key(order_id) references t_order(o_id) on delete cascade
);

create table if not exists t_stamp(
    id integer primary key autoincrement,
    user_id varchar(100) not null,
    order_id integer not null,
    quantity integer not null default 1,
    constraint fk_stamp_user foreign key (user_id) references t_user(id) on delete cascade,
    constraint fk_stamp_order foreign key (order_id) references t_order(o_id) on delete cascade
);

create table if not exists t_comment(
    id integer primary key autoincrement,
    user_id varchar(100) not null,
    product_id integer not null,
    rating float not null default 1,
    comment varchar(200),
    constraint fk_comment_user foreign key(user_id) references t_user(id) on delete cascade,
    constraint fk_comment_product foreign key(product_id) references t_product(id) on delete cascade
);

INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy01', '김싸피', 'pass01', 5);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy02', '황원태', 'pass02', 0);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy03', '한정일', 'pass03', 3);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy04', '반장운', 'pass04', 4);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy05', '박하윤', 'pass05', 5);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy06', '정비선', 'pass06', 6);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy07', '김병관', 'pass07', 7);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy08', '강석우', 'pass08', 8);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy09', '견본무', 'pass09', 9);
INSERT INTO t_user (id, name, pass, stamps) VALUES ('ssafy10', '전인성', 'pass10', 20);

INSERT INTO t_product (name, type, price, img) VALUES ('아메리카노', 'coffee', 4100, 'coffee1.png');
INSERT INTO t_product (name, type, price, img) VALUES ('카페라떼', 'coffee', 4500, 'coffee2.png');
INSERT INTO t_product (name, type, price, img) VALUES ('카라멜 마끼아또', 'coffee', 4800, 'coffee3.png');
INSERT INTO t_product (name, type, price, img) VALUES ('카푸치노', 'coffee', 4800, 'coffee4.png');
INSERT INTO t_product (name, type, price, img) VALUES ('모카라떼', 'coffee', 4800, 'coffee5.png');
INSERT INTO t_product (name, type, price, img) VALUES ('민트라떼', 'coffee', 4300, 'coffee6.png');
INSERT INTO t_product (name, type, price, img) VALUES ('화이트 모카라떼', 'coffee', 4800, 'coffee7.png');
INSERT INTO t_product (name, type, price, img) VALUES ('자몽에이드', 'coffee', 5100, 'coffee8.png');
INSERT INTO t_product (name, type, price, img) VALUES ('레몬에이드', 'coffee', 5100, 'coffee9.png');
INSERT INTO t_product (name, type, price, img) VALUES ('초코칩 쿠키', 'cookie', 1500, 'cookie.png');

INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy01', 01, 01, '신맛 강한 커피는 좀 별루네요.');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy02', 01, 02, '커피 맛을 좀 신경 써야 할 것 같네요.');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy03', 01, 03, '그냥 저냥');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy04', 04, 04, '갠춘한 맛');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy05', 05, 05, 'SoSSo');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy06', 06, 06, '그냥 저냥 먹을만함');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy07', 07, 10, '이집 화이트 모카라떼가 젤 나은듯');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy08', 08, 08, '자몽 특유의 맛이 살아있네요.');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy09', 08, 09, '수제 자몽에이드라 그런지 맛나요.');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy10', 10, 10, '초코칩 쿠키 먹으로 여기 옵니다.');

INSERT INTO t_order (user_id, order_table) VALUES ('ssafy01', 'order_table 01');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy01', 'order_table 02');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy03', 'order_table 03');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy04', 'order_table 04');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy05', 'order_table 05');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy06', 'order_table 06');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy07', 'order_table 07');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy08', 'order_table 08');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy09', 'order_table 09');
INSERT INTO t_order (user_id, order_table) VALUES ('ssafy10', 'order_table 10');

INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (01, 01, 01);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (01, 02, 03);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (02, 08, 01);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (03, 03, 03);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (04, 04, 04);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (05, 05, 05);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (06, 06, 06);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (07, 07, 07);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (08, 08, 08);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (09, 09, 09);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (10, 8, 10);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (10, 10, 10);


INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy01', 1, 04);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy01', 2, 01);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy03', 3, 03);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy04', 4, 04);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy05', 5, 05);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy06', 6, 06);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy07', 7, 07);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy08', 8, 08);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy09', 9, 09);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy10', 10, 20);
