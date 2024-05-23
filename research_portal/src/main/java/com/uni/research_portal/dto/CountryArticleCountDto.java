package com.uni.research_portal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryArticleCountDto {
    private String country;
    private int totalArticles;
    private double averageLatitude;
    private double averageLongitude;

    public CountryArticleCountDto(String country, int totalArticles, double averageLatitude, double averageLongitude) {
        this.country = country;
        this.totalArticles = totalArticles;
        this.averageLatitude = averageLatitude;
        this.averageLongitude = averageLongitude;
    }
}

