package com.test.foodzone;

/**
 * Created by Pramod on 05-Mar-16.
 */
public class LocationDetails {
    String LocationID;
    String LocationName;
    //String ItemCheckedStatus;

    void setItemID(String ItemID)
    {
        this.LocationID=ItemID;
    }

    void setItemName(String LocationName)
    {
        this.LocationName=LocationName;
    }


    String getLocationID()
    {
        return LocationID;
    }

    String getLocationName()
    {
        return LocationName;
    }

}
