-- ============================================================================
--  Forecast-Genauigkeit historisch auswerten
--  CBudgetBatch 3.0.0
--
--  Zweck
--    Forecast.java loescht vor jedem Lauf die alten Forecast-Zeilen und schreibt
--    anschliessend neu. Der Vorzustand ist damit nicht rekonstruierbar, und
--    transaktion_history enthaelt nur einen Audit-Log ohne Werte. Dieser
--    Snapshot-Mechanismus macht die Prognosequalitaet historisch auswertbar.
--
--  Anwendung
--    Das Schema gehoert der CBudget-Hauptanwendung, dieses Repo fuehrt keine
--    Migrationen. Das File wird daher manuell einmalig angewendet:
--        psql -d <datenbank> -f db/forecast_evaluation.sql
--    Es ist vollstaendig idempotent und kann beliebig oft laufen.
--
--  Encoding: ASCII / UTF-8 (enthaelt bewusst keine Sonderzeichen)
-- ============================================================================


-- ----------------------------------------------------------------------------
--  1) Snapshot: Prognose, wie sie zum Zeitpunkt der Erstellung aussah
-- ----------------------------------------------------------------------------
--  Der Snapshot entsteht waehrend des Forecast-Laufs aus den Tagessummen
--  (ForecastSnapshotWriter) und wird fuer 24 rollierende Monate vorgehalten.
--  Erstellt_tag ist bewusst getrennt von erstellt_am, damit der UNIQUE-Index
--  auf einem DATE stehen kann (date(timestamp) ist in PostgreSQL nicht immutable).

create table if not exists forecast_snapshot (
    id           serial        primary key,
    erstellt_tag date          not null,
    erstellt_am  timestamp     not null,
    kategorie    integer       not null,
    konto        integer       not null,
    monat_tag    date          not null,
    monat_key    char(7)       not null,
    jahr         integer       not null,
    wert         numeric(14,2) not null,
    modell       varchar(32)    not null,
    constraint forecast_snapshot_uq unique (erstellt_tag, kategorie, konto, monat_key)
);

--  Treibt die Auswertung: je Zeitraum/Kategorie/Konto den juengsten Stand
--  suchen, der vor Beginn des prognostizierten Monats erstellt wurde.
create index if not exists forecast_snapshot_auswertung_idx
    on forecast_snapshot (monat_key, kategorie, konto, erstellt_tag desc);

--  Aufraeumen nach Zeitraum (Retention).
create index if not exists forecast_snapshot_horizont_idx
    on forecast_snapshot (monat_key);

--  Referenzen auf kategorien/konten bewusst ohne FOREIGN KEY: die Kaskade
--  waere bei dieser Datenmenge ein Versteck fuer Schreib-Locks. Die
--  Verknuepfung wird stattdessen per JOIN sichergestellt und die
--  Aufraeumroutine entfernt verwaiste Zeilen per NOT EXISTS.

comment on table forecast_snapshot is
    'Monatsprognosen je Kategorie/Konto, Stand zum Zeitpunkt der Berechnung';


-- ----------------------------------------------------------------------------
--  2) Lauf-Metadaten des Snapshots
-- ----------------------------------------------------------------------------
--  Traegt die Modell-/Versionsangabe, nach der die Auswertung filtern soll
--  ("Forecast-Modell/Version" aus der Anforderung).

create table if not exists forecast_snapshot_meta (
    erstellt_tag     date        primary key,
    erstellt_am      timestamp   not null,
    lauf_dauer_ms    bigint,
    compute_weights  boolean,
    algorithmus      varchar(16)
);

comment on table forecast_snapshot_meta is
    'Je Forecast-Lauf ein Satz mit Dauer, computeWeights und Algorithmus-Version';


-- ----------------------------------------------------------------------------
--  3) Auswertungsergebnis je abgeschlossenem Zeitraum
-- ----------------------------------------------------------------------------
--  status: ok            Ist-Wert vorhanden und Forecast nicht nahezu null
--          kein_ist      Zeitraum beendet, aber keine Ist-Daten vorhanden
--          kein_forecast  Forecast nahezu null, Prozentwert waere undefiniert
--
--  Regel aus der Anforderung: fehlende Ist-Werte und nahezu null
--  Forecasts werden markiert und NICHT in die Kennzahlen eingerechnet.
--  Entsprechend sind die Views unten auf status = 'ok' gefiltert.
--
--  Es gibt kein gespeichertes 'kein_snapshot': forecast_wert ist NOT NULL,
--  und ohne Stand vor Zeitraumbeginn existiert kein Wert, den man eintragen
--  koennte. Solche Paare werden im Job nur gezaehlt. Welche Zeitraeume
--  ueberhaupt abgedeckt sind, zeigt v_forecast_abdeckung.

