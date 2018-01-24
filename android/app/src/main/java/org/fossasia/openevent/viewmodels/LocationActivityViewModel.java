package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.FilterableRealmLiveData;
import org.fossasia.openevent.dbutils.RealmDataRepository;


import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Predicate;

public class LocationActivityViewModel extends ViewModel {

    private FilterableRealmLiveData<Session> filterableRealmLiveData;
    private LiveData<List<Session>> filteredSessions;
    private RealmDataRepository realmRepo;
    private String searchText = "";

    public LocationActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<List<Session>> getSessionByLocation(String location,String searchText) {
        if(filterableRealmLiveData == null)
            filterableRealmLiveData = RealmDataRepository.asFilterableLiveData(realmRepo.getSessionByLocation(location));
        if (!this.searchText.equals(searchText) || filteredSessions == null) {
            setSearchText(searchText);
            final String query = searchText.toLowerCase(Locale.getDefault());
            Predicate<Session> predicate = session -> session.getName()
                    .toLowerCase(Locale.getDefault())
                    .contains(query);
            filterableRealmLiveData.filter(predicate);
            if (filteredSessions == null) {
                filteredSessions = Transformations.map(filterableRealmLiveData, input -> input);
            }
        }
        return filteredSessions;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
