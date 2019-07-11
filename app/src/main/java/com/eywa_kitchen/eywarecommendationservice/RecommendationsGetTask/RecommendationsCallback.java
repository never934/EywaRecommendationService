package com.eywa_kitchen.eywarecommendationservice.RecommendationsGetTask;

import java.util.List;

public interface RecommendationsCallback {
    void Received(List<RecommendationDetail> RecommendationsList);
    void Error();
}
