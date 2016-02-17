package com.fuav.android.fragments.helpers;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.fuav.android.R;
import com.fuav.android.activities.ConfigurationActivity;
import com.fuav.android.core.drone.DroneInterfaces;
import com.fuav.android.core.drone.DroneManager;
import com.fuav.android.core.drone.autopilot.MavLinkDrone;
import com.fuav.android.fragments.calibration.SetupMainPanel;
import com.fuav.android.fragments.calibration.SetupSidePanel;


/**
 * This fragment is used to calibrate the drone's compass, and accelerometer.
 */
public abstract class SuperSetupFragment extends Fragment implements DroneInterfaces.OnDroneListener,
		OnItemSelectedListener {

	private MavLinkDrone drone;

	protected ConfigurationActivity parentActivity;

	private FragmentManager fragmentManager;
	private SetupMainPanel setupPanel;
	private SetupSidePanel sidePanel;

	public abstract int getSpinnerItems();

	public abstract SetupMainPanel getMainPanel(int index);

	public abstract SetupMainPanel initMainPanel();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof ConfigurationActivity)) {
			throw new IllegalStateException("Parent activity must be "
					+ ConfigurationActivity.class.getName());
		}

		parentActivity = (ConfigurationActivity) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		parentActivity = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.fragment_setup, container, false);

		setupLocalViews(view);

		fragmentManager = getChildFragmentManager();
		setupPanel = (SetupMainPanel) fragmentManager
				.findFragmentById(R.id.fragment_setup_mainpanel);

		if (setupPanel == null) {
			setupPanel = initMainPanel();

			fragmentManager.beginTransaction().add(R.id.fragment_setup_mainpanel, setupPanel)
					.commit();
		}

		sidePanel = (SetupSidePanel) fragmentManager
				.findFragmentById(R.id.fragment_setup_sidepanel);
		if (sidePanel == null) {
			sidePanel = setupPanel.getSidePanel();
			if (sidePanel != null) {
				fragmentManager.beginTransaction().add(R.id.fragment_setup_sidepanel, sidePanel)
						.commit();
			}
		}

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.drone = DroneManager.getDrone();
	}

	@Override
	public void onStart() {
		if(drone!=null){
			drone.addDroneListener(this);
		}else{
			Toast.makeText(getActivity(),"ok",Toast.LENGTH_SHORT).show();
		}

		super.onStart();
	}

	@Override
	public void onStop() {
		if(drone!=null){
			drone.removeDroneListener(this);
		}
		super.onStop();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		changeMainPanel(arg2);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDroneEvent(DroneInterfaces.DroneEventsType event, MavLinkDrone drone) {
		// TODO Auto-generated method stub

	}

	private void setupLocalViews(View view) {
		Spinner spinnerSetup = (Spinner) view.findViewById(R.id.spinnerSetupType);
		spinnerSetup.setOnItemSelectedListener(this);

		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parentActivity,
				getSpinnerItems(), R.layout.spinner_setup);

		spinnerSetup.setAdapter(adapter);
	}

	public void changeMainPanel(int step) {
		setupPanel = getMainPanel(step);
		sidePanel = setupPanel.getSidePanel();

		final FragmentTransaction ft = fragmentManager.beginTransaction();
		if (setupPanel != null) {
			ft.replace(R.id.fragment_setup_mainpanel, setupPanel);
		}

		if (sidePanel != null) {
			ft.replace(R.id.fragment_setup_sidepanel, sidePanel);
		}

		ft.commit();
	}

	public SetupSidePanel changeSidePanel(SetupSidePanel sPanel) {
		sidePanel = sPanel;

		if (setupPanel != null && sidePanel != null)
			setupPanel.setSidePanel(sidePanel);

		final FragmentTransaction ft = fragmentManager.beginTransaction();
		if (sidePanel != null) {
			ft.replace(R.id.fragment_setup_sidepanel, sidePanel);
		}

		ft.commit();

		return sidePanel;
	}

	public void doCalibrationStep(int step) {
		if (setupPanel != null) {
			setupPanel.doCalibrationStep(step);
		}
	}

	public void updateSidePanelTitle(int calibrationStep) {
		if (sidePanel != null) {
			sidePanel.updateDescription(calibrationStep);
		}
	}

}