package org.quicksplit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import org.quicksplit.model.User;
import org.quicksplit.service.UserClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    private String token;
    private String userId;
    private List<User> users;
    private RecyclerView mRecyclerViewFriends;
    private FriendsAdapter mRecycleViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        mRecyclerViewFriends = (RecyclerView) view.findViewById(R.id.friendsReciclerView);

        getUserListItems();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getUserListItems() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        token = preferences.getString("token", null);

        UserClient client = ServiceGenerator.createService(UserClient.class, token);
        Call<List<User>> call = client.getUsers();

        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users = response.body();
                    buildRecyclerViewAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(), "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerViewAdapter() {
        mRecyclerViewFriends.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(getContext());
        mRecycleViewAdapter = new FriendsAdapter(users);
        mRecyclerViewFriends.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewFriends.setAdapter(mRecycleViewAdapter);

        mRecycleViewAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {

            }

            @Override
            public void onDeleteClick(User user) {
                //TODO: Delete friend here, and then refresh the friend list

                JWT parsedJWT = new JWT(token);
                Claim subscriptionMetaData = parsedJWT.getClaim("Id");
                userId = subscriptionMetaData.asString();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                token = preferences.getString("token", null);

                UserClient client = ServiceGenerator.createService(UserClient.class, token);
                //TODO: Pass the real idFriend
                Call<Void> call = client.deleteFriend(userId, "1");

                final ProgressDialog loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please wait...", false, false);

                call.enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            //Todo: Refresh recycler view here
                            loading.dismiss();
                        } else {
                            loading.dismiss();
                            Toast.makeText(getActivity(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
