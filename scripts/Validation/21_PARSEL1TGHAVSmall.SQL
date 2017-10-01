set echo on;

alter session set nls_language = english;
alter session set nls_date_format = 'DD-MON-YYYY';
alter session set nls_date_language = english;



/* SELECT GROUP BY HAVING */
SELECT BUCHUNG.NACH, SUM(BUCHUNG.MEILEN) FROM BUCHUNG
GROUP BY BUCHUNG.NACH HAVING COUNT (*) = 4;


/* SELECT GROUP BY HAVING WHERE */
SELECT BUCHUNG.VON, SUM(BUCHUNG.PREIS) FROM BUCHUNG WHERE (BUCHUNG.BNR > 20000)
GROUP BY BUCHUNG.VON HAVING COUNT (*) > 10;

rollback;
  