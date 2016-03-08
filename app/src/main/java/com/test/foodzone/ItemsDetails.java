package com.test.foodzone;

/**
 * Created by Pramod on 20-Oct-15.
 */
public class ItemsDetails {
    String ItemID;
    String ItemName;
    String ItemCheckedStatus;

    void setItemID(String ItemID)
    {
        this.ItemID=ItemID;
    }

    void setItemName(String ItemName)
    {
        this.ItemName=ItemName;
    }

    void setItemCheckedStatus(String Status){
        this.ItemCheckedStatus=Status;
    }

    String getItemID()
    {
        return ItemID;
    }

    String getItemName()
    {
        return ItemName;
    }

    String getItemCheckedStatus(){
        return ItemCheckedStatus;
    }
}
