package com.fuav.android.fragments.widget;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fuav.android.R;
import com.fuav.android.core.drone.DroneManager;
import com.fuav.android.dialogs.SlideToUnlockDialog;
import com.fuav.android.fragments.control.BaseFlightControlFragment;
import com.fuav.android.fragments.control.FlightControlManagerFragment;
import com.fuav.android.utils.analytics.GAUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.ControlApi;
import com.o3dr.android.client.apis.FollowApi;
import com.o3dr.android.client.apis.VehicleApi;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeEventExtra;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.GuidedState;
import com.o3dr.services.android.lib.drone.property.State;
import com.o3dr.services.android.lib.drone.property.VehicleMode;
import com.o3dr.services.android.lib.gcs.follow.FollowState;

public class BasicControlFragment extends BaseFlightControlFragment {

    private static final String ACTION_FLIGHT_ACTION_BUTTON = "Copter flight action button";

    private static final IntentFilter eventFilter = new IntentFilter();

    static {
        eventFilter.addAction(AttributeEvent.AUTOPILOT_ERROR);
        eventFilter.addAction(AttributeEvent.AUTOPILOT_MESSAGE);
        eventFilter.addAction(AttributeEvent.STATE_ARMING);
        eventFilter.addAction(AttributeEvent.STATE_CONNECTED);
        eventFilter.addAction(AttributeEvent.STATE_DISCONNECTED);
        eventFilter.addAction(AttributeEvent.STATE_UPDATED);
        eventFilter.addAction(AttributeEvent.TYPE_UPDATED);
        eventFilter.addAction(AttributeEvent.STATE_VEHICLE_MODE);
        eventFilter.addAction(AttributeEvent.FOLLOW_START);
        eventFilter.addAction(AttributeEvent.FOLLOW_STOP);
        eventFilter.addAction(AttributeEvent.FOLLOW_UPDATE);
        eventFilter.addAction(AttributeEvent.MISSION_DRONIE_CREATED);
    }

    private final BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case AttributeEvent.AUTOPILOT_ERROR:
                    break;

                case AttributeEvent.AUTOPILOT_MESSAGE:
                    break;

                case AttributeEvent.STATE_ARMING:
                case AttributeEvent.STATE_CONNECTED:
                case AttributeEvent.STATE_DISCONNECTED:
                case AttributeEvent.STATE_UPDATED:
                    setupButtonsByFlightState();
                    break;
                case AttributeEvent.TYPE_UPDATED:
                    break;
                case AttributeEvent.STATE_VEHICLE_MODE:
                    updateFlightModeButtons();
                    break;

                case AttributeEvent.FOLLOW_START:
                case AttributeEvent.FOLLOW_STOP:
                    final FollowState followState = getDrone().getAttribute(AttributeType.FOLLOW_STATE);
                    if (followState != null) {
                        String eventLabel = null;
                        switch (followState.getState()) {
                            case FollowState.STATE_START:
                                eventLabel = "FollowMe enabled";
                                break;

                            case FollowState.STATE_RUNNING:
                                eventLabel = "FollowMe running";
                                break;

                            case FollowState.STATE_END:
                                eventLabel = "FollowMe disabled";
                                break;

                            case FollowState.STATE_INVALID:
                                eventLabel = "FollowMe error: invalid state";
                                break;

                            case FollowState.STATE_DRONE_DISCONNECTED:
                                eventLabel = "FollowMe error: drone not connected";
                                break;

                            case FollowState.STATE_DRONE_NOT_ARMED:
                                eventLabel = "FollowMe error: drone not armed";
                                break;
                        }

                        if (eventLabel != null) {
                            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder()
                                    .setCategory(GAUtils.Category.FLIGHT)
                                    .setAction(ACTION_FLIGHT_ACTION_BUTTON)
                                    .setLabel(eventLabel);
                            GAUtils.sendEvent(eventBuilder);

                            Toast.makeText(getActivity(), eventLabel, Toast.LENGTH_SHORT).show();
                        }
                    }

                 /* FALL - THROUGH */
                case AttributeEvent.FOLLOW_UPDATE:
                    updateFlightModeButtons();
                    updateFollowButton();
                    break;

