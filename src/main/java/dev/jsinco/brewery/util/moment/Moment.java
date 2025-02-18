package dev.jsinco.brewery.util.moment;

public interface Moment {

    int SECOND = 20;
    int MINUTE = SECOND * 60;
    int AGING_YEAR = MINUTE * 20; //TODO: make this a setting

    long moment();

    default int minutes() {
        return (int) (moment() / MINUTE);
    }

    default int agingYears() {
        return (int) (moment() / AGING_YEAR);
    }
}
