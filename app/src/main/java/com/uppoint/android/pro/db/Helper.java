package com.uppoint.android.pro.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 */
public class Helper extends SQLiteOpenHelper {

    private static final String DB_NAME = "uppoint_pro.db";
    private static final int DB_VERSION = 1;

    private static final String CREATE_COUNTRIES = "create table " + Scheme.Country.TABLE_NAME + "("
            + Scheme.Country._ID + " integer primary key, "
            + Scheme.Country._KEY + " text unique not null,"
            + Scheme.Country.NAME + " text not null)";

    private static final String CREATE_CITIES = "create table " + Scheme.City.TABLE_NAME + "("
            + Scheme.City._ID + " integer primary key, "
            + Scheme.City._KEY + " text unique not null, "
            + Scheme.City.NAME + " text not null, "
            + Scheme.City.COUNTRY_KEY + " text not null, "
            + "foreign key(" + Scheme.City.COUNTRY_KEY + ") "
            + "references " + Scheme.Country.TABLE_NAME + "(" + Scheme.Country._KEY + "))";

    private static final String CREATE_CATEGORIES = "create table " + Scheme.Category.TABLE_NAME + "("
            + Scheme.Category._ID + " integer primary key, "
            + Scheme.Category._KEY + " text unique not null, "
            + Scheme.Category.NAME + " text not null)";

    private static final String CREATE_PROFESSIONS = "create table " + Scheme.Profession.TABLE_NAME + "("
            + Scheme.Profession._ID + " integer primary key, "
            + Scheme.Profession._KEY + " text unique not null, "
            + Scheme.Profession.NAME + " text not null, "
            + Scheme.Profession.CATEGORY_KEY + " text not null, "
            + "foreign key(" + Scheme.Profession.CATEGORY_KEY + ") "
            + "references " + Scheme.Category.TABLE_NAME + "(" + Scheme.Category._KEY + "))";

    private static final String CREATE_SERVICE_TYPES = "create table " + Scheme.ServiceType.TABLE_NAME + "("
            + Scheme.ServiceType._ID + " integer primary key, "
            + Scheme.ServiceType._KEY + " text unique not null, "
            + Scheme.ServiceType.NAME + " text not null, "
            + Scheme.ServiceType.PROFESSION_KEY + " text not null, "
            + "foreign key(" + Scheme.ServiceType.PROFESSION_KEY + ") "
            + "references " + Scheme.Profession.TABLE_NAME + "(" + Scheme.Profession._KEY + "))";

    private static final String CREATE_USERS = "create table " + Scheme.User.TABLE_NAME + "("
            + Scheme.User._ID + " integer primary key, "
            + Scheme.User._KEY + " text unique not null, "
            + Scheme.User.NAME + " text, "
            + Scheme.User.EMAIL + " text, "
            + Scheme.User.PHONE_NUMBER + " text, "
            + Scheme.User.RATING + " real, "
            + Scheme.User.ADDRESS_STREET + " text, "
            + Scheme.User.ADDRESS_NUMBER + " text, "
            + Scheme.User.ADDRESS_LATITUDE + " real, "
            + Scheme.User.ADDRESS_LONGITUDE + " real, "
            + Scheme.User.PROFESSION_KEY + " text, "
            + Scheme.User.COUNTRY_KEY + " text, "
            + Scheme.User.CITY_KEY + " text, "
            + Scheme.User._LAST_UPDATE + " integer default(strftime('%s','now')), "
            + Scheme.User._IS_DELETED + " integer default 0, "
            + "foreign key(" + Scheme.User.PROFESSION_KEY + ") "
            + "references " + Scheme.Profession.TABLE_NAME + "(" + Scheme.Profession._KEY + "), "
            + "foreign key(" + Scheme.User.CITY_KEY + ") "
            + "references " + Scheme.City.TABLE_NAME + "(" + Scheme.City._KEY + "), "
            + "foreign key(" + Scheme.User.COUNTRY_KEY + ") "
            + "references " + Scheme.Country.TABLE_NAME + "(" + Scheme.Country._KEY + "))";

    private static final String CREATE_USER_DEFINED_SERVICES = "create table " + Scheme.UserDefinedService.TABLE_NAME
            + "("
            + Scheme.UserDefinedService._ID + " integer primary key, "
            + Scheme.UserDefinedService._KEY + " text unique, "
            + Scheme.UserDefinedService.NAME + " text not null, "
            + Scheme.UserDefinedService.DESCRIPTION + " text, "
            + Scheme.UserDefinedService.PRICE + " real not null, "
            + Scheme.UserDefinedService.DURATION + " integer not null, "
            + Scheme.UserDefinedService.SERVICE_TYPE_KEY + " text not null, "
            + Scheme.UserDefinedService.USER_KEY + " text not null, "
            + Scheme.UserDefinedService._LAST_UPDATE + " integer default(strftime('%s','now')), "
            + Scheme.UserDefinedService._IS_DELETED + " integer default 0, "
            + "foreign key(" + Scheme.UserDefinedService.SERVICE_TYPE_KEY + ") "
            + "references " + Scheme.ServiceType.TABLE_NAME + "(" + Scheme.ServiceType._KEY + "), "
            + "foreign key(" + Scheme.UserDefinedService.USER_KEY + ") "
            + "references " + Scheme.User.TABLE_NAME + "(" + Scheme.User._KEY + "))";

    private static final String CREATE_EVENTS = "create table " + Scheme.Event.TABLE_NAME + "("
            + Scheme.Event._ID + " integer primary key, "
            + Scheme.Event._KEY + " text unique, "
            + Scheme.Event.TITLE + " text not null, "
            + Scheme.Event.DESCRIPTION + " text, "
            + Scheme.Event.START_TIME + " integer not null, "
            + Scheme.Event.END_TIME + " integer not null, "
            + Scheme.Event.USER_KEY + " text not null, "
            + Scheme.Event._LAST_UPDATE + " integer default(strftime('%s','now')), "
            + Scheme.Event._IS_DELETED + " integer default 0, "
            + "foreign key(" + Scheme.Event.USER_KEY + ") "
            + "references " + Scheme.User.TABLE_NAME + "(" + Scheme.User._KEY + "))";

    public Helper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COUNTRIES);
        db.execSQL(CREATE_CITIES);

        db.execSQL(CREATE_CATEGORIES);
        db.execSQL(CREATE_PROFESSIONS);
        db.execSQL(CREATE_SERVICE_TYPES);

        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_USER_DEFINED_SERVICES);
        db.execSQL(CREATE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }
}
