CREATE DATABASE zm_data_base;
CREATE TABLE `Car`(
    `Car_ID` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `License_plate` VARCHAR(255) NOT NULL,
    `Description` TEXT NOT NULL,
    `Picture` VARCHAR(255) NOT NULL,
    `Brand` VARCHAR(255) NOT NULL,
    `Condition` ENUM('Mint', 'Great', 'Good', 'Fair', 'Poor') NOT NULL DEFAULT 'Mint',
    `Model` VARCHAR(255) NOT NULL,
    `Mileage` INT UNSIGNED NOT NULL DEFAULT '0',
    `Type` ENUM(
        'Hatchback',
        'SUV',
        'Sports-car',
        'Hybrid',
        'Pickup',
        'Sedan',
        'Minivan',
        'Station-Wagon',
        'Convertible',
        'Pony-car',
        'Luxury-car',
        'Crossover',
        'Coupe',
        'Microcar',
        'Roadster',
        'Supercar',
        'Van',
        'Limousine',
        'Compact',
        'Off-road'
    ) NOT NULL DEFAULT 'Hatchback',
    `Year` INT UNSIGNED NOT NULL,
    `Colour` VARCHAR(255) NOT NULL,
    `Transmission` ENUM('Manual', 'Automatic') NOT NULL DEFAULT 'Manual',
    `Fuel` ENUM('Petrol', 'Diesel', 'Gas') NOT NULL DEFAULT 'Petrol',
    `Seating_capacity` INT UNSIGNED NOT NULL DEFAULT '4',
    `Rental_price_per_day` DECIMAL(8, 2) NOT NULL,
    `Rental_price_per_hour` DECIMAL(8, 2) NULL,
    `Rental_status` ENUM(
        'Available',
        'Not-available',
        'In-maintenance',
        'Reserved',
        'In-service',
        'In-repair', 
        'In-cleaning'
    ) NOT NULL DEFAULT 'Available',
    `Current_location` VARCHAR(255) NOT NULL,
    `Last_service_date` DATE NULL,
    `Next_service_date` DATE NOT NULL,
    `Insurance_expiry_date` DATE NOT NULL,
    `GPS_enabled` BOOLEAN NOT NULL DEFAULT '1',
    `Rating` INT UNSIGNED NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL
);
ALTER TABLE
    `Car` ADD UNIQUE `car_license_plate_unique`(`License_plate`);
ALTER TABLE `Car` ADD INDEX `car_license_plate_index`(`License_plate`);
ALTER TABLE `Car` ADD INDEX `car_rental_status_index`(`Rental_status`);
CREATE TABLE Favourites (
    User_ID BIGINT NOT NULL,
    Car_ID BIGINT NOT NULL,
    Created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (User_ID, Car_ID),
    FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
    FOREIGN KEY (Car_ID) REFERENCES Car(Car_ID) ON DELETE CASCADE
);
CREATE TABLE `Driver`(
    `Driver_ID` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Picture` VARCHAR(255) NOT NULL,
    `First_name` VARCHAR(255) NOT NULL,
    `Last_name` VARCHAR(255) NOT NULL,
    `Birthday` DATE NOT NULL,
    `Phone_number` VARCHAR(255) NOT NULL,
    `Address` TEXT NOT NULL,
    `Email` VARCHAR(255) NOT NULL,
    `Daily_wage` DECIMAL(8, 2) NOT NULL DEFAULT '1428',
    `Hourly_wage` DECIMAL(8, 2) NOT NULL DEFAULT '178',
    `Availability` BOOLEAN NOT NULL DEFAULT '1',
    `Status` ENUM(
        'Active',
        'Not-active',
        'In-vacation',
        'Retired',
        'Fired',
        'Quit',
        'Died',
        'Uknown'
    ) NOT NULL DEFAULT 'Active',
    `Years_of_experience` INT UNSIGNED NOT NULL DEFAULT '2',
    `Car_ID` BIGINT NOT NULL,
    `Rating` INT UNSIGNED NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL
);
ALTER TABLE
    `Driver` ADD UNIQUE `driver_phone_number_unique`(`Phone_number`);
ALTER TABLE
    `Driver` ADD UNIQUE `driver_email_unique`(`Email`);
ALTER TABLE
    `Driver` ADD INDEX `driver_availability_index`(`Availability`);
CREATE TABLE `User`(
    `User_ID` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Picture` VARCHAR(255) NULL,
    `First_name` VARCHAR(255) NOT NULL,
    `Last_name` VARCHAR(255) NOT NULL,
    `Birthday` DATE NOT NULL,
    `Phone_number` VARCHAR(255) NOT NULL,
    `Address` varchar(255) NOT NULL,
    `Email` VARCHAR(255) NOT NULL,
    `Password` VARCHAR(255) NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL
);
ALTER TABLE
    `User` ADD UNIQUE `user_phone_number_unique`(`Phone_number`);
ALTER TABLE
    `User` ADD UNIQUE `user_email_unique`(`Email`);
