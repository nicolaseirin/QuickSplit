package org.quicksplit.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.widget.Toast;

import org.quicksplit.models.User;
import org.quicksplit.service.UserClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private UserClient userClient;

    public UserRepository(UserClient userClient) {
        this.userClient = userClient;
    }

    public LiveData<List<User>> getUsers() {
        final MutableLiveData<List<User>> data = new MutableLiveData<>();
        userClient.getUsers().enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        return data;
    }
}
