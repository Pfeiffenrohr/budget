CREATE TABLE kategorien_new (LIKE kategorien INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES )
INSERT INTO kategorien_new SELECT * FROM kategorien;	

	UPDATE kategorien_new
   SET parent_int = (SELECT id FROM kategorien
                WHERE kategorien.name = kategorien_new.parent)
 WHERE id IN (SELECT id FROM kategorien_new);