CREATE TABLE `Gallery`(
    `Gallery_ID` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Car_ID` BIGINT NOT NULL,
    `Image` VARCHAR(255) NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL
);
ALTER TABLE
    `Gallery` ADD UNIQUE `gallery_car_id_image_unique`(`Car_ID`, `Image`);
ALTER TABLE
    `Gallery` ADD INDEX `gallery_car_id_index`(`Car_ID`);
CREATE TABLE `Reservations`(
    `Reservation_ID` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Start_date` DATETIME NOT NULL,
    `End_date` DATETIME NOT NULL,
    `Car_ID` BIGINT NOT NULL,
    `User_ID` BIGINT NOT NULL,
    `Driver_ID` BIGINT NOT NULL,
    `Self_drive` BOOLEAN NOT NULL,
    `Status` ENUM(
        'pending',
        'approved',
        'rejected',
        'completed'
    ) NOT NULL DEFAULT 'pending',
    `Fee` DECIMAL(8, 2) NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL
);
CREATE TABLE `Payments`(
    `Payment_ID` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Reservation_ID` BIGINT NOT NULL,
    `User_ID` BIGINT NOT NULL,
    `amount` DECIMAL(8, 2) NOT NULL,
    `Payment_date` DATETIME NOT NULL,
    `Status` ENUM('paid', 'pending', 'failed') NOT NULL DEFAULT 'pending',
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL
);
ALTER TABLE
    `Payments` ADD CONSTRAINT `payments_user_id_foreign` FOREIGN KEY(`User_ID`) REFERENCES `User`(`User_ID`);
ALTER TABLE
    `Driver` ADD CONSTRAINT `driver_car_id_foreign` FOREIGN KEY(`Car_ID`) REFERENCES `Car`(`Car_ID`);
ALTER TABLE
    `Reservations` ADD CONSTRAINT `reservations_driver_id_foreign` FOREIGN KEY(`Driver_ID`) REFERENCES `Driver`(`Driver_ID`);
ALTER TABLE
    `Reservations` ADD CONSTRAINT `reservations_car_id_foreign` FOREIGN KEY(`Car_ID`) REFERENCES `Car`(`Car_ID`);
ALTER TABLE
    `Reservations` ADD CONSTRAINT `reservations_user_id_foreign` FOREIGN KEY(`User_ID`) REFERENCES `User`(`User_ID`);
ALTER TABLE
    `Payments` ADD CONSTRAINT `payments_reservation_id_foreign` FOREIGN KEY(`Reservation_ID`) REFERENCES `Reservations`(`Reservation_ID`);
ALTER TABLE 
    `Gallery` ADD CONSTRAINT `gallery_car_id_foreign` FOREIGN KEY(`Car_ID`) REFERENCES `Car`(`Car_ID`);    
ALTER TABLE `Reservations`
ADD CONSTRAINT `check_reservation_dates`
CHECK (`End_date` > `Start_date`);
ALTER TABLE `Car` ADD CONSTRAINT `check_car_rating` CHECK (`Rating` BETWEEN 1 AND 5);
ALTER TABLE `Driver` ADD CONSTRAINT `check_driver_rating` CHECK (`Rating` BETWEEN 1 AND 5);
ALTER TABLE `Car` ADD CONSTRAINT `check_car_year` CHECK (`Year` BETWEEN 2000 AND CURRENT_YEAR());
ALTER TABLE `Driver` ADD CONSTRAINT `check_driver_years_of_experience` CHECK (`Years_of_experience` BETWEEN 2 AND 50);
ALTER TABLE `User` ADD CONSTRAINT `check_user_birthday` CHECK (`Birthday` < DATE_SUB(CURRENT_DATE(), INTERVAL 18 YEAR));
ALTER TABLE `Driver` ADD CONSTRAINT `check_driver_birthday` CHECK (`Birthday` < DATE_SUB(CURRENT_DATE(), INTERVAL 18 YEAR));
ALTER TABLE `Car` ADD CONSTRAINT `check_car_year` CHECK (`Year` BETWEEN 2000 AND CURRENT_YEAR());
ALTER TABLE `Driver` ADD CONSTRAINT `check_driver_years_of_experience` CHECK (`Years_of_experience` BETWEEN 2 AND 50);
ALTER TABLE `Car` ADD CONSTRAINT `check_car_seating_capacity` CHECK (`Seating_capacity` BETWEEN 2 AND 9);

DELIMITER $$

CREATE TRIGGER `update_car_timestamp`
BEFORE UPDATE ON `Car`
FOR EACH ROW
BEGIN
    SET NEW.`Updated_at` = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER `update_driver_timestamp`
BEFORE UPDATE ON `Driver`
FOR EACH ROW
BEGIN
    SET NEW.`Updated_at` = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER `update_user_timestamp`
BEFORE UPDATE ON `User`
FOR EACH ROW
BEGIN
    SET NEW.`Updated_at` = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER `update_reservation_timestamp`
BEFORE UPDATE ON `Reservations`
FOR EACH ROW
BEGIN
    SET NEW.`Updated_at` = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER `update_payment_timestamp`
BEFORE UPDATE ON `Payments`
FOR EACH ROW
BEGIN
    SET NEW.`Updated_at` = CURRENT_TIMESTAMP;
END$$

CREATE PROCEDURE `mark_completed_reservations`()
BEGIN
    UPDATE `Reservations`
    SET `Status` = 'completed'
    WHERE `End_date` < NOW() AND `Status` = 'approved';
END$$

DELIMITER ;
DELIMITER $$

CREATE PROCEDURE `update_car_condition_based_on_mileage`()
BEGIN
    UPDATE `Car`
    SET `Condition` = CASE
        WHEN `Mileage` < 20000 THEN 'Mint'
        WHEN `Mileage` BETWEEN 20000 AND 50000 THEN 'Great'
        WHEN `Mileage` BETWEEN 50001 AND 100000 THEN 'Good'
        WHEN `Mileage` BETWEEN 100001 AND 150000 THEN 'Fair'
        ELSE 'Poor'
    END;
END$$

DELIMITER ;

CALL update_car_condition_based_on_mileage();
