package com.springbootpractice.doclink.Kernel.Util; // Check package name matches your folder

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RatingUtils {

    /**
     * Calculates the average rating safely, ignoring nulls.
     */
    public static double calculateAverageRating(List<Integer> ratings) {
        // 1. Safety check for empty list
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        int count = 0;

        for (Integer rating : ratings) {
            // 2. CRITICAL: Skip nulls!
            // This handles "Comment Only" feedback safely.
            if (rating != null) {
                sum += rating;
                count++;
            }
        }

        // 3. Avoid division by zero if ALL items were null
        if (count == 0) {
            return 0.0;
        }

        double average = sum / count;

        // 4. Round to 1 decimal place
        BigDecimal bd = new BigDecimal(average).setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}