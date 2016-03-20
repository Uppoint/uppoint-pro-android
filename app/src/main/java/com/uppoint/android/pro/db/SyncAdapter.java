package com.uppoint.android.pro.db;

import com.appspot.uppoint_api.uppointApi.model.AddressPayload;
import com.appspot.uppoint_api.uppointApi.model.CategoryPayload;
import com.appspot.uppoint_api.uppointApi.model.CityPayload;
import com.appspot.uppoint_api.uppointApi.model.CountryPayload;
import com.appspot.uppoint_api.uppointApi.model.EventPayload;
import com.appspot.uppoint_api.uppointApi.model.ProUserPayload;
import com.appspot.uppoint_api.uppointApi.model.ProfessionPayload;
import com.appspot.uppoint_api.uppointApi.model.ServiceTypePayload;
import com.appspot.uppoint_api.uppointApi.model.SyncPayload;
import com.appspot.uppoint_api.uppointApi.model.UserDefinedServicePayload;
import com.uppoint.android.pro.core.ApiFactory;
import com.uppoint.android.pro.core.util.LocalePairUtil;
import com.uppoint.android.pro.core.util.Preconditions;
import com.uppoint.android.pro.core.util.SharedPreferenceConstants;
import com.uppoint.android.pro.db.mapper.SyncEventMapper;
import com.uppoint.android.pro.db.mapper.SyncKeyMapper;
import com.uppoint.android.pro.db.mapper.SyncServiceMapper;
import com.uppoint.android.pro.db.mapper.SyncUserMapper;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import bg.dalexiev.bender.content.EntityCursor;
import bg.dalexiev.bender.content.ResolverCommandBuilder;

import static bg.dalexiev.bender.db.Predicate.eq;
import static bg.dalexiev.bender.db.Predicate.gt;