create table if not exists forecast_evaluation (
    id                 serial        primary key,
    monat_key          char(7)       not null,
    jahr               integer       not null,
    monat              integer       not null,
    kategorie          integer       not null,
    konto              integer       not null,
    snapshot_tag       date          not null,
    forecast_wert      numeric(14,2) not null,
    ist_wert           numeric(14,2),
    abweichung         numeric(14,2),
    abweichung_prozent numeric(8,2),
    status             varchar(16)   not null,
    berechnet_am       timestamp     not null,
    constraint forecast_evaluation_uq unique (monat_key, kategorie, konto)
);

create index if not exists forecast_evaluation_kennzahlen_idx
    on forecast_evaluation (jahr, status);

create index if not exists forecast_evaluation_zeitraum_idx
    on forecast_evaluation (monat_key);

comment on table forecast_evaluation is
    'Soll-Ist-Vergleich je Monat/Kategorie/Konto auf Basis des historisch korrekten Standes';


-- ----------------------------------------------------------------------------
--  4) Konfiguration in der vorhandenen settings-Tabelle
-- ----------------------------------------------------------------------------
--  forecast_eval_toleranz      Trefferquote-Grenze in Prozent (Standard 10)
--  forecast_eval_horizont      Monate, die je Lauf im Snapshot gehalten werden
--  forecast_eval_algorithmus   Modell-/Versionskennung im Snapshot
--  forecast_eval_retention_jahre  Aufbewahrung der Snapshots in Jahren
--  forecast_eval_monate        Monate, die je Lauf ausgewertet werden

insert into settings (parameter, wert) select 'forecast_eval_toleranz', '10.0'
    where not exists (select 1 from settings where parameter = 'forecast_eval_toleranz');

insert into settings (parameter, wert) select 'forecast_eval_horizont', '24'
    where not exists (select 1 from settings where parameter = 'forecast_eval_horizont');

insert into settings (parameter, wert) select 'forecast_eval_algorithmus', 'v1'
    where not exists (select 1 from settings where parameter = 'forecast_eval_algorithmus');

insert into settings (parameter, wert) select 'forecast_eval_retention_jahre', '3'
    where not exists (select 1 from settings where parameter = 'forecast_eval_retention_jahre');

insert into settings (parameter, wert) select 'forecast_eval_monate', '6'
    where not exists (select 1 from settings where parameter = 'forecast_eval_monate');


-- ----------------------------------------------------------------------------
--  5) Sichten fuer die Auswertung
-- ----------------------------------------------------------------------------
--  Die Auswertung liest ausschliesslich ueber diese Views. Sie sind bewusst
--  materialisierungsfrei: die Datenmenge eines Haushalts ist klein genug,
--  dass Postgres die Kennzahlen zur Abfragezeit schneller bildet als eine
--  zweite Tabelle, die mit dem Snapshot-Lauf synchron gehalten werden muss.

--  5.1 Detailtabelle: Zeitraum, damaliger Forecast, Ist, Differenz
create or replace view v_forecast_detail as
select
    e.id,
    e.jahr,
    e.monat,
    e.monat_key,
    e.kategorie,
    k.name                          as kategorie_name,
    e.konto,
    ko.kontoname                    as konto_name,
    e.snapshot_tag,
    e.forecast_wert,
    e.ist_wert,
    e.abweichung,
    e.abweichung_prozent,
    e.status,
    e.berechnet_am
from forecast_evaluation e
    left join kategorien k  on k.id  = e.kategorie
    left join konten     ko on ko.id = e.konto;

comment on view v_forecast_detail is
    'Detailtabelle Soll-Ist je Monat, Kategorie und Konto';

--  5.2 Zeitreihe: Forecast gegen Ist pro Monat
create or replace view v_forecast_zeitreihe as
select
    e.jahr,
    e.monat,
    e.monat_key,
    e.kategorie,
    k.name          as kategorie_name,
    e.konto,
    ko.kontoname    as konto_name,
    e.forecast_wert,
    e.ist_wert,
    e.abweichung,
    e.abweichung_prozent,
    e.status
from forecast_evaluation e
    left join kategorien k  on k.id  = e.kategorie
    left join konten     ko on ko.id = e.konto
where e.status = 'ok'
order by e.jahr, e.monat, e.kategorie, e.konto;

