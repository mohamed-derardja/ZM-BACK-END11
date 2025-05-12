package com.zm.zmbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Configuration
public class CancellationConfig {

    @Value("${reservation.cancellation.fee.default:0.10}")
    private BigDecimal defaultCancellationFeePercentage;

    @Value("${reservation.cancellation.fee.lastMinute:0.50}")
    private BigDecimal lastMinuteCancellationFeePercentage;

    @Value("${reservation.cancellation.fee.sameDay:0.25}")
    private BigDecimal sameDayCancellationFeePercentage;

    @Value("${reservation.cancellation.fee.lastMinuteHours:24}")
    private int lastMinuteHours;

    @Value("${reservation.cancellation.fee.sameDayHours:72}")
    private int sameDayHours;

    /**
     * Calculate the cancellation fee percentage based on how close to the reservation start time
     * the cancellation is occurring.
     *
     * @param startDate The start date of the reservation
     * @param cancellationTime The time when the cancellation is requested
     * @return The cancellation fee percentage as a BigDecimal
     */
    public BigDecimal calculateCancellationFeePercentage(Instant startDate, Instant cancellationTime) {
        // If the reservation has already started, it cannot be cancelled
        if (startDate.isBefore(cancellationTime)) {
            throw new RuntimeException("Cannot cancel a reservation that has already started or ended");
        }

        // Calculate the duration between cancellation time and reservation start time
        Duration timeUntilReservation = Duration.between(cancellationTime, startDate);
        long hoursUntilReservation = timeUntilReservation.toHours();

        // Apply fee based on how close to the start time
        if (hoursUntilReservation <= lastMinuteHours) {
            return lastMinuteCancellationFeePercentage;
        } else if (hoursUntilReservation <= sameDayHours) {
            return sameDayCancellationFeePercentage;
        } else {
            return defaultCancellationFeePercentage;
        }
    }

    public BigDecimal getDefaultCancellationFeePercentage() {
        return defaultCancellationFeePercentage;
    }

    public BigDecimal getLastMinuteCancellationFeePercentage() {
        return lastMinuteCancellationFeePercentage;
    }

    public BigDecimal getSameDayCancellationFeePercentage() {
        return sameDayCancellationFeePercentage;
    }

    public int getLastMinuteHours() {
        return lastMinuteHours;
    }

    public int getSameDayHours() {
        return sameDayHours;
    }
}