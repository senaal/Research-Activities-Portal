package com.uni.research_portal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryStatistics {
    private int totalArticles = 0;
    private double sumLatitude = 0;
    private double sumLongitude = 0;
    private int institutionCount = 0;

    public void addInstitution(double latitude, double longitude, long articles) {
        this.sumLatitude += latitude;
        this.sumLongitude += longitude;
        this.totalArticles += articles;
        this.institutionCount++;
    }

    public double getAverageLatitude() {
        return sumLatitude / institutionCount;
    }

    public double getAverageLongitude() {
        return sumLongitude / institutionCount;
    }
}