comment on view v_forecast_zeitreihe is
    'Nur auswertbare Zeitraeume, fuer das Diagramm Forecast gegen Ist';

--  5.3 Uebersichtskarten: mittlere Abweichung, Trefferquote, Bias
--      Toleranz kommt aus settings, damit sie ohne Schema-Aenderung
--      justierbar bleibt.
create or replace view v_forecast_kennzahlen as
select
    e.jahr,
    e.kategorie,
    k.name       as kategorie_name,
    e.konto,
    ko.kontoname as konto_name,
    count(*)                                                    as anzahl_zeilen,
    count(*) filter (where e.status = 'ok')                    as anzahl_ausgewertet,
    count(*) filter (where e.status = 'kein_ist')              as anzahl_ohne_ist,
    count(*) filter (where e.status = 'kein_forecast')          as anzahl_ohne_forecastwert,
    avg(e.abweichung) filter (where e.status = 'ok')            as mittel_abweichung,
    avg(abs(e.abweichung)) FILTER (where e.status = 'ok')       as mittel_abs_abweichung,
    avg(e.abweichung_prozent) FILTER (where e.status = 'ok')    as bias_prozent,
    avg(abs(e.abweichung_prozent)) FILTER (where e.status = 'ok') as mittel_abs_prozent,
    100.0 * count(*) filter (
                where e.status = 'ok'
                  and abs(e.abweichung_prozent) <= coalesce(
                          (select wert::numeric from settings where parameter = 'forecast_eval_toleranz'),
                          10.0)
            ) / nullif(count(*) filter (where e.status = 'ok'), 0) as trefferquote,
    max(e.abweichung) filter (where e.status = 'ok')            as staerkste_ueberschaetzung,
    min(e.abweichung) filter (where e.status = 'ok')            as staerkste_unterschaetzung
from forecast_evaluation e
    left join kategorien k  on k.id  = e.kategorie
    left join konten     ko on ko.id = e.konto
group by e.jahr, e.kategorie, k.name, e.konto, ko.kontoname;

comment on view v_forecast_kennzahlen is
    'Kennzahlen je Jahr/Kategorie/Konto; nur status = ok wird eingerechnet';

--  5.4 Heatmap-Rohdaten: Monat gegen Kategorie
--      Long-Format, das Pivoting uebernimmt der Client.
create or replace view v_forecast_heatmap as
select
    e.jahr,
    e.monat,
    e.monat_key,
    e.kategorie,
    k.name                as kategorie_name,
    count(*)                                            as anzahl,
    avg(e.abweichung)         filter (where e.status = 'ok') as mittel_abweichung,
    avg(e.abweichung_prozent) filter (where e.status = 'ok') as mittel_prozent,
    min(e.abweichung)         filter (where e.status = 'ok') as min_abweichung,
    max(e.abweichung)         filter (where e.status = 'ok') as max_abweichung
from forecast_evaluation e
    left join kategorien k on k.id = e.kategorie
group by e.jahr, e.monat, e.monat_key, e.kategorie, k.name;

comment on view v_forecast_heatmap is
    'Monat x Kategorie, Long-Format fuer eine Heatmap';

--  5.5 Staerkste Ueber- und Unterschaetzungen ueber alle Filter hinweg
create or replace view v_forecast_abweichung as
select
    e.jahr,
    e.monat_key,
    e.kategorie,
    k.name       as kategorie_name,
    e.konto,
    ko.kontoname as konto_name,
    e.forecast_wert,
    e.ist_wert,
    e.abweichung,
    e.abweichung_prozent
from forecast_evaluation e
    left join kategorien k  on k.id  = e.kategorie
    left join konten     ko on ko.id = e.konto
where e.status = 'ok'
order by abs(e.abweichung) desc nulls last;

comment on view v_forecast_abweichung is
    'Alle auswertbaren Zeitraeume nach Groesse der Abweichung sortiert';

--  5.6 Verfuegbarkeit: wie viele Snapshots existieren je Zeitraum ueberhaupt
--      (erlaubt, Datenluecken vor dem ersten Lauf von echten Fehlern zu trennen)
create or replace view v_forecast_abdeckung as
select
    monat_key,
    min(erstellt_tag)                                   as erster_snapshot,
    max(erstellt_tag)                                   as letzter_snapshot,
    count(distinct erstellt_tag)                        as anzahl_staende,
    count(*)                                            as anzahl_zeilen
from forecast_snapshot
group by monat_key
order by monat_key;

comment on view v_forecast_abdeckung is
    'Welche Zeitraeume ueberhaupt historisch abgedeckt sind';