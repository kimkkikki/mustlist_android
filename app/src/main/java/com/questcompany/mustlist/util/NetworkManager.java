package com.questcompany.mustlist.util;

import com.questcompany.mustlist.entity.Must;
import com.questcompany.mustlist.entity.PreviewResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * TODO:Network 통신 처리 추가 필요함
 */

public class NetworkManager {

    public static List<Must> getMustList() {
        List<Must> mustList = new ArrayList<>();

        return mustList;
    }

    public static PreviewResult previewAddMust(String startDay, String period, String amount, String timeRange) {
        PreviewResult previewResult = new PreviewResult();

        //TODO: 서버 API를 호출하도록 변경 필요
        {
            previewResult.setStartDay(startDay);
            previewResult.setPeriod(period);
            previewResult.setAmount(amount);
            previewResult.setTimeRange(timeRange);

            previewResult.setDefaultPoint(100);
            previewResult.setSuccessPoint(100);
        }

        return previewResult;
    }
}
