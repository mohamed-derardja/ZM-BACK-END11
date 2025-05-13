-- Add payment_method column to payments table
ALTER TABLE payments ADD COLUMN payment_method VARCHAR(255);

-- Add payment_method column to reservations table
ALTER TABLE reservations ADD COLUMN payment_method VARCHAR(255);

-- Add card information columns to user table
ALTER TABLE user ADD COLUMN card_number VARCHAR(16);
ALTER TABLE user ADD COLUMN card_expiration VARCHAR(5);
ALTER TABLE user ADD COLUMN card_cvv VARCHAR(3);