ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_RESERVATION FOREIGN KEY (reservation_id) REFERENCES reservations (reservation_id);

ALTER TABLE car
    MODIFY `Condition` LONGTEXT;

ALTER TABLE car
    MODIFY `Condition` LONGTEXT NOT NULL;

ALTER TABLE car
    ALTER `Condition` SET DEFAULT 'Mint';

ALTER TABLE reservations
    MODIFY fee DECIMAL(10, 2);

ALTER TABLE car
    DROP COLUMN fuel;

ALTER TABLE car
    DROP COLUMN rental_status;

ALTER TABLE car
    DROP COLUMN transmission;

ALTER TABLE car
    DROP COLUMN type;

ALTER TABLE car
    ADD fuel LONGTEXT DEFAULT 'Petrol' NOT NULL;

ALTER TABLE car
    ALTER fuel SET DEFAULT 'Petrol';

ALTER TABLE payments
    DROP COLUMN payment_id;

ALTER TABLE payments
    DROP COLUMN status;

ALTER TABLE payments
    ADD payment_id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY;

ALTER TABLE car
    ADD rental_status LONGTEXT DEFAULT 'Available' NOT NULL;

ALTER TABLE car
    ALTER rental_status SET DEFAULT 'Available';

ALTER TABLE reservations
    DROP COLUMN reservation_id;

ALTER TABLE reservations
    DROP COLUMN status;

ALTER TABLE reservations
    ADD reservation_id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY;

ALTER TABLE driver
    DROP COLUMN status;

ALTER TABLE driver
    ADD status LONGTEXT DEFAULT 'Active' NOT NULL;

ALTER TABLE driver
    ALTER status SET DEFAULT 'Active';

ALTER TABLE payments
    ADD status LONGTEXT DEFAULT 'pending' NOT NULL;

ALTER TABLE payments
    ALTER status SET DEFAULT 'pending';

ALTER TABLE reservations
    ADD status VARCHAR(255) DEFAULT 'Pending' NOT NULL;

ALTER TABLE reservations
    ALTER status SET DEFAULT 'Pending';

ALTER TABLE car
    ADD transmission LONGTEXT DEFAULT 'Manual' NOT NULL;

ALTER TABLE car
    ALTER transmission SET DEFAULT 'Manual';

ALTER TABLE car
    ADD type LONGTEXT DEFAULT 'Hatchback' NOT NULL;

ALTER TABLE car
    ALTER type SET DEFAULT 'Hatchback';