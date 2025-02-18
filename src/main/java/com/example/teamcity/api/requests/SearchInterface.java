package com.example.teamcity.api.requests;

import com.example.teamcity.api.models.BaseModel;

public interface SearchInterface {
    Object search(String searchParameter, String searchValue);
}
