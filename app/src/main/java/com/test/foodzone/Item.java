package com.test.foodzone;

/**
 * Created by Pramod on 11-Oct-15.
 */
public class Item {
    String ItemID;
    String ItemName;

    String getItemID()
    {
        return ItemID;
    }

    String getItemName()
    {
        return ItemName;
    }

    void setItemID(String iID)
    {
        ItemID=iID;
    }

    void setItemName(String Name){
        ItemName=Name;
    }
}
