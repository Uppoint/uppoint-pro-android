package com.uppoint.android.pro.db;

import android.provider.BaseColumns;

/**
 */
public final class Scheme {

    private Scheme() {
        // deny instantiation
    }

    private interface Entity extends BaseColumns {

        String _KEY = "key";

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

    public interface User extends Entity {

        String TABLE_NAME = "users";

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
    }

    public interface UserDefinedService extends Entity {

        String TABLE_NAME = "user_defined_services";

        String NAME = "name";
        String DESCRIPTION = "description";
        String DURATION = "duration";
        String PRICE = "price";

        String USER_KEY = "user_key";
        String SERVICE_TYPE_KEY = "service_type_key";

    }
}