/**
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ARG_USER_KEY = "arg_user_key";
    public static final String ARG_TIMESTAMP = "arg_timestamp";

    private final ContentResolver mContentResolver;

    private final ResolverCommandBuilder mBuilder;

    public SyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false /* don't allow parallel sync */);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mBuilder = new ResolverCommandBuilder();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        final String timestamp = extras.getString(ARG_TIMESTAMP);
        final String userKey = extras.getString(ARG_USER_KEY);
        Preconditions.nonNull(userKey, "Missing required parameter arg_user_key");

        SyncPayload localChanges = null;
        if (timestamp != null) {
            localChanges = fetchLocalChanges(timestamp, userKey);
        }

        try {
            final SyncPayload remoteChanges = ApiFactory.getApi(getContext()).users().sync(userKey, localChanges)
                    .setTimestamp(timestamp).execute();
            applyRemoteChanges(remoteChanges, syncResult);
            mContentResolver.notifyChange(Scheme.BASE_URI, null);
        } catch (IOException e) {
            syncResult.hasHardError();
        }
    }

    private void applyRemoteChanges(SyncPayload remoteChanges, SyncResult syncResult) {
        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        insertCountries(remoteChanges, operations);
        insertCities(remoteChanges, operations);

        insertCategories(remoteChanges, operations);
        insertProfessions(remoteChanges, operations);
        insertServiceTypes(remoteChanges, operations);

        insertRemoteUser(remoteChanges, operations);
        insertUserServices(remoteChanges, operations);
        insertEvents(remoteChanges, operations);

        try {
            final ContentProviderResult[] results = mContentResolver.applyBatch(Provider.AUTHORITY, operations);
            saveSyncTimestamp(remoteChanges.getLastSyncTimestamp());
            if (results.length > 0) {
                mContentResolver.notifyChange(Scheme.BASE_URI, null);
            }
        } catch (RemoteException | OperationApplicationException e) {
            syncResult.hasHardError();
        }
    }

    private void saveSyncTimestamp(long lastSyncTimestamp) {
        final SharedPreferences prefs = getContext()
                .getSharedPreferences(SharedPreferenceConstants.SETTINGS_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(SharedPreferenceConstants.KEY_SYNC_TIMESTAMP, String.valueOf(lastSyncTimestamp)).apply();
    }

    private void insertEvents(SyncPayload remoteChanges, ArrayList<ContentProviderOperation> operations) {
        final List<EventPayload> events = remoteChanges.getUpdatedEvents();
        if ((events == null) || events.isEmpty()) {
            return;
        }

        for (EventPayload event : events) {
            final ContentProviderOperation insert = createEventInsert(event, remoteChanges.getLastSyncTimestamp());
            operations.add(insert);
        }
    }

    private ContentProviderOperation createEventInsert(EventPayload event, long lastSyncTimestamp) {
        return ContentProviderOperation.newInsert(buildSyncUri(Scheme.Event.URI))
                .withValue(Scheme.Event._KEY, event.getKey())
                .withValue(Scheme.Event.TITLE, event.getTitle())
                .withValue(Scheme.Event.DESCRIPTION, event.getDescription())
                .withValue(Scheme.Event.START_TIME, event.getStartTime())
                .withValue(Scheme.Event.END_TIME, event.getEndTime())
                .withValue(Scheme.Event.USER_KEY, event.getUserKey())
                .withValue(Scheme.Event._LAST_UPDATE, lastSyncTimestamp)
                .build();
    }

    private void insertUserServices(SyncPayload remoteChanges, ArrayList<ContentProviderOperation> operations) {
        final List<UserDefinedServicePayload> services = remoteChanges.getUpdatedServices();
        if ((services == null) || services.isEmpty()) {
            return;
        }

        for (UserDefinedServicePayload service : services) {
            final ContentProviderOperation insert = createServiceInsert(service, remoteChanges.getLastSyncTimestamp());
            operations.add(insert);
        }
    }

    private ContentProviderOperation createServiceInsert(UserDefinedServicePayload service, long lastSyncTimestamp) {
        return ContentProviderOperation.newInsert(buildSyncUri(Scheme.UserDefinedService.URI))
                .withValue(Scheme.UserDefinedService._KEY, service.getKey())
                .withValue(Scheme.UserDefinedService.NAME, service.getName())
                .withValue(Scheme.UserDefinedService.DESCRIPTION, service.getDescription())
                .withValue(Scheme.UserDefinedService.PRICE, service.getPrice())
                .withValue(Scheme.UserDefinedService.DURATION, service.getPrice())
                .withValue(Scheme.UserDefinedService.USER_KEY, service.getUserKey())
                .withValue(Scheme.UserDefinedService.SERVICE_TYPE_KEY, service.getServiceTypeKey())
                .withValue(Scheme.UserDefinedService._LAST_UPDATE, lastSyncTimestamp)
                .build();
    }

    private void insertServiceTypes(SyncPayload remoteChanges, ArrayList<ContentProviderOperation> operations) {
        final List<ServiceTypePayload> serviceTypes = remoteChanges.getUpdatedServiceTypes();
        if ((serviceTypes == null) || serviceTypes.isEmpty()) {
            return;
        }

        for (ServiceTypePayload serviceType : serviceTypes) {
            final ContentProviderOperation insert = createServiceTypeInsert(serviceType);
            operations.add(insert);
        }
    }


    private ContentProviderOperation createServiceTypeInsert(ServiceTypePayload serviceType) {
        return ContentProviderOperation.newInsert(buildSyncUri(Scheme.ServiceType.URI))
                .withValue(Scheme.ServiceType._KEY, serviceType.getKey())
                .withValue(Scheme.ServiceType.NAME, LocalePairUtil.toJson(serviceType.getTranslation()))
                .withValue(Scheme.ServiceType.PROFESSION_KEY, serviceType.getProfessionKey())
                .build();
    }

    private void insertProfessions(SyncPayload remoteChanges, ArrayList<ContentProviderOperation> operations) {
        final List<ProfessionPayload> professions = remoteChanges.getUpdatedProfessions();
        if ((professions == null) || professions.isEmpty()) {
            return;
        }

        for (ProfessionPayload profession : professions) {
            final ContentProviderOperation insert = createProfessionInsert(profession);
            operations.add(insert);
        }
    }

    private ContentProviderOperation createProfessionInsert(ProfessionPayload profession) {
        return ContentProviderOperation.newInsert(buildSyncUri(Scheme.Profession.URI))
                .withValue(Scheme.Profession._KEY, profession.getKey())
                .withValue(Scheme.Profession.NAME, LocalePairUtil.toJson(profession.getTranslation()))
                .withValue(Scheme.Profession.CATEGORY_KEY, profession.getCategoryKey())
                .build();
    }

    private void insertCategories(SyncPayload remoteChanges, ArrayList<ContentProviderOperation> operations) {
        final List<CategoryPayload> categories = remoteChanges.getUpdatedCategories();
        if ((categories == null) || categories.isEmpty()) {
            return;
        }

        for (CategoryPayload category : categories) {
            final ContentProviderOperation insert = createCategoryInsert(category);
            operations.add(insert);
        }
    }

    private ContentProviderOperation createCategoryInsert(CategoryPayload category) {
        return ContentProviderOperation.newInsert(buildSyncUri(Scheme.Category.URI))
                .withValue(Scheme.Category._KEY, category.getKey())
                .withValue(Scheme.Category.NAME, LocalePairUtil.toJson(category.getTranslation()))
                .build();
    }

    private void insertCities(SyncPayload remoteChanges, ArrayList<ContentProviderOperation> operations) {
        final List<CityPayload> cities = remoteChanges.getUpdatedCities();
        if ((cities == null) || cities.isEmpty()) {
            return;
        }

        for (CityPayload city : cities) {
            final ContentProviderOperation insert = createCityInsert(city);
            operations.add(insert);
        }
    }

    private ContentProviderOperation createCityInsert(CityPayload city) {
        return ContentProviderOperation.newInsert(buildSyncUri(Scheme.City.URI))
                .withValue(Scheme.City._KEY, city.getKey())
                .withValue(Scheme.City.NAME, LocalePairUtil.toJson(city.getTranslation()))
                .withValue(Scheme.City.COUNTRY_KEY, city.getCountryKey())
                .build();
    }

    private void insertCountries(SyncPayload remoteChanges, ArrayList<ContentProviderOperation> operations) {
        final List<CountryPayload> countries = remoteChanges.getUpdatedCountries();
        if ((countries == null) || countries.isEmpty()) {
            return;
        }

        for (CountryPayload country : countries) {
            final ContentProviderOperation insert = createCountryInsert(country);
            operations.add(insert);
        }
    }

    private ContentProviderOperation createCountryInsert(CountryPayload country) {
        return ContentProviderOperation
                .newInsert(buildSyncUri(Scheme.Country.URI))
                .withValue(Scheme.Country._KEY, country.getKey())
                .withValue(Scheme.Country.NAME, LocalePairUtil.toJson(country.getTranslation()))
                .build();
    }

    private void insertRemoteUser(SyncPayload remoteChanges, ArrayList<ContentProviderOperation> operations) {
        final ProUserPayload proUserPayload = remoteChanges.getProfile();
        if (proUserPayload != null) {
            final ContentProviderOperation.Builder insertBuilder = ContentProviderOperation
                    .newInsert(buildSyncUri(Scheme.User.URI))
                    .withValue(Scheme.User._KEY, proUserPayload.getKey())
                    .withValue(Scheme.User.NAME, proUserPayload.getName())
                    .withValue(Scheme.User.EMAIL, proUserPayload.getEmail())
                    .withValue(Scheme.User.PHONE_NUMBER, proUserPayload.getPhoneNumber())
                    .withValue(Scheme.User.RATING, proUserPayload.getRating())
                    .withValue(Scheme.User.PROFESSION_KEY, proUserPayload.getProfessionKey())
                    .withValue(Scheme.User._LAST_UPDATE, remoteChanges.getLastSyncTimestamp());
            final AddressPayload address = proUserPayload.getAddress();
            if (address != null) {
                insertBuilder
                        .withValue(Scheme.User.ADDRESS_STREET, address.getStreet())
                        .withValue(Scheme.User.ADDRESS_NUMBER, address.getNumber())
                        .withValue(Scheme.User.ADDRESS_LONGITUDE, address.getLongitude())
                        .withValue(Scheme.User.ADDRESS_LATITUDE, address.getLatitude());
            }
            operations.add(insertBuilder.build());
        }
    }

    private static Uri buildSyncUri(Uri uri) {
        return uri.buildUpon()
                .appendQueryParameter(Provider.PARAM_CONFLICT_ALGORITHM,
                        String.valueOf(SQLiteDatabase.CONFLICT_REPLACE))
                .appendQueryParameter(Provider.PARAM_IS_SYNC, "true")
                .build();
    }

    private SyncPayload fetchLocalChanges(String timestamp, String userKey) {
        final SyncPayload syncPayload = new SyncPayload();

        final ProUserPayload proUserPayload = fetchProfile(userKey, timestamp);
        syncPayload.setProfile(proUserPayload);

        final List<UserDefinedServicePayload> updatedServices = fetchUpdatedServices(userKey, timestamp);
        syncPayload.setUpdatedServices(updatedServices);

        final List<EventPayload> updatedEvents = fetchUpdatedEvents(userKey, timestamp);
        syncPayload.setUpdatedEvents(updatedEvents);

        final List<String> deletedServiceKeys = fetchDeletedServiceKeys(userKey, timestamp);
        syncPayload.setDeletedServices(deletedServiceKeys);

        final List<String> deletedEventKeys = fetchDeletedEventKeys(userKey, timestamp);
        syncPayload.setDeletedEvents(deletedEventKeys);

        return syncPayload;
    }

    private List<String> fetchDeletedEventKeys(String userKey, String timestamp) {
        final EntityCursor<String> keysCursor = mBuilder
                .query(mContentResolver, String.class)
                .onUri(Scheme.Event.URI)
                .select(Scheme.Event._KEY)
                .where(eq(Scheme.UserDefinedService.USER_KEY, userKey))
                .where(gt(Scheme.UserDefinedService._LAST_UPDATE, timestamp))
                .where(eq(Scheme.UserDefinedService._IS_DELETED, "1"))
                .useRowMapper(new SyncKeyMapper())
                .execute();

        return cursorAsList(keysCursor);
    }

    private List<String> fetchDeletedServiceKeys(String userKey, String timestamp) {
        final EntityCursor<String> keysCursor = mBuilder
                .query(mContentResolver, String.class)
                .onUri(Scheme.UserDefinedService.URI)
                .select(Scheme.UserDefinedService._KEY)
                .where(eq(Scheme.UserDefinedService.USER_KEY, userKey))
                .where(gt(Scheme.UserDefinedService._LAST_UPDATE, timestamp))
                .where(eq(Scheme.UserDefinedService._IS_DELETED, "1"))
                .useRowMapper(new SyncKeyMapper())
                .execute();

        return cursorAsList(keysCursor);
    }

    private List<EventPayload> fetchUpdatedEvents(String userKey, String timestamp) {
        final EntityCursor<EventPayload> eventsCursor = mBuilder
                .query(mContentResolver, EventPayload.class)
                .onUri(Scheme.Event.URI)
                .select(Scheme.Event.SYNC_PROJECTION)
                .where(eq(Scheme.Event.USER_KEY, userKey))
                .where(gt(Scheme.Event._LAST_UPDATE, timestamp))
                .where(eq(Scheme.Event._IS_DELETED, "0"))
                .useRowMapper(new SyncEventMapper())
                .execute();

        return cursorAsList(eventsCursor);
    }

    private List<UserDefinedServicePayload> fetchUpdatedServices(String userKey, String timestamp) {
        final EntityCursor<UserDefinedServicePayload> servicesCursor = mBuilder
                .query(mContentResolver, UserDefinedServicePayload.class).onUri(Scheme.UserDefinedService.URI)
                .select(Scheme.UserDefinedService.SYNC_PROJECTION)
                .where(eq(Scheme.UserDefinedService.USER_KEY, userKey))
                .where(gt(Scheme.UserDefinedService._LAST_UPDATE, timestamp))
                .where(eq(Scheme.UserDefinedService._IS_DELETED, "0"))
                .useRowMapper(new SyncServiceMapper())
                .execute();

        return cursorAsList(servicesCursor);
    }

    @Nullable
    private ProUserPayload fetchProfile(String userKey, String timestamp) {
        final EntityCursor<ProUserPayload> cursor = mBuilder
                .query(mContentResolver, ProUserPayload.class)
                .onUri(Scheme.User.URI)
                .select(Scheme.User.SYNC_PROJECTION)
                .where(eq(Scheme.User._KEY, userKey))
                .where(gt(Scheme.User._LAST_UPDATE, timestamp))
                .useRowMapper(new SyncUserMapper())
                .execute();
        ProUserPayload proUserPayload = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                proUserPayload = cursor.getEntity();
            }
            cursor.close();
        }
        return proUserPayload;
    }

    private static <E> List<E> cursorAsList(EntityCursor<E> cursor) {
        if (cursor == null) {
            return Collections.emptyList();
        }
        final List<E> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            list.add(cursor.getEntity());
        }

        return list;
    }
}
