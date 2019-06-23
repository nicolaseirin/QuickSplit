package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.GroupAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.GroupModelIn;
import org.quicksplit.models.LeaveGroup;
import org.quicksplit.service.GroupClient;
import org.quicksplit.service.UserClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment implements View.OnClickListener {

    private static final String FRAGMENT_ID = "fragment_id";
    private String fragmentId;

    static final int ADD_GROUP_REQUEST = 0;
    static final int MODIFY_GROUP_REQUEST = 1;

    private LinearLayout mLinearLayoutEmptyGroups;

    private List<GroupModelIn> groups;
    private RecyclerView mRecyclerViewGroups;
    private GroupAdapter mRecyclerViewGroupsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    private FloatingActionButton mButtonCreateGroup;

    private OnFragmentInteractionListener mListener;

    public GroupsFragment() {
    }

    public static GroupsFragment newInstance(String fragmentId) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_ID, fragmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentId = getArguments().getString(FRAGMENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        mRecyclerViewGroups = view.findViewById(R.id.groupsRecyclerView);

        mButtonCreateGroup = view.findViewById(R.id.btn_createGroup);
        mButtonCreateGroup.setOnClickListener(this);

        mLinearLayoutEmptyGroups = view.findViewById(R.id.lly_emptyGroups);

        getGroups();

        return view;
    }

    private void getGroups() {
        TokenManager tokenManager = new TokenManager(getContext());
        ServiceGenerator sg = new ServiceGenerator();
        //UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());
        UserClient client = sg.createServiceNs(UserClient.class, tokenManager.getToken());
        Call<List<GroupModelIn>> call = client.getUserGroups(tokenManager.getUserIdFromToken());

        final ProgressDialog loading = ProgressDialog.show(getActivity(), getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        call.enqueue(new Callback<List<GroupModelIn>>() {
            @Override
            public void onResponse(Call<List<GroupModelIn>> call, Response<List<GroupModelIn>> response) {
                if (response.isSuccessful()) {
                    groups = response.body();
                    if (groups.size() == 0) {
                        mLinearLayoutEmptyGroups.setVisibility(View.VISIBLE);
                    } else {
                        mLinearLayoutEmptyGroups.setVisibility(View.GONE);
                    }

                    buildRecyclerViewGroups();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(getActivity(), "Error al obtener grupos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupModelIn>> call, Throwable t) {
                loading.dismiss();
                loadFragment(new ErrorFragment());
            }
        });
    }

    private void loadFragment(Fragment fragment) {

        Bundle data = new Bundle();
        data.putString("fragment_id", fragmentId + "");
        fragment.setArguments(data);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void buildRecyclerViewGroups() {
        mRecyclerViewGroups.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(getContext());
        mRecyclerViewGroupsAdapter = new GroupAdapter(groups, getContext());
        mRecyclerViewGroups.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewGroups.setAdapter(mRecyclerViewGroupsAdapter);

        mRecyclerViewGroupsAdapter.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onReportClick(GroupModelIn group) {
                getReportGroup(group);
            }

            @Override
            public void onModifyClick(GroupModelIn group) {
                modifyGroup(group);
            }

            @Override
            public void onLeaveClick(GroupModelIn group) {
                tryLeaveGroup(group);
            }

            @Override
            public void onDeleteClick(GroupModelIn group) {
                tryDeleteGroup(group);
            }
        });
    }

    private void getReportGroup(GroupModelIn group) {
        Intent intent = new Intent(getContext(), ReportActivity.class);
        intent.putExtra("EXTRA_GROUP_ID", group.getId());
        startActivity(intent);
    }

    private void modifyGroup(GroupModelIn group) {
        Intent intent = new Intent(getContext(), ModifyGroupActivity.class);
        intent.putExtra("EXTRA_GROUP_ID", group.getId());
        startActivityForResult(intent, MODIFY_GROUP_REQUEST);
    }

    private void tryLeaveGroup(final GroupModelIn group) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        leaveGroup(group);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Quiere abandonar el grupo?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void leaveGroup(GroupModelIn group) {
        TokenManager tokenManager = new TokenManager(getContext());

        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

        final ProgressDialog loading = ProgressDialog.show(getActivity(), getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        LeaveGroup leaveGroup = new LeaveGroup();
        leaveGroup.setUserId(Integer.parseInt(tokenManager.getUserIdFromToken()));
        leaveGroup.setGroupId(Integer.parseInt(group.getId()));

        Call<Void> call = client.leaveGroup(leaveGroup);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    getGroups();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    try {
                        Toast.makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.dismiss();
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    private void tryDeleteGroup(final GroupModelIn group) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteGroup(group);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Quiere borrar el grupo?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void deleteGroup(GroupModelIn group) {
        TokenManager tokenManager = new TokenManager(getContext());

        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

        final ProgressDialog loading = ProgressDialog.show(getActivity(), getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        Call<Void> call = client.deleteGroup(group.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    getGroups();
                } else {
                    loading.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.error_delete_group) + " " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(), "Error en la comunicación al borrar grupo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Intent createGroup = new Intent(getActivity(), CreateGroupActivity.class);
        startActivityForResult(createGroup, ADD_GROUP_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_GROUP_REQUEST || requestCode == MODIFY_GROUP_REQUEST) {
            if (resultCode == RESULT_OK)
                getGroups();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
