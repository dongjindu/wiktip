create database dictionary charset utf8;
drop table voc;
create table voc (word varchar(50), dragged boolean default false, suggested boolean default false, over10000 boolean default false, docnull boolean default false, m1count int default 0, mw text default "", primary key (word));
delete from voc;
load data infile "D:\\javacode\\v21000new.txt" replace into table voc (word) set dragged = false, suggested = false, over10000 = false, docnull = false; ---- ignore 21830 lines (word);
update voc set word = ltrim(rtrim(word));
delete from voc where word in ("\r");
update voc set word = substr(word, 1, locate("\r", word) - 1) where locate("\r", word) > 0;

delete from voc where word in ("", "\r") or length(word) = 0;

drop table if exists meaning0;
create table meaning0(word varchar(50), entry varchar(80), ieo int default 0, voice varchar(100), wordtype varchar(50),
       odate varchar(50), primary key(word, entry)) DEFAULT CHARSET=utf8 COLLATE utf8_bin;;
drop table if exists meaning1;
create table meaning1(word varchar(50), entry varchar(80), ieo int default 0, isn int, sn varchar(10),
       meaning varchar(800), primary key(word, entry, isn)) DEFAULT CHARSET=utf8 COLLATE utf8_bin;;
drop table if exists notfound;
create table notfound(word varchar(50), suggestion varchar(50), primary key (word, suggestion)) DEFAULT CHARSET=utf8 COLLATE utf8_bin;;
drop table dict;
create table dict(word varchar(50), ltext text, m1count int default 0, primary key (word));
drop table if exists dict300;
create table dict300(word varchar(50), ltext text, m1count int default 0, primary key (word));
drop table if exists dict350;
create table dict350(word varchar(50), ltext text, m1count int default 0, primary key (word));

select distinct word  from meaning1 where word not in (select word from dict350);
update voc set suggested = false, dragged = false, over10000=false, docnull = false, mw = "";
delete from meaning0;
delete from meaning1;
delete from notfound;

delete from voc;

select * from meaning0;
select * from meaning1;
select * from notfound;
select * from voc where length(word) < 3;
select * from voc inner join notfound on voc.word = notfound.word;
select * from voc where word in (select distinct word from notfound);
select * from voc where word regexp 'head' or word regexp 'sight';
select * from voc where over10000 is true;
select * from voc where docnull is true;
select * from voc where dragged is true and docnull is true;
select * from meaning0 where word in (select word from voc where dragged is true and docnull is true);
select * from meaning1 where word in (select word from voc where dragged is true and docnull is true);
select word, count(*) as numberofentries from meaning1 group by word having numberofentries > 3 order by numberofentries desc ;
select count(*) from dict350;

select word into outfile "d:\\javacode\\v21000new.txt" from voc;

alter table voc add column over10000 boolean default false after suggested;
alter table voc add column docnull boolean default false after over10000;
alter table voc add column m1count int;
alter table voc modify column mw text;
alter table dict add column m1count int;

alter table meaning1 modify column meaning varchar(800);
update voc set docnull = false;
update voc set word = lower(word);
update voc set m1count = (select count(*) from meaning1 m1 where m1.word = voc.word);
update dict300 set m1count = (select count(*) from meaning1 m1 where m1.word = dict300.word);

create table dict350a like dict350;
create table dict350b like dict350;

delete from dict350;
delete d:\temp\test350.txt;

update dict350b set ltext=replace(ltext, "&", "&#x26;");
select "<!DOCTYPE html><html><head><link rel=\"stylesheet\" type=\"text\/css\" media=\"all\" href=\"t1.css\"/></head><body><div id=\"mc\">", "", "", "", ""
union select "<p><word>", word, "</word><ltext>", ltext, "</ltext></p>"  from dictionary.dict350b
union select "</div></body>", "", "", "", "" into outfile "d:\\temp\\test350b.htm";

select "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-stylesheet type=\"text/xsl\" href=\"dict.xsl\"?><dict>",
 "", "", "", ""
union select "<p><word>", ltrim(rtrim(word)), "</word><ltext>", ltext, "</ltext></p>"  from dictionary.dict350b limit 100, 320
union select "</dict>", "", "", "", "" into outfile "d:\\temp\\test350btest.xml";

select count(*) from meaning0 where entry in 
( select entry from meaning0 order by word asc, ieo asc limit 0, 1) order by word asc, ieo asc;

select count(distinct entry) from meaning0 union select count(*) from meaning0 where voice != "";
create table meaning0a like meaning0;

delimiter //
drop procedure if exists curdemo//
drop procedure if exists m0a//
CREATE PROCEDURE m0a()
BEGIN
  DECLARE done1 INT DEFAULT FALSE;
  DECLARE have1 INT DEFAULT 0;
  DECLARE word0 varchar(50);
  DECLARE entry0 varchar(80);
  DECLARE ieo0 int;
  DECLARE voice0 varchar(100);
  DECLARE wordtype0 varchar(50);
  DECLARE odate0 varchar(50);
  DECLARE cur1 CURSOR FOR SELECT word, entry, ieo, voice, wordtype, odate FROM dictionary.meaning0 order by word0, entry0;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done1 = TRUE;

  OPEN cur1;
  loop1: LOOP
    FETCH cur1 INTO word0, entry0, ieo0, voice0,wordtype0, odate0;
    if done1 then
        close cur1;
        leave loop1;
    end if;
----    loop2: Loop
        block2: begin
            DECLARE word0a varchar(50);
            DECLARE entry0a varchar(80);
            DECLARE done2 INT DEFAULT FALSE;
            DECLARE cur2 CURSOR FOR SELECT word, entry from dictionary.meaning0a where entry = entry0 order by word asc limit 0, 1;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done2 = TRUE; 
            set have1 = 0;
            OPEN cur2;
            loop3: Loop
                FETCH cur2 into word0a, entry0a;
                if done2 then
                    close cur2;
                    leave loop3;
                end if;
                set have1 = have1 + 1;
            end loop loop3;
            if have1 = 0 then
                 insert into meaning0a values(word0, entry0, ieo0, voice0, wordtype0, odate0);
            end if;
        end block2;
----    end loop loop2;
  END LOOP loop1;
END//
delimiter ;
call m0a;
