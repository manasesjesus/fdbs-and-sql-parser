set echo on;

alter session set nls_language = english;
alter session set nls_date_format = 'DD-MON-YYYY';
alter session set nls_date_language = english;


/* SELECT COUNT(*) */
SELECT COUNT(*) FROM FLUGHAFEN;


/* SELECT COUNT(*) GROUP BY */
SELECT FLUG.FLC, COUNT(*) FROM FLUG GROUP BY FLUG.FLC;


/* SELECT SUM GROUP BY */
SELECT FLUG.FLC, SUM(FLUG.FNR) FROM FLUG GROUP BY FLUG.FLC;


/* SELECT GROUP BY */
SELECT FLUGLINIE.LAND, COUNT(*) FROM FLUGLINIE GROUP BY FLUGLINIE.LAND;



rollback;
  