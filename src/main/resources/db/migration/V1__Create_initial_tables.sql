CREATE TABLE `user` (
    `User_ID` BIGINT NOT NULL,
    `Picture` VARCHAR(255),
    `First_name` VARCHAR(255) NOT NULL,
    `Last_name` VARCHAR(255) NOT NULL,
    `Birthday` DATE NOT NULL,
    `Phone_number` VARCHAR(255) NOT NULL,
    `Address` TEXT NOT NULL,
    `Email` VARCHAR(255) NOT NULL,
    `Password` VARCHAR(255) NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL,
    PRIMARY KEY (`User_ID`)
);

CREATE TABLE `car` (
    `Car_ID` BIGINT NOT NULL,
    `License_plate` VARCHAR(255) NOT NULL,
    `Description` TEXT NOT NULL,
    `Picture` VARCHAR(255) NOT NULL,
    `Brand` VARCHAR(255) NOT NULL,
    `Condition` TEXT NOT NULL DEFAULT 'Mint',
    `Model` VARCHAR(255) NOT NULL,
    `Mileage` INT UNSIGNED NOT NULL DEFAULT 0,
    `Type` TEXT NOT NULL DEFAULT 'Hatchback',
    `Year` INT UNSIGNED NOT NULL,
    `Colour` VARCHAR(255) NOT NULL,
    `Transmission` TEXT NOT NULL DEFAULT 'Manual',
    `Fuel` TEXT NOT NULL DEFAULT 'Petrol',
    `Seating_capacity` INT UNSIGNED NOT NULL DEFAULT 4,
    `Rental_price_per_day` DECIMAL(8,2) NOT NULL,
    `Rental_price_per_hour` DECIMAL(8,2),
    `Rental_status` TEXT NOT NULL DEFAULT 'Available',
    `Current_location` VARCHAR(255) NOT NULL,
    `Last_service_date` DATE,
    `Next_service_date` DATE NOT NULL,
    `Insurance_expiry_date` DATE NOT NULL,
    `GPS_enabled` BOOLEAN NOT NULL DEFAULT TRUE,
    `Rating` INT UNSIGNED NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL,
    PRIMARY KEY (`Car_ID`)
);

CREATE TABLE `driver` (
    `Driver_ID` BIGINT NOT NULL,
    `Picture` VARCHAR(255) NOT NULL,
    `First_name` VARCHAR(255) NOT NULL,
    `Last_name` VARCHAR(255) NOT NULL,
    `Birthday` DATE NOT NULL,
    `Phone_number` VARCHAR(255) NOT NULL,
    `Address` TEXT NOT NULL,
    `Email` VARCHAR(255) NOT NULL,
    `Daily_wage` DECIMAL(8,2) NOT NULL DEFAULT 1428.00,
    `Hourly_wage` DECIMAL(8,2) NOT NULL DEFAULT 178.00,
    `Availability` BOOLEAN NOT NULL DEFAULT TRUE,
    `Status` TEXT NOT NULL DEFAULT 'Active',
    `Years_of_experience` INT UNSIGNED NOT NULL DEFAULT 2,
    `Car_ID` BIGINT NOT NULL,
    `Rating` INT UNSIGNED NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL,
    PRIMARY KEY (`Driver_ID`),
    FOREIGN KEY (`Car_ID`) REFERENCES `car` (`Car_ID`)
);

CREATE TABLE `favourites` (
    `User_ID` BIGINT NOT NULL,
    `Car_ID` BIGINT NOT NULL,
    `Created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`User_ID`, `Car_ID`),
    FOREIGN KEY (`User_ID`) REFERENCES `user` (`User_ID`) ON DELETE CASCADE,
    FOREIGN KEY (`Car_ID`) REFERENCES `car` (`Car_ID`) ON DELETE CASCADE
);

CREATE TABLE `reservations` (
    `Reservation_ID` BIGINT NOT NULL,
    `Start_date` TIMESTAMP NOT NULL,
    `End_date` TIMESTAMP NOT NULL,
    `Car_ID` BIGINT NOT NULL,
    `User_ID` BIGINT NOT NULL,
    `Driver_ID` BIGINT NOT NULL,
    `Self_drive` BOOLEAN NOT NULL DEFAULT FALSE,
    `Status` TEXT NOT NULL DEFAULT 'pending',
    `Fee` DECIMAL(8,2) NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL,
    PRIMARY KEY (`Reservation_ID`),
    FOREIGN KEY (`Car_ID`) REFERENCES `car` (`Car_ID`),
    FOREIGN KEY (`User_ID`) REFERENCES `user` (`User_ID`),
    FOREIGN KEY (`Driver_ID`) REFERENCES `driver` (`Driver_ID`)
);

CREATE TABLE `gallery` (
    `Gallery_ID` BIGINT NOT NULL,
    `Car_ID` BIGINT NOT NULL,
    `Image` VARCHAR(255) NOT NULL,
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL,
    PRIMARY KEY (`Gallery_ID`),
    FOREIGN KEY (`Car_ID`) REFERENCES `car` (`Car_ID`)
);

CREATE TABLE `payments` (
    `Payment_ID` BIGINT NOT NULL,
    `Reservation_ID` BIGINT NOT NULL,
    `User_ID` BIGINT NOT NULL,
    `amount` DECIMAL(8,2) NOT NULL,
    `Payment_date` TIMESTAMP NOT NULL,
    `Status` TEXT NOT NULL DEFAULT 'pending',
    `Created_at` TIMESTAMP NOT NULL,
    `Updated_at` TIMESTAMP NOT NULL,
    PRIMARY KEY (`Payment_ID`),
    FOREIGN KEY (`Reservation_ID`) REFERENCES `reservations` (`Reservation_ID`),
    FOREIGN KEY (`User_ID`) REFERENCES `user` (`User_ID`)
);