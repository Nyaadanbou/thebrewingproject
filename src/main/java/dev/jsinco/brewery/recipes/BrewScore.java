package dev.jsinco.brewery.recipes;

import org.jetbrains.annotations.Nullable;

public record BrewScore(double ingredientScore, double cauldronTimeScore, double distillRunsScore,
                        double agingTimeScore, double cauldronTypeScore, double barrelTypeScore, int brewDifficulty) {

    public static final BrewScore EXCELLENT = new BrewScore(1, 1, 1, 1, 1, 1, 1);
    public static final BrewScore NONE = new BrewScore(0, 0, 0, 0, 0, 0, 1);
    private static final char FULL_STAR = '\u2605';
    private static final char HALF_STAR = '\u2BEA';
    private static final char EMPTY_STAR = '\u2606';

    public @Nullable BrewQuality brewQuality() {
        return quality(score());
    }

    private double score() {
        // Avoid extreme point, log(0) is minus infinity
        if (brewDifficulty == 0) {
            return 1D;
        }
        double score = rawScore();
        double scoreWithDifficulty;
        // Avoid extreme point, can not divide by log(1) as it's 0
        if (brewDifficulty == 1) {
            scoreWithDifficulty = score;
        } else {
            double logBrewDifficulty = Math.log(brewDifficulty);
            scoreWithDifficulty = (Math.exp(score * logBrewDifficulty) / Math.exp(logBrewDifficulty) - 1D / brewDifficulty) / (1 - 1D / brewDifficulty);
        }
        return Math.max(scoreWithDifficulty - 0.3, 0.0) * 1 / 0.7;
    }

    public String displayName() {
        StringBuilder builder = new StringBuilder();
        int score = (int) (score() * 10);
        int fullStars = score / 2;
        int remainder = score % 2;
        builder.repeat(FULL_STAR, fullStars);
        if (remainder == 1) {
            builder.append(HALF_STAR);
            builder.repeat(EMPTY_STAR, 4 - fullStars);
        } else {
            builder.repeat(EMPTY_STAR, 5 - fullStars);
        }
        return builder.toString();
    }

    public double rawScore() {
        return ingredientScore * cauldronTimeScore * distillRunsScore * agingTimeScore * cauldronTypeScore * barrelTypeScore;
    }

    public static BrewQuality quality(double score) {
        if (score >= 0.8) {
            return BrewQuality.EXCELLENT;
        }
        if (score >= 0.6) {
            return BrewQuality.GOOD;
        }
        if (score > 0) {
            return BrewQuality.BAD;
        }
        return null;
    }
}
