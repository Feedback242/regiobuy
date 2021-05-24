package de.uni_marburg.sp21.data_structure;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Random;

import de.uni_marburg.sp21.R;
import de.uni_marburg.sp21.filter.CheckItem;

public enum ShopType implements Serializable {
    PRODUCER,
    SHOP,
    RESTAURANT,
    HOTEL,
    MART;

    private final int[] MART_DRAWABLE_IDS = new int[]{R.drawable.mart0, R.drawable.mart1};
    private final int[] HOTEL_DRAWABLE_IDS = new int[]{R.drawable.hotel0, R.drawable.hotel1};
    private final int[] RESTAURANT_DRAWABLE_IDS = new int[]{R.drawable.restaurant0, R.drawable.restaurant1};
    private final int[] SHOP_DRAWABLE_IDS = new int[]{R.drawable.shop0, R.drawable.shop1};
    private final int[] PRODUCER_DRAWABLE_IDS = new int[]{R.drawable.producer0, R.drawable.producer1};

    public static CheckItem[] createCheckItemArray() {
        CheckItem[] checkItem = new CheckItem[ShopType.values().length];
        for (int i = 0; i < checkItem.length; i++) {
            checkItem[i] = new CheckItem(ShopType.values()[i].toString());
        }
        return checkItem;
    }

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case MART:
                return "Markt";
            case SHOP:
                return "Geschäft";
            case HOTEL:
                return "Hotel";
            case PRODUCER:
                return "Direktproduzent";
            case RESTAURANT:
                return "Restaurant";
            default:
                return "";
        }
    }

    public int toDrawableID() {
        switch (this) {
            case MART:
                return MART_DRAWABLE_IDS[new Random().nextInt(MART_DRAWABLE_IDS.length)];
            case HOTEL:
                return HOTEL_DRAWABLE_IDS[new Random().nextInt(HOTEL_DRAWABLE_IDS.length)];
            case PRODUCER:
                return PRODUCER_DRAWABLE_IDS[new Random().nextInt(PRODUCER_DRAWABLE_IDS.length)];
            case RESTAURANT:
                return RESTAURANT_DRAWABLE_IDS[new Random().nextInt(RESTAURANT_DRAWABLE_IDS.length)];
            default:
                return SHOP_DRAWABLE_IDS[new Random().nextInt(SHOP_DRAWABLE_IDS.length)];
        }
    }

    public static ShopType fromDatabaseString(String s) {
        switch (s) {
            case "producer":
                return PRODUCER;
            case "mart":
                return MART;
            case "restaurant":
                return RESTAURANT;
            case "hotel":
                return HOTEL;
            default:
                return SHOP;
        }
    }
}