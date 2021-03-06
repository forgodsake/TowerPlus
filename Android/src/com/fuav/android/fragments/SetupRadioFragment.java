package com.fuav.android.fragments;

import android.os.Bundle;

import com.fuav.android.core.MAVLink.MavLinkStreamRates;
import com.fuav.android.core.drone.DroneManager;
import com.fuav.android.core.drone.autopilot.MavLinkDrone;
import com.fuav.android.fragments.calibration.SetupMainPanel;
import com.fuav.android.fragments.calibration.rc.FragmentSetupRC;
import com.fuav.android.fragments.helpers.SuperSetupFragment;

/**
 * This fragment is used to calibrate the drone's radio related functionalities.
 */
public class SetupRadioFragment extends SuperSetupFragment {
	// Extreme RC update rate in this screen
	private static final int RC_MSG_RATE = 50;

	private MavLinkDrone drone;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.drone = DroneManager.getDrone();
	}

	@Override
	public void onStart() {
		super.onStart();
		setupDataStreamingForRcSetup();
	}

	@Override
	public void onStop() {
		super.onStop();
		if(drone!=null){
			drone.getStreamRates().setupStreamRatesFromPref();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setupDataStreamingForRcSetup();
	}

	@Override
	public SetupMainPanel initMainPanel() {
		return new FragmentSetupRC();
	}

//	@Override
//	public SetupMainPanel getMainPanel(int index) {
//		SetupMainPanel setupPanel;
//		switch (index) {
//		case 1:
//			setupPanel = new FragmentSetupFM();
//			break;
//		case 2:
//			setupPanel = new FragmentSetupCH();
//			break;
//		case 3:
//			setupPanel = new FragmentSetupSF();
//			break;
//		case 0:
//		default:
//			setupPanel = new FragmentSetupRC();
//		}
//
//		return setupPanel;
//	}

	public void setupDataStreamingForRcSetup() {
		if(drone!=null){
			MavLinkStreamRates.setupStreamRates(drone.getMavClient(),drone.getSysid(),drone.getCompid(), 1, 0, 1, 1, 1, RC_MSG_RATE,0,0);
		}
	}
}
