package com.uppoint.android.pro.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 */
public final class Scheme {

    private static final Uri BASE_URI = new Uri.Builder().scheme("content").authority(Provider.AUTHORITY).build();

    private Scheme() {
        // deny instantiation
    }

    public interface Entity extends BaseColumns {

        String _KEY = "key";

    }

    private interface SyncedEntity extends Entity {

        String _LAST_UPDATE = "last_update";
        String _IS_DELETED = "is_deleted";

    }

    private interface Nomenclature extends Entity {

        String NAME = "name";

    }

    public interface Country extends Nomenclature {

        String TABLE_NAME = "countries";

        String[] PROJECTION = {_ID, _KEY, NAME};

    }

    public interface City extends Nomenclature {

        String TABLE_NAME = "cities";

        String COUNTRY_KEY = "country_key";

        String[] PROJECTION = {_ID, _KEY, NAME, COUNTRY_KEY};
    }

    public interface Category extends Nomenclature {

        String TABLE_NAME = "categories";

        String[] PROJECTION = {_ID, _KEY, NAME};

    }

    public interface Profession extends Nomenclature {

        String TABLE_NAME = "professions";

        String CATEGORY_KEY = "category_key";

        String[] PROJECTION = {_ID, _KEY, NAME, CATEGORY_KEY};

    }

    public interface ServiceType extends Nomenclature {

        String TABLE_NAME = "service_types";

        String PROFESSION_KEY = "profession_key";

        String[] PROJECTION = {_ID, _KEY, NAME, PROFESSION_KEY};

    }

    public interface User extends SyncedEntity {

        String TABLE_NAME = "users";

        Uri URI = BASE_URI.buildUpon().appendEncodedPath(TABLE_NAME).build();

        String NAME = "name";
        String EMAIL = "email";
        String PHONE_NUMBER = "phone_number";
        String RATING = "rating";

        String ADDRESS_STREET = "address_street";
        String ADDRESS_NUMBER = "address_number";
        String ADDRESS_LATITUDE = "address_latitude";
        String ADDRESS_LONGITUDE = "address_longitude";

        String PROFESSION_KEY = "profession_key";
        String COUNTRY_KEY = "country_key";
        String CITY_KEY = "city_key";

        String[] SYNC_PROJECTION = {_KEY, NAME, EMAIL, PHONE_NUMBER, RATING, ADDRESS_STREET, ADDRESS_NUMBER,
                ADDRESS_LATITUDE, ADDRESS_LONGITUDE, CITY_KEY, COUNTRY_KEY, PROFESSION_KEY};
    }

    public interface UserDefinedService extends SyncedEntity {

        String TABLE_NAME = "user_defined_services";

        Uri URI = BASE_URI.buildUpon().appendEncodedPath(TABLE_NAME).build();

        String NAME = "name";
        String DESCRIPTION = "description";
        String DURATION = "duration";
        String PRICE = "price";

        String USER_KEY = "user_key";
        String SERVICE_TYPE_KEY = "service_type_key";

        String[] SYNC_PROJECTION = {_KEY, NAME, DESCRIPTION, DURATION, PRICE, USER_KEY, SERVICE_TYPE_KEY};

    }

    public interface Event extends SyncedEntity {

        String TABLE_NAME = "events";

        Uri URI = BASE_URI.buildUpon().appendEncodedPath(TABLE_NAME).build();

        String TITLE = "title";
        String DESCRIPTION = "description";
        String START_TIME = "start_time";
        String END_TIME = "end_time";

        String USER_KEY = "user_key";

        String[] PROJECTION = {_ID, _KEY, TITLE, DESCRIPTION, START_TIME, END_TIME};

        String[] SYNC_PROJECTION = {_KEY, TITLE, DESCRIPTION, START_TIME, END_TIME, USER_KEY};
    }
}
