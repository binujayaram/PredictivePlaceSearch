package com.binujayaram.coverassessment.interfaces;

import com.binujayaram.coverassessment.models.Carriers;

public interface Response {
    public void onSuccess(Carriers carriers);
    public void onFaliure(Carriers carriers);
}
