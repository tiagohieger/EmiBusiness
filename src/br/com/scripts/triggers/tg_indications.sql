
DROP FUNCTION IF EXISTS tg_indications() CASCADE;

CREATE OR REPLACE FUNCTION tg_indications()
  RETURNS trigger AS
$$
    DECLARE
	rc record;
    BEGIN

        SELECT * INTO rc FROM indications JOIN payment_vouchers ON payment_vouchers.id = indications.payment_voucher
				  JOIN addresses ON addresses.id = indications.address				 
	WHERE indications.id = NEW.id AND indications.client = NEW.client;

	IF rc IS NOT NULL THEN
		INSERT INTO indications_history (document_, name_, person_type, phone, email, status, note, cep, street, 
						 neighborhood, number_, city, state, client, user_, file_)
						
				  VALUES(rc.document_, rc.name_, rc.person_type, rc.phone, rc.email, rc.status, rc.note,
					 rc.cep, rc.street, rc.neighborhood, rc.number_, rc.city, rc.state, rc.client,
					 rc.user_, rc.file_);
                    END IF;	
				
        RETURN NEW;

    END;
$$
  LANGUAGE plpgsql;

CREATE TRIGGER tg_indications
  BEFORE INSERT OR UPDATE
  ON indications FOR EACH ROW
  EXECUTE PROCEDURE tg_indications();