                case AttributeEvent.MISSION_DRONIE_CREATED:
                    //Get the bearing of the dronie mission.
                    float bearing = intent.getFloatExtra(AttributeEventExtra.EXTRA_MISSION_DRONIE_BEARING, -1);
                    if (bearing >= 0) {
                        final FlightControlManagerFragment parent = (FlightControlManagerFragment) getParentFragment();
                        if (parent != null) {
                            parent.updateMapBearing(bearing);
                        }
                    }
                    break;


            }
        }
    };

    private void setupButtonsForArmed() {
        button_take_off.setVisibility(View.GONE);
        button_land.setVisibility(View.VISIBLE);
    }

    private void setupButtonsForDisarmed() {
        button_take_off.setVisibility(View.VISIBLE);
        button_land.setVisibility(View.GONE);
    }

    private void updateFlightModeButtons() {
        initBackground();

        State droneState = getDrone().getAttribute(AttributeType.STATE);
        if (droneState == null)
            return;

        final VehicleMode flightMode = droneState.getVehicleMode();
        if (flightMode == null)
            return;

        switch (flightMode) {

            case COPTER_GUIDED:
                final Drone drone = getDrone();
                final GuidedState guidedState = drone.getAttribute(AttributeType.GUIDED_STATE);
                final FollowState followState = drone.getAttribute(AttributeType.FOLLOW_STATE);
                if (guidedState.isInitialized() && !followState.isEnabled()) {
                    button_hover.setActivated(true);
                }
                break;

            case COPTER_BRAKE:
                button_hover.setActivated(true);
                break;

            case COPTER_RTL:
                button_go_home.setActivated(true);
                break;

            case COPTER_LAND:
                button_land.setActivated(true);
                break;
            default:
                break;
        }
    }

    private void setupButtonsByFlightState() {
        final State droneState = getDrone().getAttribute(AttributeType.STATE);
        if (droneState.isArmed()) {
            setupButtonsForArmed();
            if (droneState.isFlying()) {
//                    setupButtonsForFlying();
            } else {

            }
        } else {
            setupButtonsForDisarmed();
        }
    }

    private void updateFollowButton() {
        FollowState followState = getDrone().getAttribute(AttributeType.FOLLOW_STATE);
        if (followState == null)
            return;

        switch (followState.getState()) {
            case FollowState.STATE_START:
                break;

            case FollowState.STATE_RUNNING:
                button_follow_me.setActivated(true);
                break;

            default:
                button_follow_me.setActivated(false);
                break;
        }
    }

    private Button button_take_off;
    private Button button_land;
    private Button button_go_home;
    private Button button_hover;
    private Button button_follow_me;


    public BasicControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_control, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button_take_off= (Button) view.findViewById(R.id.button_take_off);
        button_take_off.setOnClickListener(this);
        button_land= (Button) view.findViewById(R.id.button_land);
        button_land.setOnClickListener(this);
        button_go_home= (Button) view.findViewById(R.id.button_go_home);
        button_go_home.setOnClickListener(this);
        button_hover= (Button) view.findViewById(R.id.button_hover);
        button_hover.setOnClickListener(this);
        button_follow_me= (Button) view.findViewById(R.id.button_follow_me);
        button_follow_me.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(DroneManager.getDrone()!=null){
            switch (v.getId()){
                case R.id.button_take_off:
                    getArmingConfirmation();
                    break;
                case R.id.button_land:
                    getLandConfirmation();
                    break;
                case R.id.button_go_home:
                    getGoHomeConfirmation();
                    break;
                case R.id.button_hover:
                    getHoverConfirmation();
                    break;
                case R.id.button_follow_me:
                    getFolloeMeConfirmation();
                    break;
                default:
                    break;
            }
        }else{
            Toast.makeText(getActivity(),"无人机未连接",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onApiConnected() {
        super.onApiConnected();

        setupButtonsByFlightState();
        updateFlightModeButtons();
        updateFollowButton();

        getBroadcastManager().registerReceiver(eventReceiver, eventFilter);
    }

    @Override
    public void onApiDisconnected() {
        super.onApiDisconnected();
        getBroadcastManager().unregisterReceiver(eventReceiver);
    }

    @Override
    public boolean isSlidingUpPanelEnabled(Drone drone) {
        return false;
    }

    private void getArmingConfirmation() {
        SlideToUnlockDialog unlockDialog = SlideToUnlockDialog.newInstance("arm", new Runnable() {
            @Override
            public void run() {
                getDrone().arm(true);
                final double takeOffAltitude = getAppPrefs().getDefaultAltitude();
                ControlApi.getApi(getDrone()).takeoff(takeOffAltitude, null);
            }
        });
        unlockDialog.show(getChildFragmentManager(), "Slide To Arm");
    }

    private void getGoHomeConfirmation() {
        SlideToUnlockDialog unlockDialog = SlideToUnlockDialog.newInstance("GoHome", new Runnable() {
            @Override
            public void run() {
                VehicleApi.getApi(getDrone()).setVehicleMode(VehicleMode.COPTER_RTL);
            }
        });
        unlockDialog.show(getChildFragmentManager(), "Slide To GoHome");
    }

    private void getHoverConfirmation() {
        SlideToUnlockDialog unlockDialog = SlideToUnlockDialog.newInstance("Hover", new Runnable() {
            @Override
            public void run() {
                final Drone drone = getDrone();
                final FollowState followState = drone.getAttribute(AttributeType.FOLLOW_STATE);
                if (followState.isEnabled()) {
                    FollowApi.getApi(drone).disableFollowMe();
                }
                ControlApi.getApi(drone).pauseAtCurrentLocation(null);
            }
        });
        unlockDialog.show(getChildFragmentManager(), "Slide To Hover");
    }

    private void getLandConfirmation() {
        final SlideToUnlockDialog unlockDialog = SlideToUnlockDialog.newInstance("Land off", new Runnable() {
            @Override
            public void run() {
                VehicleApi.getApi(getDrone()).setVehicleMode(VehicleMode.COPTER_LAND);
            }
        });
        unlockDialog.show(getChildFragmentManager(), "Slide to Land off");
    }

    private void getFolloeMeConfirmation() {
        final SlideToUnlockDialog unlockDialog = SlideToUnlockDialog.newInstance("Folloe Me", new Runnable() {
            @Override
            public void run() {
                toggleFollowMe();
            }
        });
        unlockDialog.show(getChildFragmentManager(), "Slide to Folloe Me");
    }

    private void initBackground() {
        button_go_home.setActivated(false);
        button_land.setActivated(false);
        button_hover.setActivated(false);
    }
}
