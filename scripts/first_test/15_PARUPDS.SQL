/*
set echo on;

alter session set nls_language = english;
alter session set nls_date_format = 'DD-MON-YYYY';
alter session set nls_date_language = english;
*/

-- ======================================================================= 
--                  BOOKINGS
-- ======================================================================= 

SELECT COUNT(*) FROM BUCHUNG;

/* [[UPDATE ONE TUPLE]] */
--SELECT * FROM BUCHUNG WHERE BNR = 80;
UPDATE BUCHUNG SET TAG = '01-JAN-2014' WHERE BNR = 80;
--SELECT * FROM BUCHUNG WHERE BNR = 80;
SELECT COUNT(*) FROM BUCHUNG;

/* [[UPDATE IN ALL PARTITIONS]] */
UPDATE BUCHUNG SET TAG = '01-FEB-2015' WHERE VON = 'FRA';
SELECT COUNT(*) FROM BUCHUNG;

/* [[UPDATE ALL]] */
UPDATE BUCHUNG SET MEILEN = 1000;
SELECT COUNT(*) FROM BUCHUNG;

SELECT COUNT(*) FROM FLUG;

/* According to 02_CREPARTABS.SQL, it moves from 3rd to 1st partition */
SELECT * FROM FLUG WHERE (FLUG.FNR = 52);
/* [[UPDATE CHANGE OF PARTITION 1500 --> 0700 (3rd to 1st partition)]] */
UPDATE FLUG SET AB = 0700 WHERE FNR = 52;
SELECT * FROM FLUG WHERE (FLUG.FNR = 52);
SELECT COUNT(*) FROM FLUG;

/* According to 02_CREPARTABS.SQL, it moves from 2nd to 3rd partition */
SELECT * FROM FLUG WHERE (FLUG.FNR = 3);
/* [[UPDATE CHANGE OF PARTITION 1310 --> 1910 (2nd to 3rd partition)]] */
UPDATE FLUG SET AB = 1910 WHERE FNR = 3;
SELECT * FROM FLUG WHERE (FLUG.FNR = 3);
SELECT COUNT(*) FROM FLUG;

/* According to 02_CREPARTABS.SQL, it moves from 1st to 2nd partition */
SELECT * FROM FLUG WHERE (FLUG.FNR = 86);
/* [[UPDATE CHANGE OF PARTITION 0 --> 1234 (1st to 2nd partition)]] */
UPDATE FLUG SET AB = 1234 WHERE FNR = 86;
SELECT * FROM FLUG WHERE (FLUG.FNR = 86);
SELECT COUNT(*) FROM FLUG;



rollback;
  