package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import io.reactivex.Observable;
import io.realm.RealmResults;
import timber.log.Timber;

public class LocationActivityViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private MutableLiveData<List<Session>> session;
    private MutableLiveData<List<Session>> filteredSession;
    private RealmResults<Session> sessionRealmResult;
    private String searchText = "";

    public LocationActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<List<Session>> getSessionByLocation(String location, String searchText) {
        setSearchText(searchText);

        if (filteredSession == null)
            filteredSession = new MutableLiveData<>();
        if (session == null) {
            session = new MutableLiveData<>();
            sessionRealmResult = realmRepo.getSessionsByLocation(location);
            sessionRealmResult.addChangeListener((sessions, orderedCollectionChangeSet) -> {
                session.setValue(sessions);
                getFilteredSessions();
            });
        } else {
            getFilteredSessions();
        }
        return filteredSession;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    protected void onCleared() {
        sessionRealmResult.removeAllChangeListeners();
        super.onCleared();
    }

    private void getFilteredSessions() {
        final String query = searchText.toLowerCase(Locale.getDefault());

        Observable.fromIterable(session.getValue())
                .filter(session -> session.getTitle()
                        .toLowerCase(Locale.getDefault())
                        .contains(query))
                .toList().subscribe(filteredSessions -> {
            filteredSession.setValue(filteredSessions);
            Timber.d("Filtering done total results %d", filteredSessions.size());

            if (filteredSessions.isEmpty()) {
                Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");
            }
        });
    }
}