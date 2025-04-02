package dev.jsinco.brewery.util.random;

import java.util.List;
import java.util.Random;

public class RandomUtil {
    private static final Random RANDOM = new Random();

    public static final <T extends WeightedProbabilityElement> T randomWeighted(List<T> tList) {
        int[] cumulativeSums = new int[tList.size()];
        int cumulativeSum = 0;
        for (int i = 0; i < tList.size(); i++) {
            T drunkEvent = tList.get(i);
            cumulativeSum += drunkEvent.getProbabilityWeight();
            cumulativeSums[i] = cumulativeSum;
        }
        int randomInt = RANDOM.nextInt(cumulativeSum);
        for (int i = 0; i < tList.size(); i++) {
            if (cumulativeSums[i] > randomInt) {
                return tList.get(i);
            }
        }
        return tList.getLast();
    }

}
