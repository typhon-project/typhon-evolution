import {AnalyticsModel} from "../../models/analytics.model";

export class AnalyticsApiMapper {

    public static mapJsonObjectToAnalytics = (jsonObject: any): AnalyticsModel => {
        let analytics: AnalyticsModel;
        if (jsonObject) {
            analytics = new AnalyticsModel(+jsonObject.id, jsonObject.query);
        }
        return analytics;
    };

    public static mapJsonArrayToAnalytics = (jsonArray: any[]): AnalyticsModel[] => {
        let analytics: AnalyticsModel[] = [];
        if (jsonArray) {
            for (let jsonObject of jsonArray) {
                analytics.push(AnalyticsApiMapper.mapJsonObjectToAnalytics(jsonObject));
            }
        }
        return analytics;
    };
}
