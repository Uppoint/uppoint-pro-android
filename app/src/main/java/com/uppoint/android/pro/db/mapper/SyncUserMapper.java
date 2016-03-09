package com.uppoint.android.pro.db.mapper;

import com.appspot.uppoint_api.uppointApi.model.AddressPayload;
import com.appspot.uppoint_api.uppointApi.model.ProUserPayload;
import com.uppoint.android.pro.db.Scheme;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import bg.dalexiev.bender.mapper.RowMapper;

/**
 */
public class SyncUserMapper implements RowMapper<ProUserPayload> {

    @Nullable
    @Override
    public ProUserPayload toObject(@NonNull Cursor cursor, int rowNum) {
        final ProUserPayload user = new ProUserPayload();

        final String key = cursor.getString(cursor.getColumnIndex(Scheme.User._KEY));
        user.setKey(key);

        final String name = cursor.getString(cursor.getColumnIndex(Scheme.User.NAME));
        user.setName(name);

        final String email = cursor.getString(cursor.getColumnIndex(Scheme.User.EMAIL));
        user.setEmail(email);

        final String phoneNumber = cursor.getString(cursor.getColumnIndex(Scheme.User.PHONE_NUMBER));
        user.setPhoneNumber(phoneNumber);

        final double rating = cursor.getDouble(cursor.getColumnIndex(Scheme.User.RATING));
        user.setRating(rating);

        final AddressPayload address = mapAddress(cursor);
        user.setAddress(address);

        final String professionKey = cursor.getString(cursor.getColumnIndex(Scheme.User.PROFESSION_KEY));
        user.setProfessionKey(professionKey);

        return user;
    }

    @NonNull
    private AddressPayload mapAddress(@NonNull Cursor cursor) {
        final AddressPayload address = new AddressPayload();
        final String street = cursor.getString(cursor.getColumnIndex(Scheme.User.ADDRESS_STREET));
        address.setStreet(street);

        final String number = cursor.getString(cursor.getColumnIndex(Scheme.User.ADDRESS_NUMBER));
        address.setNumber(number);

        final float latitude = cursor.getFloat(cursor.getColumnIndex(Scheme.User.ADDRESS_LATITUDE));
        address.setLatitude(latitude);

        final float longitude = cursor.getFloat(cursor.getColumnIndex(Scheme.User.ADDRESS_LONGITUDE));
        address.setLongitude(longitude);

        final String countryKey = cursor.getString(cursor.getColumnIndex(Scheme.User.COUNTRY_KEY));
        address.setCountryKey(countryKey);

        final String cityKey = cursor.getString(cursor.getColumnIndex(Scheme.User.CITY_KEY));
        address.setCityKey(cityKey);

        return address;
    }
}
