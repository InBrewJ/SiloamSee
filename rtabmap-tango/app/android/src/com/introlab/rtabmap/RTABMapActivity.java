package com.introlab.rtabmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Debug;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

// For location services

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Location;

// gms.common.api is in "play-services-basement"
// gms.location and gms.tasks are in the obvious places in
// /opt/android-sdk/extras/google/m2repository/com/google/android/gms

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

// For Tango

import com.google.atap.tangoservice.Tango;

// The main activity of the application. This activity shows debug information
// and a glSurfaceView that renders graphic content.
public class RTABMapActivity extends Activity implements OnClickListener, OnItemSelectedListener {

    // Tag for debug logging.
    public static final String TAG = RTABMapActivity.class.getSimpleName();
    public static boolean DISABLE_LOG = true;

    // APPLICATION_ID (think this is a gradle specific thing, so make it a constant here)
    public static final String APPLICATION_ID = "com.introlab.rtabmap";

    // The minimum Tango Core version required from this application.
    private static final int  MIN_TANGO_CORE_VERSION = 9377;

    // The package name of Tang Core, used for checking minimum Tango Core version.
    private static final String TANGO_PACKAGE_NAME = "com.google.tango";

    public static final String EXTRA_KEY_PERMISSIONTYPE = "PERMISSIONTYPE";
    public static final String EXTRA_VALUE_ADF = "ADF_LOAD_SAVE_PERMISSION";
	
    public static final String RTABMAP_TMP_DB = "rtabmap.tmp.db";
    public static final String RTABMAP_TMP_DIR = "tmp";
    public static final String RTABMAP_TMP_FILENAME = "map";
    public static final String RTABMAP_SDCARD_PATH = "/sdcard/";
    public static final String RTABMAP_EXPORT_DIR = "Export/";

    public static final String RTABMAP_AUTH_TOKEN_KEY = "com.introlab.rtabmap.AUTH_TOKEN";
    public static final String RTABMAP_FILENAME_KEY = "com.introlab.rtabmap.FILENAME";
    public static final String RTABMAP_OPENED_DB_PATH_KEY = "com.introlab.rtabmap.OPENED_DB_PATH";
    public static final String RTABMAP_WORKING_DIR_KEY = "com.introlab.rtabmap.WORKING_DIR";
    public static final int SKETCHFAB_ACTIVITY_CODE = 999;
    private String mAuthToken;
	
    public static final long NOTOUCH_TIMEOUT = 5000; // 5 sec
    private boolean mHudVisible = true;
    private int mSavedRenderingType = 0;
    private boolean mMenuOpened = false;

    // UI states
    private static enum State {
	STATE_IDLE,
	STATE_PROCESSING,
	STATE_VISUALIZING,
	STATE_VISUALIZING_WHILE_LOADING
    }
    State mState = State.STATE_IDLE;

    // GLSurfaceView and renderer, all of the graphic content is rendered
    // through OpenGL ES 2.0 in native code.
    private Renderer mRenderer = null;
    private GLSurfaceView mGLView;
	
    View mDecorView;
    int mStatusBarHeight = 0;
    int mActionBarHeight = 0;

    ProgressDialog mProgressDialog;
    ProgressDialog mExportProgressDialog;

    // Screen size for normalizing the touch input for orbiting the render camera.
    private Point mScreenSize = new Point();
    private long mOnPauseStamp = 0;
    private boolean mOnPause = false;
    private Date mDateOnPause = new Date();
    private boolean mBlockBack = true;

    private MenuItem mItemSave;
    private MenuItem mItemOpen;
    private MenuItem mItemPostProcessing;
    private MenuItem mItemExport;
    private MenuItem mItemSettings;
    private MenuItem mItemModes;
    private MenuItem mItemReset;
    private MenuItem mItemLocalizationMode;
    private MenuItem mItemTrajectoryMode;
    private MenuItem mItemRenderingPointCloud;
    private MenuItem mItemRenderingMesh;
    private MenuItem mItemRenderingTextureMesh;
    private MenuItem mItemDataRecorderMode;
    private MenuItem mItemStatusVisibility;
    private MenuItem mItemDebugVisibility;

    private NDSpinner mButtonCameraView;
    private ToggleButton mButtonPause;
    private ToggleButton mButtonLighting;
    private ToggleButton mButtonWireframe;
    private ToggleButton mButtonBackfaceShown;
    private Button mButtonCloseVisualization;
    private Button mButtonSaveOnDevice;
    private Button mButtonShareOnSketchfab;
    private SeekBar mSeekBarFov;
    private SeekBar mSeekBarGrid;

    private String mOpenedDatabasePath = "";
    private String mWorkingDirectory = "";
    private String mWorkingDirectoryHuman = "";

    private String mUpdateRate;
    private String mTimeThr;
    private String mMaxFeatures;
    private String mLoopThr;
    private String mMinInliers;
    private String mMaxOptimizationError;

    private int mTotalLoopClosures = 0;
    private boolean mMapIsEmpty = false;
    int mMapNodes = 0;

    private Toast mToast = null;
	
    private AlertDialog mMemoryWarningDialog = null;
	
    private String[] mStatusTexts = new String[16];
	
    GestureDetector mGesDetect = null;

    /*
     * Variables for location services
     *
     * The location aspect of the activity
     * polls for the first ten seconds of mapping
     * to get a decent GPS reading and then keeps 
     * the last value. Not the best solution,
     * but it will work for this prototype
     *
     */

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     * Exactly how inexact remains to be seen. 
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;

    // UI Widgets.
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mLastUpdateTimeTextView;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    // Labels.
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;

    // Location related functions

    // insert here...

    //Tango Service connection.
    ServiceConnection mTangoServiceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName name, final IBinder service) {
		Thread bindThread = new Thread(new Runnable() {
			public void run() {
			    if(!RTABMapLib.onTangoServiceConnected(service))
				{
				    runOnUiThread(new Runnable() {
					    public void run() {
						mToast.makeText(getApplicationContext(), 
								String.format("Failed to intialize Tango!"), mToast.LENGTH_LONG).show();
					    } 
					});
				}
			}
		    });
		bindThread.start();
	    }

	    public void onServiceDisconnected(ComponentName name) {
		// Handle this if you need to gracefully shutdown/retry
		// in the event that Tango itself crashes/gets upgraded while running.
		mToast.makeText(getApplicationContext(), 
				String.format("Tango disconnected!"), mToast.LENGTH_LONG).show();
	    }
	};

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setTitle(R.string.menu_name);

	// Query screen size, the screen size is used for computing the normalized
	// touch point.
	Display display = getWindowManager().getDefaultDisplay();
	display.getSize(mScreenSize);
	
	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	// Setting content view of this activity.
	setContentView(R.layout.activity_rtabmap);
		
	// Make sure to initialize all default values
	SettingsActivity settings;
		
	mDecorView = getWindow().getDecorView();
	mStatusBarHeight = getStatusBarHeight();
	mActionBarHeight = getActionBarHeight();
	
	// Buttons for selecting camera view and Set up button click listeners.
	mButtonCameraView = (NDSpinner)findViewById(R.id.camera_button);
	mButtonPause = (ToggleButton)findViewById(R.id.pause_button);
	mButtonLighting = (ToggleButton)findViewById(R.id.light_button);
	mButtonWireframe = (ToggleButton)findViewById(R.id.wireframe_button);
	mButtonBackfaceShown = (ToggleButton)findViewById(R.id.backface_button);
	mButtonCloseVisualization = (Button)findViewById(R.id.close_visualization_button);
	mButtonSaveOnDevice = (Button)findViewById(R.id.button_saveOnDevice);
	mButtonShareOnSketchfab = (Button)findViewById(R.id.button_shareToSketchfab);
	mButtonCameraView.setOnItemSelectedListener(this);
	mButtonPause.setOnClickListener(this);
	mButtonLighting.setOnClickListener(this);
	mButtonWireframe.setOnClickListener(this);
	mButtonBackfaceShown.setOnClickListener(this);
	mButtonCloseVisualization.setOnClickListener(this);
	mButtonSaveOnDevice.setOnClickListener(this);
	mButtonShareOnSketchfab.setOnClickListener(this);
	mButtonLighting.setChecked(false);
	mButtonLighting.setVisibility(View.INVISIBLE);
	mButtonWireframe.setChecked(false);
	mButtonWireframe.setVisibility(View.INVISIBLE);
	mButtonCloseVisualization.setVisibility(View.INVISIBLE);
	mButtonSaveOnDevice.setVisibility(View.INVISIBLE);
	mButtonShareOnSketchfab.setVisibility(View.INVISIBLE);

	// Buttons for GPS logging etc
		// Location services widgets, labels, bundle storage and callbacks

	// Locate the UI widgets.
        mStartUpdatesButton = (Button) findViewById(R.id.start_gps_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_gps_button);
        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);

        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

	
	if(mItemRenderingMesh != null && mItemRenderingTextureMesh != null)
	    {
		mButtonBackfaceShown.setVisibility(mItemRenderingMesh.isChecked() || mItemRenderingTextureMesh.isChecked()?View.VISIBLE:View.INVISIBLE);
	    }
		
	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.camera_view_array, android.R.layout.simple_spinner_item);
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	mButtonCameraView.setAdapter(adapter);
	mButtonCameraView.setOnTouchListener(new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		    resetNoTouchTimer();
		    return false;
		}
	    });
		
	mSeekBarFov = (SeekBar)findViewById(R.id.seekBar_fov);
	mSeekBarFov.setMax(45);
	mSeekBarFov.setProgress(20);
	mSeekBarFov.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
		    if(mButtonCameraView.getSelectedItemPosition() == 0)
			{
			    RTABMapLib.setFOV((float)progressValue+45.0f);
			}
		    else if(mButtonCameraView.getSelectedItemPosition() == 3)
			{
			    RTABMapLib.setOrthoCropFactor((float)(120-progressValue)/20.0f - 3.0f);
			}
		    resetNoTouchTimer();
		}
			
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
			
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	    });
		
	mSeekBarGrid = (SeekBar)findViewById(R.id.seekBar_grid);
	mSeekBarGrid.setMax(180);
	mSeekBarGrid.setProgress(90);
	mSeekBarGrid.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
		    RTABMapLib.setGridRotation(((float)progressValue-90.0f)/2.0f);
		    resetNoTouchTimer();
		}
			
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
			
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	    });

	mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

	// OpenGL view where all of the graphics are drawn.
	mGLView = (GLSurfaceView) findViewById(R.id.gl_surface_view);

	mGesDetect = new GestureDetector(this, new DoubleTapGestureDetector());
		
	// Configure OpenGL renderer
	mGLView.setEGLContextClientVersion(2);
	mGLView.setEGLConfigChooser(8, 8, 8, 8, 24, 0);
	mGLView.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	        	
		    resetNoTouchTimer();
	        	
	            mGesDetect.onTouchEvent(event);
	            
	            // Pass the touch event to the native layer for camera control.
		    // Single touch to rotate the camera around the device.
		    // Two fingers to zoom in and out.
		    int pointCount = event.getPointerCount();
		    if (pointCount == 1) {
			float normalizedX = event.getX(0) / mScreenSize.x;
			float normalizedY = event.getY(0) / mScreenSize.y;
			RTABMapLib.onTouchEvent(1, 
	    					event.getActionMasked(), normalizedX, normalizedY, 0.0f, 0.0f);
		    }
		    if (pointCount == 2) {
			if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
			    int index = event.getActionIndex() == 0 ? 1 : 0;
			    float normalizedX = event.getX(index) / mScreenSize.x;
			    float normalizedY = event.getY(index) / mScreenSize.y;
			    RTABMapLib.onTouchEvent(1, 
						    MotionEvent.ACTION_DOWN, normalizedX, normalizedY, 0.0f, 0.0f);
			} else {
			    float normalizedX0 = event.getX(0) / mScreenSize.x;
			    float normalizedY0 = event.getY(0) / mScreenSize.y;
			    float normalizedX1 = event.getX(1) / mScreenSize.x;
			    float normalizedY1 = event.getY(1) / mScreenSize.y;
			    RTABMapLib.onTouchEvent(2, event.getActionMasked(),
						    normalizedX0, normalizedY0, normalizedX1, normalizedY1);
			}
		    }
		    return true;
	        }
	    });
		
	// Configure the OpenGL renderer.
	mRenderer = new Renderer(this);
	mGLView.setRenderer(mRenderer);

	mProgressDialog = new ProgressDialog(this);
	mProgressDialog.setCanceledOnTouchOutside(false);
	mRenderer.setProgressDialog(mProgressDialog);
	mRenderer.setToast(mToast);
	setNavVisibility(true);
		
	mExportProgressDialog = new ProgressDialog(this);
	mExportProgressDialog.setCanceledOnTouchOutside(false);
	mExportProgressDialog.setCancelable(false);
	mExportProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	mExportProgressDialog.setProgressNumberFormat(null);
	mExportProgressDialog.setProgressPercentFormat(null);
	mExportProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
		    RTABMapLib.cancelProcessing();
		}
	    });

	// Check if the Tango Core is out dated.
	if (!CheckTangoCoreVersion(MIN_TANGO_CORE_VERSION)) {
	    mToast.makeText(this, "Tango Core out dated, please update in Play Store", mToast.LENGTH_LONG).show();
	    finish();
	    return;
	}   

	mOpenedDatabasePath = "";
	mWorkingDirectory = "";
	mWorkingDirectoryHuman = "";
	mTotalLoopClosures = 0;

	if(Environment.getExternalStorageState().compareTo(Environment.MEDIA_MOUNTED)==0)
	    {
		File extStore = Environment.getExternalStorageDirectory();
		mWorkingDirectory = extStore.getAbsolutePath() + "/" + getString(R.string.app_name) + "/";
		extStore = new File(mWorkingDirectory);
		extStore.mkdirs();
		mWorkingDirectoryHuman = RTABMAP_SDCARD_PATH + getString(R.string.app_name) + "/";
	    }
	else
	    {
		// show warning that data cannot be saved!
		mToast.makeText(getApplicationContext(), 
				String.format("Failed to get external storage path (SD-CARD, state=%s). Saving disabled.", 
					      Environment.getExternalStorageState()), mToast.LENGTH_LONG).show();
	    }

	RTABMapLib.onCreate(this);
	String tmpDatabase = mWorkingDirectory+RTABMAP_TMP_DB;
	(new File(tmpDatabase)).delete();
	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	boolean databaseInMemory = sharedPref.getBoolean(getString(R.string.pref_key_db_in_memory), Boolean.parseBoolean(getString(R.string.pref_default_db_in_memory)));
	RTABMapLib.openDatabase(tmpDatabase, databaseInMemory, false);

	DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (displayManager != null) {
            displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
		    @Override
		    public void onDisplayAdded(int displayId) {

		    }

		    @Override
		    public void onDisplayChanged(int displayId) {
			synchronized (this) {
			    setAndroidOrientation();
			    Display display = getWindowManager().getDefaultDisplay();
			    display.getSize(mScreenSize);
			}
		    }

		    @Override
		    public void onDisplayRemoved(int displayId) {}
		}, null);
        }

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
	Log.v(TAG, "Location methods called in onCreate()!");
                
       	DISABLE_LOG =  !( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
    }

    // Location related functions (adapted from LocationUpdates-app in the Android Studio samples repo)
    // https://github.com/googlesamples/android-play-location/tree/master/LocationUpdates

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateGPSUI();
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
	Log.i(TAG, "buildLocationSettingsRequest over!");
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateGPSUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(RTABMapActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(RTABMapActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        updateGPSUI();
                    }
                });
    }

    /**
     * Updates all UI fields.
     */
    private void updateGPSUI() {
        setButtonsEnabledState();
        updateLocationUI();
    }

    /**
     * Disables both buttons when functionality is disabled due to insuffucient location settings.
     * Otherwise ensures that only one button is enabled at any time. The Start Updates button is
     * enabled if the user is not requesting location updates. The Stop Updates button is enabled
     * if the user is requesting location updates.
     */
    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            mLatitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", mLatitudeLabel,
                    mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", mLongitudeLabel,
                    mCurrentLocation.getLongitude()));
            mLastUpdateTimeTextView.setText(String.format(Locale.ENGLISH, "%s: %s",
                    mLastUpdateTimeLabel, mLastUpdateTime));
        } else {
	    Log.i(TAG, "mCurrentLocation is NULL!");
	}
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                        setButtonsEnabledState();
                    }
                });
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(RTABMapActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(RTABMapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    // This was @Overridden, but javac was complaining that it wasn't overriding anything
    //@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.
		Log.w(TAG, "WARNING: Location permission denied!");

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                //Uri uri = Uri.fromParts("package",
                                //        BuildConfig.APPLICATION_ID, null);
				Uri uri = Uri.fromParts("package",
                                        APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    public int getStatusBarHeight() {
	int result = 0;
	int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	if (resourceId > 0) {
	    result = getResources().getDimensionPixelSize(resourceId);
	}
	return result;
    }
    public int getActionBarHeight() {
	int result = 0;
	TypedValue tv = new TypedValue();
	if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
	    {
		result = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
	    }

	return result;
    }
	
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

	super.onWindowFocusChanged(hasFocus);

	if(!mHudVisible)
	    {
		mRenderer.setOffset(!hasFocus?-mStatusBarHeight:0);
	    }
    }
		
    // This snippet hides the system bars.
    private void setNavVisibility(boolean visible) {
	int newVis = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (!visible) {
            newVis |= View.SYSTEM_UI_FLAG_LOW_PROFILE 
		| View.SYSTEM_UI_FLAG_FULLSCREEN       // hide status bar
		| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  // hide nav bar
		| View.SYSTEM_UI_FLAG_IMMERSIVE; 
            mRenderer.setOffset(!hasWindowFocus()?-mStatusBarHeight:0);
        }
        else
	    {
        	mRenderer.setOffset(-mStatusBarHeight-mActionBarHeight);
	    }

        // Set the new desired visibility.
        mDecorView.setSystemUiVisibility(newVis);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// Check which request we're responding to
	if (requestCode == Tango.TANGO_INTENT_ACTIVITYCODE) {
	    // Make sure the request was successful
	    if (resultCode == RESULT_CANCELED) {
		mToast.makeText(this, "Motion Tracking Permissions Required!", mToast.LENGTH_SHORT).show();
		finish();
	    }
	}
	else if (requestCode == SKETCHFAB_ACTIVITY_CODE) {
	    // Make sure the request was successful
	    if (resultCode == RESULT_OK) {
		mAuthToken = data.getStringExtra(RTABMAP_AUTH_TOKEN_KEY);
	    }
	}
       	else if (requestCode == REQUEST_CHECK_SETTINGS) {
	    // Check for the integer request code originally supplied to startResolutionForResult().
	    switch (resultCode) {
	    case Activity.RESULT_OK:
		Log.i(TAG, "User agreed to make required location settings changes.");
		// Nothing to do. startLocationupdates() gets called in onResume again.
		break;
	    case Activity.RESULT_CANCELED:
		Log.i(TAG, "User chose not to make required location settings changes.");
		mRequestingLocationUpdates = false;
		updateGPSUI();
		break;
	    }
        }
    }
	
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
	mMenuOpened = true;
	return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
	mMenuOpened = false;
    }
	
    @Override
    public void onBackPressed() {
	   
	if(mBlockBack)
	    {
		mToast.makeText(this, "Press Back once more to exit", mToast.LENGTH_LONG).show();
		mBlockBack = false;
	    }
	else
	    {
		super.onBackPressed();
	    }
    }
	
    @Override
    protected void onPause() {
	super.onPause();

	// Remove location updates to save battery.
        stopLocationUpdates();
	
	stopDisconnectTimer();
		
	if(!DISABLE_LOG) Log.i(TAG, "onPause()");
	mOnPause = true;
		
	// This deletes OpenGL context!
	mGLView.onPause();

	RTABMapLib.onPause();

	unbindService(mTangoServiceConnection);

	if(!mButtonPause.isChecked())
	    {
		mButtonPause.setChecked(true);
		pauseMapping();
	    }
		
	mOnPauseStamp = System.currentTimeMillis()/1000;
    }

    @Override
    protected void onResume() {
	super.onResume();

	// Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

        updateGPSUI();
		
	mProgressDialog.setTitle("");
	if(mOnPause)
	    {
		if(System.currentTimeMillis()/1000 - mOnPauseStamp < 1)
		    {
			mProgressDialog.setMessage(String.format("RTAB-Map has been interrupted by another application, Tango should be re-initialized! Set your phone/tablet in Airplane mode if this happens often."));
		    }
		else
		    {
			mProgressDialog.setMessage(String.format("Hold Tight! Initializing Tango Service..."));
		    }
		mToast.makeText(this, "Mapping is paused!", mToast.LENGTH_LONG).show();
	    }
	else
	    {
		mProgressDialog.setMessage(String.format("Hold Tight! Initializing Tango Service...\nTip: If the camera is still drifting just after the mapping has started, do \"Reset\"."));
	    }
	mProgressDialog.show();
	mOnPause = false;
		
	setAndroidOrientation();

	// update preferences
	try
	    {
		if(!DISABLE_LOG) Log.d(TAG, "update preferences...");
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		mUpdateRate = sharedPref.getString(getString(R.string.pref_key_update_rate), getString(R.string.pref_default_update_rate));
		mTimeThr = sharedPref.getString(getString(R.string.pref_key_time_thr), getString(R.string.pref_default_time_thr));
		String memThr = sharedPref.getString(getString(R.string.pref_key_mem_thr), getString(R.string.pref_default_mem_thr));
		mLoopThr = sharedPref.getString(getString(R.string.pref_key_loop_thr), getString(R.string.pref_default_loop_thr));
		String simThr = sharedPref.getString(getString(R.string.pref_key_sim_thr), getString(R.string.pref_default_sim_thr));
		mMinInliers = sharedPref.getString(getString(R.string.pref_key_min_inliers), getString(R.string.pref_default_min_inliers));
		mMaxOptimizationError = sharedPref.getString(getString(R.string.pref_key_opt_error), getString(R.string.pref_default_opt_error));
		mMaxFeatures = sharedPref.getString(getString(R.string.pref_key_features_voc), getString(R.string.pref_default_features_voc));
		String maxFeaturesLoop = sharedPref.getString(getString(R.string.pref_key_features), getString(R.string.pref_default_features));
		String featureType = sharedPref.getString(getString(R.string.pref_key_features_type), getString(R.string.pref_default_features_type));
		boolean keepAllDb = sharedPref.getBoolean(getString(R.string.pref_key_keep_all_db), Boolean.parseBoolean(getString(R.string.pref_default_keep_all_db)));
		boolean optimizeFromGraphEnd = sharedPref.getBoolean(getString(R.string.pref_key_optimize_end), Boolean.parseBoolean(getString(R.string.pref_default_optimize_end)));
		String optimizer = sharedPref.getString(getString(R.string.pref_key_optimizer), getString(R.string.pref_default_optimizer));
			
		if(!DISABLE_LOG) Log.d(TAG, "set mapping parameters");
		RTABMapLib.setOnlineBlending(sharedPref.getBoolean(getString(R.string.pref_key_blending), Boolean.parseBoolean(getString(R.string.pref_default_blending))));
		RTABMapLib.setNodesFiltering(sharedPref.getBoolean(getString(R.string.pref_key_nodes_filtering), Boolean.parseBoolean(getString(R.string.pref_default_nodes_filtering))));
		RTABMapLib.setRawScanSaved(sharedPref.getBoolean(getString(R.string.pref_key_raw_scan_saved), Boolean.parseBoolean(getString(R.string.pref_default_raw_scan_saved))));
		RTABMapLib.setFullResolution(sharedPref.getBoolean(getString(R.string.pref_key_resolution), Boolean.parseBoolean(getString(R.string.pref_default_resolution))));
		RTABMapLib.setSmoothing(sharedPref.getBoolean(getString(R.string.pref_key_smoothing), Boolean.parseBoolean(getString(R.string.pref_default_smoothing))));
		RTABMapLib.setCameraColor(!sharedPref.getBoolean(getString(R.string.pref_key_fisheye), Boolean.parseBoolean(getString(R.string.pref_default_fisheye))));
		RTABMapLib.setAppendMode(sharedPref.getBoolean(getString(R.string.pref_key_append), Boolean.parseBoolean(getString(R.string.pref_default_append))));
		RTABMapLib.setMappingParameter("Rtabmap/DetectionRate", mUpdateRate);
		RTABMapLib.setMappingParameter("Rtabmap/TimeThr", mTimeThr);
		RTABMapLib.setMappingParameter("Rtabmap/MemoryThr", memThr);
		RTABMapLib.setMappingParameter("Mem/RehearsalSimilarity", simThr);
		RTABMapLib.setMappingParameter("Kp/MaxFeatures", mMaxFeatures);
		RTABMapLib.setMappingParameter("Vis/MaxFeatures", maxFeaturesLoop);
		RTABMapLib.setMappingParameter("Vis/MinInliers", mMinInliers);
		RTABMapLib.setMappingParameter("Rtabmap/LoopThr", mLoopThr);
		RTABMapLib.setMappingParameter("RGBD/OptimizeMaxError", mMaxOptimizationError);
		RTABMapLib.setMappingParameter("Kp/DetectorStrategy", featureType);
		RTABMapLib.setMappingParameter("Vis/FeatureType", featureType);
		RTABMapLib.setMappingParameter("Mem/NotLinkedNodesKept", String.valueOf(keepAllDb));
		RTABMapLib.setMappingParameter("RGBD/OptimizeFromGraphEnd", String.valueOf(optimizeFromGraphEnd));
		RTABMapLib.setMappingParameter("Optimizer/Strategy", optimizer);
	
		if(!DISABLE_LOG) Log.d(TAG, "set exporting parameters...");
		RTABMapLib.setCloudDensityLevel(Integer.parseInt(sharedPref.getString(getString(R.string.pref_key_density), getString(R.string.pref_default_density))));
		RTABMapLib.setMaxCloudDepth(Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_depth), getString(R.string.pref_default_depth))));
		RTABMapLib.setMinCloudDepth(Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_min_depth), getString(R.string.pref_default_min_depth))));
		RTABMapLib.setPointSize(Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_point_size), getString(R.string.pref_default_point_size))));
		RTABMapLib.setMeshAngleTolerance(Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_angle), getString(R.string.pref_default_angle))));
		RTABMapLib.setMeshTriangleSize(Integer.parseInt(sharedPref.getString(getString(R.string.pref_key_triangle), getString(R.string.pref_default_triangle))));
		float bgColor = Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_background_color), getString(R.string.pref_default_background_color)));
		RTABMapLib.setBackgroundColor(bgColor);
		mRenderer.setTextColor(bgColor>=0.6f?0.0f:1.0f);
			
		if(!DISABLE_LOG) Log.d(TAG, "set rendering parameters...");
		RTABMapLib.setClusterRatio(Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_cluster_ratio), getString(R.string.pref_default_cluster_ratio))));
		RTABMapLib.setMaxGainRadius(Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_gain_max_radius), getString(R.string.pref_default_gain_max_radius))));
		RTABMapLib.setRenderingTextureDecimation(Integer.parseInt(sharedPref.getString(getString(R.string.pref_key_rendering_texture_decimation), getString(R.string.pref_default_rendering_texture_decimation))));
	
		if(mItemRenderingPointCloud != null)
		    {
			int renderingType =  sharedPref.getInt(getString(R.string.pref_key_rendering), Integer.parseInt(getString(R.string.pref_default_rendering)));
			if(renderingType == 0)
			    {
				mItemRenderingPointCloud.setChecked(true);
			    }
			else if(renderingType == 1)
			    {
				mItemRenderingMesh.setChecked(true);
			    }
			else
			    {
				mItemRenderingTextureMesh.setChecked(true);
			    }
			RTABMapLib.setMeshRendering(
						    mItemRenderingMesh.isChecked() || mItemRenderingTextureMesh.isChecked(), 
						    mItemRenderingTextureMesh.isChecked());
				
			mButtonBackfaceShown.setVisibility(mItemRenderingMesh.isChecked() || mItemRenderingTextureMesh.isChecked()?View.VISIBLE:View.INVISIBLE);
		    }
	    }
	catch(Exception e)
	    {
		Log.e(TAG, "Error parsing preferences: " + e.getMessage());
		mToast.makeText(this, String.format("Error parsing preferences: "+e.getMessage()), mToast.LENGTH_LONG).show();
	    }

	if(!DISABLE_LOG) Log.i(TAG, String.format("onResume()"));

	if (Tango.hasPermission(this, Tango.PERMISSIONTYPE_MOTION_TRACKING)) {

	    mGLView.onResume();

	} else {
	    if(!DISABLE_LOG) Log.i(TAG, String.format("Asking for motion tracking permission"));
	    startActivityForResult(
				   Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_MOTION_TRACKING),
				   Tango.TANGO_INTENT_ACTIVITYCODE);
	}
		
	TangoInitializationHelper.bindTangoService(getActivity(), mTangoServiceConnection);
	resetNoTouchTimer();
    }
	
    private void setCamera(int type)
    {			
	if(!DISABLE_LOG) Log.i(TAG, String.format("called setCamera(type=%d);", type));
		
	// for convenience, for a refresh of the memory used
	mStatusTexts[1] = getString(R.string.memory)+String.valueOf(Debug.getNativeHeapAllocatedSize()/(1024*1024));
	mStatusTexts[2] = getString(R.string.free_memory)+String.valueOf(getFreeMemory());
	updateStatusTexts();
				
	RTABMapLib.setCamera(type);
	mButtonCameraView.setSelection(type, true);
	mSeekBarFov.setVisibility(type!=0 && type!=3?View.INVISIBLE:View.VISIBLE);
	mSeekBarGrid.setVisibility(mSeekBarGrid.isEnabled() && type==3?View.VISIBLE:View.INVISIBLE);
	if(type==0)
	    {
		mSeekBarFov.setMax(45);
		mSeekBarFov.setProgress(20);
	    }
	if(type==3)
	    {
		mSeekBarFov.setMax(120);
		mSeekBarFov.setProgress(80);
	    }
    }

    /********************* BUTTON HANDLING ********************/

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            startLocationUpdates();
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    public void stopUpdatesButtonHandler(View view) {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
    }

    @Override
    public void onClick(View v) {
	// Handle button clicks.
	//
	// Includes the buttons to start and stop the GPS tracking
	// When GPS tracking is stopped, mCurrentLocation should
	// hold the last known location
	
	switch (v.getId()) {
	case R.id.pause_button:
	    pauseMapping();
	    break;
	case R.id.light_button:
	    RTABMapLib.setLighting(mButtonLighting.isChecked());
	    break;
	case R.id.backface_button:
	    RTABMapLib.setBackfaceCulling(!mButtonBackfaceShown.isChecked());
	    break;
	case R.id.wireframe_button:
	    RTABMapLib.setWireframe(mButtonWireframe.isChecked());
	    break;
	case R.id.close_visualization_button:
	    if(mSavedRenderingType==0)
		{
		    mItemRenderingPointCloud.setChecked(true);
		}
	    else if(mSavedRenderingType==1)
		{
		    mItemRenderingMesh.setChecked(true);
		}
	    else 
		{
		    mItemRenderingTextureMesh.setChecked(true);
		}
	    updateState(State.STATE_IDLE);
	    RTABMapLib.postExportation(false);
	    break;
	case R.id.button_saveOnDevice:
	    saveOnDevice();
	    break;
	case R.id.button_shareToSketchfab:
	    shareToSketchfab();
	    break;
	default:
	    return;
	}
	resetNoTouchTimer();
    }
	
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	setCamera(pos);
	resetNoTouchTimer();
    }

    public void onNothingSelected(AdapterView<?> parent) {
    	resetNoTouchTimer();
    }
		
    private void setAndroidOrientation() {
        Display display = getWindowManager().getDefaultDisplay();
        Camera.CameraInfo colorCameraInfo = new Camera.CameraInfo();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fisheye = sharedPref.getBoolean(getString(R.string.pref_key_fisheye), Boolean.parseBoolean(getString(R.string.pref_default_fisheye)));
        Camera.getCameraInfo(fisheye?1:0, colorCameraInfo);
        RTABMapLib.setScreenRotation(display.getRotation(), colorCameraInfo.orientation);
    }
	
    class DoubleTapGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
	    float normalizedX = event.getX(0) / mScreenSize.x;
	    float normalizedY = event.getY(0) / mScreenSize.y;
	    RTABMapLib.onTouchEvent(3, event.getActionMasked(), normalizedX, normalizedY, 0.0f, 0.0f);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	if(!DISABLE_LOG) Log.i(TAG, "called onCreateOptionsMenu;");

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.optionmenu, menu);
		
	getActionBar().setDisplayShowHomeEnabled(true);
	getActionBar().setIcon(R.drawable.ic_launcher);

	mItemSave = menu.findItem(R.id.save);
	mItemOpen = menu.findItem(R.id.open);
	mItemPostProcessing = menu.findItem(R.id.post_processing);
	mItemExport = menu.findItem(R.id.export);
	mItemSettings = menu.findItem(R.id.settings);
	mItemModes = menu.findItem(R.id.modes);
	mItemReset = menu.findItem(R.id.reset);
	mItemLocalizationMode = menu.findItem(R.id.localization_mode);
	mItemTrajectoryMode = menu.findItem(R.id.trajectory_mode);
	mItemRenderingPointCloud = menu.findItem(R.id.point_cloud);
	mItemRenderingMesh = menu.findItem(R.id.mesh);
	mItemRenderingTextureMesh = menu.findItem(R.id.texture_mesh);
	mItemDataRecorderMode = menu.findItem(R.id.data_recorder);
	mItemStatusVisibility = menu.findItem(R.id.status);
	mItemDebugVisibility = menu.findItem(R.id.debug);
	mItemSave.setEnabled(false);
	mItemExport.setEnabled(false);
	mItemOpen.setEnabled(false);
	mItemPostProcessing.setEnabled(false);
	mItemDataRecorderMode.setEnabled(false);

	try
	    {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int renderingType =  sharedPref.getInt(getString(R.string.pref_key_rendering), Integer.parseInt(getString(R.string.pref_default_rendering)));
		if(renderingType == 0)
		    {
			mItemRenderingPointCloud.setChecked(true);
		    }
		else if(renderingType == 1)
		    {
			mItemRenderingMesh.setChecked(true);
		    }
		else
		    {
			mItemRenderingTextureMesh.setChecked(true);
		    }
		RTABMapLib.setMeshRendering(
					    mItemRenderingMesh.isChecked() || mItemRenderingTextureMesh.isChecked(), 
					    mItemRenderingTextureMesh.isChecked());
			
		if(mButtonBackfaceShown != null)
		    {
			mButtonBackfaceShown.setVisibility(mItemRenderingMesh.isChecked() || mItemRenderingTextureMesh.isChecked()?View.VISIBLE:View.INVISIBLE);
		    }
	    }
	catch(Exception e)
	    {
		Log.e(TAG, "Error parsing rendering preferences: " + e.getMessage());
		mToast.makeText(this, String.format("Error parsing rendering preferences: "+e.getMessage()), mToast.LENGTH_LONG).show();
	    }

	updateState(mState);

	return true;
    }
	
    private long getFreeMemory()
    {
	MemoryInfo mi = new MemoryInfo();
	ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	activityManager.getMemoryInfo(mi);
	return mi.availMem / 0x100000L; // MB
    }
	
    private void updateStatusTexts()
    {
	if(mItemStatusVisibility != null && mItemDebugVisibility != null)
	    {
		if((mItemStatusVisibility.isChecked() || mState == State.STATE_VISUALIZING_WHILE_LOADING) && mItemDebugVisibility.isChecked())
		    {
			mRenderer.updateTexts(mStatusTexts);
		    }
		else if((mItemStatusVisibility.isChecked() || mState == State.STATE_VISUALIZING_WHILE_LOADING))
		    {
			mRenderer.updateTexts(Arrays.copyOfRange(mStatusTexts, 0, 3));
		    }
		else if(mItemDebugVisibility.isChecked())
		    {
			mRenderer.updateTexts(Arrays.copyOfRange(mStatusTexts, 4, mStatusTexts.length));
		    }
		else
		    {
			mRenderer.updateTexts(null);
		    }
	    }
    }

    private void updateStatsUI(
			       int processMemoryUsed,
			       int loopClosureId,
			       int inliers,
			       int matches,
			       int rejected,
			       float optimizationMaxError,
			       String[] statusTexts)
    {
	mStatusTexts = statusTexts;
	updateStatusTexts();

	if(mButtonPause!=null)
	    {
		if(!mButtonPause.isChecked())
		    {	
			//check if we are low in memory
			long memoryUsed = processMemoryUsed;
			long memoryFree = getFreeMemory();
				
			if(memoryFree < 200)
			    {
				mButtonPause.setChecked(true);
				pauseMapping();
					
				if(mMemoryWarningDialog!=null)
				    {
					mMemoryWarningDialog.dismiss();
					mMemoryWarningDialog = null;
				    }
					
				mMemoryWarningDialog = new AlertDialog.Builder(getActivity())
				    .setTitle("Memory is full!")
				    .setCancelable(false)
				    .setMessage(String.format("Scanning has been paused because free memory is too "
							      + "low (%d MB). You should be able to save the database but some post-processing and exporting options may fail. "
							      + "\n\nNote that for large environments, you can save multiple databases and "
							      + "merge them with RTAB-Map Desktop version.", memoryUsed))
				    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
						mMemoryWarningDialog = null;
					    }
					})
				    .setNeutralButton("Save", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
						saveOnDevice();
						mMemoryWarningDialog = null;
					    }
					})
				    .create();
				mMemoryWarningDialog.show();
			    }
			else if(mMemoryWarningDialog == null && memoryUsed*3 > memoryFree && (mItemDataRecorderMode == null || !mItemDataRecorderMode.isChecked()))
			    {
				mMemoryWarningDialog = new AlertDialog.Builder(getActivity())
				    .setTitle("Warning: Memory is almost full!")
				    .setCancelable(false)
				    .setMessage(String.format("Free memory (%d MB) should be at least 3 times the "
							      + "memory used (%d MB) so that some post-processing and exporting options "
							      + "have enough memory to work correctly. If you just want to save the database "
							      + "after scanning, you can continue until the next warning.\n\n"
							      + "Note that showing only point clouds reduces memory needed for rendering.", memoryFree, memoryUsed))
				    .setPositiveButton("Pause", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
						mButtonPause.setChecked(true);
						pauseMapping();
					    }
					})
				    .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
					    }
					})
				    .create();
				mMemoryWarningDialog.show();
			    }
		    }
	    }

		
	if(mButtonPause!=null && !mButtonPause.isChecked())
	    {
		if(loopClosureId > 0)
		    {
			mToast.setText(String.format("Loop closure detected! (%d/%d inliers)", inliers, matches));
			mToast.show();
		    }
		else if(rejected > 0)
		    {
			if(inliers >= Integer.parseInt(mMinInliers))
			    {
				if(optimizationMaxError > 0.0f)
				    {
					mToast.setText(String.format("Loop closure rejected, too high graph optimization error (%.3fm > %sm).", optimizationMaxError, mMaxOptimizationError));
				    }
				else
				    {
					mToast.setText(String.format("Loop closure rejected, graph optimization failed! You may try a different Graph Optimizer (see Mapping options)."));
				    }
			    }
			else
			    {
				mToast.setText(String.format("Loop closure rejected, not enough inliers (%d/%d < %s).", inliers, matches, mMinInliers));
			    }
			mToast.show();
		    }
	    }
    }

    // called from jni
    public void updateStatsCallback(
				    final int nodes, 
				    final int words, 
				    final int points, 
				    final int polygons,
				    final float updateTime, 
				    final int loopClosureId,
				    final int highestHypId,
				    final int processMemoryUsed,
				    final int databaseMemoryUsed,
				    final int inliers,
				    final int matches,
				    final int featuresExtracted,
				    final float hypothesis,
				    final int nodesDrawn,
				    final float fps,
				    final int rejected,
				    final float rehearsalValue,
				    final float optimizationMaxError)
    {
	if(!DISABLE_LOG) Log.i(TAG, String.format("updateStatsCallback()"));

	final String[] statusTexts = new String[16];
	if(mButtonPause!=null && !mButtonPause.isChecked())
	    {
		String updateValue = mUpdateRate.compareTo("0")==0?"Max":mUpdateRate;
		statusTexts[0] = getString(R.string.status)+(mItemDataRecorderMode!=null&&mItemDataRecorderMode.isChecked()?String.format("Recording (%s Hz)", updateValue):mItemLocalizationMode!=null && mItemLocalizationMode.isChecked()?String.format("Localization (%s Hz)", updateValue):String.format("Mapping (%s Hz)", updateValue));
	    }
	else
	    {
		statusTexts[0] = mStatusTexts[0];
	    }
		
	// getNativeHeapAllocatedSize() is too slow, so we need to use the estimate.
	// Multiply by 3/2 to match getNativeHeapAllocatedSize()
	final int adjustedMemoryUsed = (processMemoryUsed*3)/2;
		
	if(mButtonPause!=null)
	    {
		if(!mButtonPause.isChecked())
		    {
			statusTexts[1] = getString(R.string.memory)+adjustedMemoryUsed; 
		    }
		else if(mState == State.STATE_PROCESSING)
		    {
			// This request is long to do, only do it when processing.
			statusTexts[1] = getString(R.string.memory)+String.valueOf(Debug.getNativeHeapAllocatedSize()/(1024*1024));
		    }
		else
		    {
			statusTexts[1] = mStatusTexts[1];
		    }
	    }
	else
	    {
		statusTexts[1] = mStatusTexts[1];
	    }
	statusTexts[2] = getString(R.string.free_memory)+getFreeMemory();
	
		
	if(loopClosureId > 0)
	    {
		++mTotalLoopClosures;
	    }
		
	mMapNodes = nodes;
		
	int index = 4;
	statusTexts[index++] = getString(R.string.nodes)+nodes+" (" + nodesDrawn + " shown)";
	statusTexts[index++] = getString(R.string.words)+words;
	statusTexts[index++] = getString(R.string.database_size)+databaseMemoryUsed;
	statusTexts[index++] = getString(R.string.points)+points;
	statusTexts[index++] = getString(R.string.polygons)+polygons;
	statusTexts[index++] = getString(R.string.update_time)+(int)(updateTime) + " / " + (mTimeThr.compareTo("0")==0?"No Limit":mTimeThr);
	statusTexts[index++] = getString(R.string.features)+featuresExtracted +" / " + (mMaxFeatures.compareTo("0")==0?"No Limit":mMaxFeatures.compareTo("-1")==0?"Disabled":mMaxFeatures);
	statusTexts[index++] = getString(R.string.rehearsal)+(int)(rehearsalValue*100.0f);
	statusTexts[index++] = getString(R.string.total_loop)+mTotalLoopClosures;
	statusTexts[index++] = getString(R.string.inliers)+inliers;
	statusTexts[index++] = getString(R.string.hypothesis)+(int)(hypothesis*100.0f) +" / " + (int)(Float.parseFloat(mLoopThr)*100.0f) + " (" + (loopClosureId>0?loopClosureId:highestHypId)+")";
	statusTexts[index++] = getString(R.string.fps)+(int)fps+" Hz";
	
	runOnUiThread(new Runnable() {
		public void run() {
		    updateStatsUI(adjustedMemoryUsed, loopClosureId, inliers, matches, rejected, optimizationMaxError, statusTexts);
		} 
	    });
    }

    private void rtabmapInitEventUI(
				    int status, 
				    String msg)
    {
	if(!DISABLE_LOG) Log.i(TAG, String.format("rtabmapInitEventsUI() status=%d msg=%s", status, msg));

	int optimizedMeshDetected = 0;
	
	if(msg.equals("Loading optimized cloud...done!"))
	    {
		optimizedMeshDetected = 1;
	    }
	else if(msg.equals("Loading optimized mesh...done!"))
	    {
		optimizedMeshDetected = 2;
	    }
	else if(msg.equals("Loading optimized texture mesh...done!"))
	    {
		optimizedMeshDetected = 3;
	    }
	if(optimizedMeshDetected > 0)
	    {
		resetNoTouchTimer();
		mSavedRenderingType = mItemRenderingPointCloud.isChecked()?0:mItemRenderingMesh.isChecked()?1:2;
		if(optimizedMeshDetected==1)
		    {
			mItemRenderingPointCloud.setChecked(true);
		    }
		else if(optimizedMeshDetected==2)
		    {
			mItemRenderingMesh.setChecked(true);
		    }
		else // isOBJ
		    {
			mItemRenderingTextureMesh.setChecked(true);
		    }
			
		updateState(State.STATE_VISUALIZING_WHILE_LOADING);
		if(mButtonCameraView.getSelectedItemPosition() == 0)
		    {
			setCamera(2);
		    }
		mToast.makeText(getActivity(), String.format("Optimized mesh detected in the database, it is shown while the database is loading..."), mToast.LENGTH_LONG).show();
		mProgressDialog.dismiss();
	    }
		
	if(mButtonPause!=null)
	    {
		if(mButtonPause.isChecked())
		    {
			mStatusTexts[0] = getString(R.string.status)+(status == 1 && msg.isEmpty()?"Paused":msg);
		    }
		else if(mItemLocalizationMode!=null && mItemDataRecorderMode!=null)
		    {
			mStatusTexts[0] = getString(R.string.status)+(status == 1 && msg.isEmpty()?(mItemDataRecorderMode!=null&&mItemDataRecorderMode.isChecked()?"Recording":mItemLocalizationMode!=null&&mItemLocalizationMode.isChecked()?"Localization":"Mapping"):msg);
		    }
			
		mStatusTexts[1] = getString(R.string.memory)+String.valueOf(Debug.getNativeHeapAllocatedSize()/(1024*1024));
		mStatusTexts[2] = getString(R.string.free_memory)+String.valueOf(getFreeMemory());
		updateStatusTexts();
	    }
    }

    //called from jni
    public void rtabmapInitEventCallback(
					 final int status, 
					 final String msg)
    {
	if(!DISABLE_LOG) Log.i(TAG, String.format("rtabmapInitEventCallback()"));

	runOnUiThread(new Runnable() {
		public void run() {
		    rtabmapInitEventUI(status, msg);
		} 
	    });
    }
	
    private void updateProgressionUI(
				     int count, 
				     int max)
    {
	if(!DISABLE_LOG) Log.i(TAG, String.format("updateProgressionUI() count=%d max=%s", count, max));

	mExportProgressDialog.setMax(max);
	mExportProgressDialog.setProgress(count);
    }

    //called from jni
    public void updateProgressionCallback(
					  final int count, 
					  final int max)
    {
	if(!DISABLE_LOG) Log.i(TAG, String.format("updateProgressionCallback()"));

	runOnUiThread(new Runnable() {
		public void run() {
		    updateProgressionUI(count, max);
		} 
	    });
    }

    private void tangoEventUI(
			      int type, 
			      String key,
			      String value)
    {
	/**
	 * 
	 * "TangoServiceException:X" - The service has encountered an exception, and a text description is given in X.
	 * "FisheyeOverExposed:X" - the fisheye image is over exposed with average pixel value X px.
	 * "FisheyeUnderExposed:X" - the fisheye image is under exposed with average pixel value X px.
	 * "ColorOverExposed:X" - the color image is over exposed with average pixel value X px.
	 * "ColorUnderExposed:X" - the color image is under exposed with average pixel value X px.
	 * "TooFewFeaturesTracked:X" - too few features were tracked in the fisheye image. The number of features tracked is X.
	 * "AreaDescriptionSaveProgress:X" - ADF saving is X * 100 percent complete.
	 * "Unknown"
	 */
	String str = null;
	if(key.equals("TangoServiceException"))
	    str = String.format("Tango service exception: %s", value);
	else if(key.equals("FisheyeOverExposed"))
	    ;//str = String.format("The fisheye image is over exposed with average pixel value %s px.", value);
	else if(key.equals("FisheyeUnderExposed"))
	    ;//str = String.format("The fisheye image is under exposed with average pixel value %s px.", value);
	else if(key.equals("ColorOverExposed")) 
	    ;//str = String.format("The color image is over exposed with average pixel value %s px.", value);
	else if(key.equals("ColorUnderExposed")) 
	    ;//str = String.format("The color image is under exposed with average pixel value %s px.", value);
	else if(key.equals("CameraTango")) 
	    str = value;
	else if(key.equals("TooFewFeaturesTracked")) 
	    {
		if(!value.equals("0"))
		    {
			str = String.format("Too few features (%s) were tracked in the fisheye image. This may result in poor odometry!", value);
		    }
	    }
	else	
	    {
		str = String.format("Unknown Tango event detected!? (type=%d)", type);
	    }
	if(str!=null)
	    {
		mToast.setText(str);
		mToast.show();
	    }
    }

    //called from jni
    public void tangoEventCallback(
				   final int type, 
				   final String key,
				   final String value)
    {
	if(mButtonPause != null && !mButtonPause.isChecked())
	    {
		runOnUiThread(new Runnable() {
			public void run() {
			    tangoEventUI(type, key, value);
			} 
		    });
	    }
    }

    private boolean CheckTangoCoreVersion(int minVersion) {
	int versionNumber = 0;
	String packageName = TANGO_PACKAGE_NAME;
	try {
	    PackageInfo pi = getApplicationContext().getPackageManager().getPackageInfo(packageName,
											PackageManager.GET_META_DATA);
	    versionNumber = pi.versionCode;
	} catch (NameNotFoundException e) {
	    e.printStackTrace();
	}
	return (minVersion <= versionNumber);
    }

    private RTABMapActivity getActivity() {return this;}

    private void standardOptimization() {
	mExportProgressDialog.setTitle("Post-Processing");
	mExportProgressDialog.setMessage(String.format("Please wait while optimizing..."));
	mExportProgressDialog.setProgress(0);
	mExportProgressDialog.show();
		
	updateState(State.STATE_PROCESSING);
	Thread workingThread = new Thread(new Runnable() {
		public void run() {
		    final int loopDetected = RTABMapLib.postProcessing(-1);
		    runOnUiThread(new Runnable() {
			    public void run() {
				if(mExportProgressDialog.isShowing())
				    {
					mExportProgressDialog.dismiss();
					if(loopDetected >= 0)
					    {
						mTotalLoopClosures+=loopDetected;
						mProgressDialog.setTitle("Post-Processing");
						mProgressDialog.setMessage(String.format("Optimization done! Increasing visual appeal..."));
						mProgressDialog.show();
					    }
					else if(loopDetected < 0)
					    {
						mToast.makeText(getActivity(), String.format("Optimization failed!"), mToast.LENGTH_LONG).show();
					    }
				    }
				else
				    {
					mToast.makeText(getActivity(), String.format("Optimization canceled"), mToast.LENGTH_LONG).show();
				    }
				updateState(State.STATE_IDLE);
			    }
			});
		} 
	    });
	workingThread.start();
    }
	
    private Handler notouchHandler = new Handler(){
	    public void handleMessage(Message msg) {
	    }
	};

    private Runnable notouchCallback = new Runnable() {
	    @Override
	    public void run() {
        	if(!mProgressDialog.isShowing() && !mMenuOpened)
		    {
	        	setNavVisibility(false);
	        	mHudVisible = false;
			updateState(mState);
		    }
        	else
		    {
        		resetNoTouchTimer();
		    }
	    }
	};

    public void resetNoTouchTimer(){
    	if(!mHudVisible)
	    {
    		setNavVisibility(true);
    		mHudVisible = true;
    		updateState(mState);
	    }
    	mHudVisible = true;
    	
        notouchHandler.removeCallbacks(notouchCallback);
        notouchHandler.postDelayed(notouchCallback, NOTOUCH_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        notouchHandler.removeCallbacks(notouchCallback);
    }
		
    private void updateState(State state)
    {	
	if(mState == State.STATE_VISUALIZING && state == State.STATE_IDLE && mMapNodes > 100)
	    {
		mToast.makeText(getActivity(), String.format("Re-adding %d online clouds, this may take some time...", mMapNodes), mToast.LENGTH_LONG).show();
	    }
	mState = state;
	switch(state)
	    {
	    case STATE_PROCESSING:
		mButtonLighting.setVisibility(View.INVISIBLE);
		mButtonWireframe.setVisibility(View.INVISIBLE);
		mButtonCloseVisualization.setVisibility(View.INVISIBLE);
		mButtonSaveOnDevice.setVisibility(View.INVISIBLE);
		mButtonShareOnSketchfab.setVisibility(View.INVISIBLE);
		mItemSave.setEnabled(false);
		mItemExport.setEnabled(false);
		mItemOpen.setEnabled(false);
		mItemPostProcessing.setEnabled(false);
		mItemSettings.setEnabled(false);
		mItemReset.setEnabled(false);
		mItemModes.setEnabled(false);
		mButtonPause.setVisibility(View.INVISIBLE);
		break;
	    case STATE_VISUALIZING:
		mButtonLighting.setVisibility(mHudVisible && !mItemRenderingPointCloud.isChecked()?View.VISIBLE:View.INVISIBLE);
		mButtonWireframe.setVisibility(mHudVisible && !mItemRenderingPointCloud.isChecked()?View.VISIBLE:View.INVISIBLE);
		mButtonCloseVisualization.setVisibility(mHudVisible?View.VISIBLE:View.INVISIBLE);
		mButtonCloseVisualization.setEnabled(true);
		mButtonSaveOnDevice.setVisibility(mHudVisible?View.VISIBLE:View.INVISIBLE);
		mButtonShareOnSketchfab.setVisibility(mHudVisible?View.VISIBLE:View.INVISIBLE);
		mItemSave.setEnabled(mButtonPause.isChecked());
		mItemExport.setEnabled(mButtonPause.isChecked() && !mItemDataRecorderMode.isChecked());
		mItemOpen.setEnabled(false);
		mItemPostProcessing.setEnabled(false);
		mItemSettings.setEnabled(true);
		mItemReset.setEnabled(true);
		mItemModes.setEnabled(true);
		mButtonPause.setVisibility(View.INVISIBLE);
		mItemDataRecorderMode.setEnabled(mButtonPause.isChecked());
		break;
	    case STATE_VISUALIZING_WHILE_LOADING:
		mButtonLighting.setVisibility(mHudVisible && !mItemRenderingPointCloud.isChecked()?View.VISIBLE:View.INVISIBLE);
		mButtonWireframe.setVisibility(mHudVisible && !mItemRenderingPointCloud.isChecked()?View.VISIBLE:View.INVISIBLE);
		mButtonCloseVisualization.setVisibility(mHudVisible?View.VISIBLE:View.INVISIBLE);
		mButtonCloseVisualization.setEnabled(false);
		mButtonSaveOnDevice.setVisibility(View.INVISIBLE);
		mButtonShareOnSketchfab.setVisibility(View.INVISIBLE);
		mItemSave.setEnabled(false);
		mItemExport.setEnabled(false);
		mItemOpen.setEnabled(false);
		mItemPostProcessing.setEnabled(false);
		mItemSettings.setEnabled(false);
		mItemReset.setEnabled(false);
		mItemModes.setEnabled(false);
		mButtonPause.setVisibility(View.INVISIBLE);
		break;
	    default:
		mButtonLighting.setVisibility(View.INVISIBLE);
		mButtonWireframe.setVisibility(View.INVISIBLE);
		mButtonCloseVisualization.setVisibility(View.INVISIBLE);
		mButtonSaveOnDevice.setVisibility(View.INVISIBLE);
		mButtonShareOnSketchfab.setVisibility(View.INVISIBLE);
		mItemSave.setEnabled(mButtonPause.isChecked());
		mItemExport.setEnabled(mButtonPause.isChecked() && !mItemDataRecorderMode.isChecked());
		mItemOpen.setEnabled(mButtonPause.isChecked() && !mItemDataRecorderMode.isChecked());
		mItemPostProcessing.setEnabled(mButtonPause.isChecked() && !mItemDataRecorderMode.isChecked());
		mItemSettings.setEnabled(true);
		mItemReset.setEnabled(true);
		mItemModes.setEnabled(true);
		mButtonPause.setVisibility(mHudVisible?View.VISIBLE:View.INVISIBLE);
		mItemDataRecorderMode.setEnabled(mButtonPause.isChecked());
		break;
	    }
	mButtonCameraView.setVisibility(mHudVisible?View.VISIBLE:View.INVISIBLE);
	mButtonBackfaceShown.setVisibility(mHudVisible && (mItemRenderingMesh.isChecked() || mItemRenderingTextureMesh.isChecked())?View.VISIBLE:View.INVISIBLE);
	mSeekBarFov.setVisibility(mHudVisible && (mButtonCameraView.getSelectedItemPosition() == 0 || mButtonCameraView.getSelectedItemPosition() == 3)?View.VISIBLE:View.INVISIBLE);
	mSeekBarGrid.setVisibility(mHudVisible && mSeekBarGrid.isEnabled() && mButtonCameraView.getSelectedItemPosition() == 3?View.VISIBLE:View.INVISIBLE);
    }

    private void pauseMapping() {

	updateState(State.STATE_IDLE);

	if(mButtonPause.isChecked())
	    {
		RTABMapLib.setPausedMapping(true);
			
		mStatusTexts[0] = getString(R.string.status)+"Paused";
		mStatusTexts[1] = getString(R.string.memory)+String.valueOf(Debug.getNativeHeapAllocatedSize()/(1024*1024));
		mStatusTexts[2] = getString(R.string.free_memory)+String.valueOf(getFreeMemory());
		updateStatusTexts();
			
		mMapIsEmpty = false;
		mDateOnPause = new Date();

		long memoryFree = getFreeMemory();
		if(!mOnPause && !mItemLocalizationMode.isChecked() && !mItemDataRecorderMode.isChecked() && memoryFree >= 100 && mMapNodes>2)
		    {
			// Do standard post processing?
			new AlertDialog.Builder(getActivity())
			    .setTitle("Mapping Paused! Optimize Now?")
			    .setMessage("Do you want to do standard map optimization now? This can be also done later using \"Optimize\" menu.")
			    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
					standardOptimization();
				    }
				})
			    .setNegativeButton("No", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
					// do nothing...
				    }
				})
			    .show();
		    } 
	    }
	else
	    {
		if(mMemoryWarningDialog != null)
		    {
			mMemoryWarningDialog.dismiss();
			mMemoryWarningDialog=null;
		    }
		RTABMapLib.setPausedMapping(false);
			
		if(mItemDataRecorderMode.isChecked())
		    {
			mToast.makeText(getActivity(), String.format("Data Recorder Mode: no map is created, only raw data is recorded."), mToast.LENGTH_LONG).show();
		    }
		else if(!mMapIsEmpty)
		    {
			mToast.makeText(getActivity(), String.format("On resume, a new map is created. Tip: Try relocalizing in the previous area."), mToast.LENGTH_LONG).show();
		    }
	    }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
	resetNoTouchTimer();
	if(!DISABLE_LOG) Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
	int itemId = item.getItemId();
	if (itemId == R.id.post_processing_standard)
	    {
		standardOptimization();
	    }
	else if (itemId == R.id.detect_more_loop_closures)
	    {
		mProgressDialog.setTitle("Post-Processing");
		mProgressDialog.setMessage(String.format("Please wait while detecting more loop closures..."));
		mProgressDialog.show();
		updateState(State.STATE_PROCESSING);
		Thread workingThread = new Thread(new Runnable() {
			public void run() {
			    final int loopDetected = RTABMapLib.postProcessing(2);
			    runOnUiThread(new Runnable() {
				    public void run() {
					mProgressDialog.dismiss();
					if(loopDetected >= 0)
					    {
						mTotalLoopClosures+=loopDetected;
						mToast.makeText(getActivity(), String.format("Detection done! %d new loop closure(s) added.", loopDetected), mToast.LENGTH_SHORT).show();
					    }
					else if(loopDetected < 0)
					    {
						mToast.makeText(getActivity(), String.format("Detection failed!"), mToast.LENGTH_SHORT).show();
					    }
					updateState(State.STATE_IDLE);
				    }
				});
			} 
		    });
		workingThread.start();
	    }
	else if (itemId == R.id.global_graph_optimization)
	    {
		mProgressDialog.setTitle("Post-Processing");
		mProgressDialog.setMessage(String.format("Global graph optimization..."));
		mProgressDialog.show();
		updateState(State.STATE_PROCESSING);
		Thread workingThread = new Thread(new Runnable() {
			public void run() {
			    final int value = RTABMapLib.postProcessing(0);
			    runOnUiThread(new Runnable() {
				    public void run() {
					mProgressDialog.dismiss();
					if(value >= 0)
					    {
						mToast.makeText(getActivity(), String.format("Optimization done!"), mToast.LENGTH_SHORT).show();
					    }
					else if(value < 0)
					    {
						mToast.makeText(getActivity(), String.format("Optimization failed!"), mToast.LENGTH_SHORT).show();
					    }
					updateState(State.STATE_IDLE);
				    }
				});
			} 
		    });
		workingThread.start();
	    }
	else if (itemId == R.id.polygons_filtering)
	    {		
		mProgressDialog.setTitle("Post-Processing");
		mProgressDialog.setMessage(String.format("Noise filtering..."));
		mProgressDialog.show();
		RTABMapLib.postProcessing(4);
	    }
	else if (itemId == R.id.gain_compensation_fast)
	    {		
		mProgressDialog.setTitle("Post-Processing");
		mProgressDialog.setMessage(String.format("Adjusting Colors (Fast)..."));
		mProgressDialog.show();
		RTABMapLib.postProcessing(5);
	    }
	else if (itemId == R.id.gain_compensation_full)
	    {		
		mProgressDialog.setTitle("Post-Processing");
		mProgressDialog.setMessage(String.format("Adjusting Colors (Full)..."));
		mProgressDialog.show();
		RTABMapLib.postProcessing(6);
	    }
	else if (itemId == R.id.bilateral_filtering)
	    {		
		mProgressDialog.setTitle("Post-Processing");
		mProgressDialog.setMessage(String.format("Mesh smoothing..."));
		mProgressDialog.show();
		RTABMapLib.postProcessing(7);
	    }
	else if (itemId == R.id.sba)
	    {
		mProgressDialog.setTitle("Post-Processing");
		mProgressDialog.setMessage(String.format("Bundle adjustment..."));
		mProgressDialog.show();

		Thread workingThread = new Thread(new Runnable() {
			public void run() {
			    final int value = RTABMapLib.postProcessing(1);
			    runOnUiThread(new Runnable() {
				    public void run() {
					mProgressDialog.dismiss();
					if(value >= 0)
					    {
						mToast.makeText(getActivity(), String.format("Optimization done!"), mToast.LENGTH_SHORT).show();
					    }
					else if(value < 0)
					    {
						mToast.makeText(getActivity(), String.format("Optimization failed!"), mToast.LENGTH_SHORT).show();
					    }
				    }
				});
			} 
		    });
		workingThread.start();
	    }
	else if(itemId == R.id.status)
	    {
		item.setChecked(!item.isChecked());
		updateStatusTexts();
	    }
	else if(itemId == R.id.debug)
	    {
		item.setChecked(!item.isChecked());
		updateStatusTexts();
	    }
	else if(itemId == R.id.mesh || itemId == R.id.texture_mesh || itemId == R.id.point_cloud)
	    {
		item.setChecked(true);
		RTABMapLib.setMeshRendering(
					    mItemRenderingMesh.isChecked() || mItemRenderingTextureMesh.isChecked(), 
					    mItemRenderingTextureMesh.isChecked());
			
		resetNoTouchTimer();
			
		if(mState != State.STATE_VISUALIZING)
		    {
			// save preference
			int type = mItemRenderingPointCloud.isChecked()?0:mItemRenderingMesh.isChecked()?1:2;
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(getString(R.string.pref_key_rendering), type);
			// Commit the edits!
			editor.commit();
		    }
	    }
	else if(itemId == R.id.map_shown)
	    {
		item.setChecked(!item.isChecked());
		RTABMapLib.setMapCloudShown(item.isChecked());
	    }
	else if(itemId == R.id.odom_shown)
	    {
		item.setChecked(!item.isChecked());
		RTABMapLib.setOdomCloudShown(item.isChecked());
	    }
	else if(itemId == R.id.localization_mode)
	    {
		item.setChecked(!item.isChecked());
		RTABMapLib.setLocalizationMode(item.isChecked());
	    }
	else if(itemId == R.id.trajectory_mode)
	    {
		item.setChecked(!item.isChecked());
		RTABMapLib.setTrajectoryMode(item.isChecked());
		setCamera(item.isChecked()?2:1);
	    }
	else if(itemId == R.id.graph_optimization)
	    {
		item.setChecked(!item.isChecked());
		RTABMapLib.setGraphOptimization(item.isChecked());
	    }
	else if(itemId == R.id.graph_visible)
	    {
		item.setChecked(!item.isChecked());
		RTABMapLib.setGraphVisible(item.isChecked());
	    }
	else if(itemId == R.id.grid_visible)
	    {
		item.setChecked(!item.isChecked());
		mSeekBarGrid.setEnabled(item.isChecked());
		mSeekBarGrid.setVisibility(mHudVisible && mSeekBarGrid.isEnabled()&&mButtonCameraView.getSelectedItemPosition() == 3?View.VISIBLE:View.INVISIBLE);
		RTABMapLib.setGridVisible(item.isChecked());
	    }
	else if (itemId == R.id.save)
	    {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("RTAB-Map Database Name (*.db):");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT); 
		if(mOpenedDatabasePath.isEmpty())
		    {
			String timeStamp = new SimpleDateFormat("yyMMdd-HHmmss").format(mDateOnPause);
			input.setText(timeStamp);
		    }
		else
		    {
			File f = new File(mOpenedDatabasePath);
			String name = f.getName();
			input.setText(name.substring(0,name.lastIndexOf(".")));
		    }
		input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		input.setSelectAllOnFocus(true);
		input.selectAll();
		builder.setView(input);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			    final String fileName = input.getText().toString();  
			    dialog.dismiss();
			    if(!fileName.isEmpty())
				{
				    File newFile = new File(mWorkingDirectory + fileName + ".db");
				    if(newFile.exists())
					{
					    new AlertDialog.Builder(getActivity())
						.setTitle("File Already Exists")
						.setMessage("Do you want to overwrite the existing file?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							    saveDatabase(fileName);
							}
						    })
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							    dialog.dismiss();
							}
						    })
						.show();
					}
				    else
					{
					    saveDatabase(fileName);
					}
				}
			}
		    });
		AlertDialog alertToShow = builder.create();
		alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		alertToShow.show();
	    }
	else if(itemId == R.id.reset)
	    {
		mTotalLoopClosures = 0;
			
		int index = 4;
		mMapNodes = 0;
		mStatusTexts[index++] = getString(R.string.nodes)+0;
		mStatusTexts[index++] = getString(R.string.words)+0;
		mStatusTexts[index++] = getString(R.string.database_size)+0;
		mStatusTexts[index++] = getString(R.string.points)+0;
		mStatusTexts[index++] = getString(R.string.polygons)+0;
		mStatusTexts[index++] = getString(R.string.update_time)+0;
		mStatusTexts[index++] = getString(R.string.features)+0;
		mStatusTexts[index++] = getString(R.string.rehearsal)+0;
		mStatusTexts[index++] = getString(R.string.total_loop)+0;
		mStatusTexts[index++] = getString(R.string.inliers)+0;
		mStatusTexts[index++] = getString(R.string.hypothesis)+0;
		mStatusTexts[index++] = getString(R.string.fps)+0;
		updateStatusTexts();

		mOpenedDatabasePath = "";
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean databaseInMemory = sharedPref.getBoolean(getString(R.string.pref_key_db_in_memory), Boolean.parseBoolean(getString(R.string.pref_default_db_in_memory)));
		String tmpDatabase = mWorkingDirectory+RTABMAP_TMP_DB;
		(new File(tmpDatabase)).delete();
		RTABMapLib.openDatabase(tmpDatabase, databaseInMemory, false);
			
		mMapIsEmpty = true;
		mItemSave.setEnabled(false);
		mItemExport.setEnabled(false);
		mItemPostProcessing.setEnabled(false);
		updateState(State.STATE_IDLE);
	    }
	else if(itemId == R.id.data_recorder)
	    {
		final boolean dataRecorderOldState = item.isChecked();
		new AlertDialog.Builder(getActivity())
		    .setTitle("Data Recorder Mode")
		    .setMessage("Changing from/to data recorder mode will close the current session. Do you want to continue?")
		    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {           	  
				// reset
				mTotalLoopClosures = 0;
				int index = 4;
				mMapNodes = 0;
				mStatusTexts[index++] = getString(R.string.nodes)+0;
				mStatusTexts[index++] = getString(R.string.words)+0;
				mStatusTexts[index++] = getString(R.string.database_size)+0;
				mStatusTexts[index++] = getString(R.string.points)+0;
				mStatusTexts[index++] = getString(R.string.polygons)+0;
				mStatusTexts[index++] = getString(R.string.update_time)+0;
				mStatusTexts[index++] = getString(R.string.features)+0;
				mStatusTexts[index++] = getString(R.string.rehearsal)+0;
				mStatusTexts[index++] = getString(R.string.total_loop)+0;
				mStatusTexts[index++] = getString(R.string.inliers)+0;
				mStatusTexts[index++] = getString(R.string.hypothesis)+0;
				mStatusTexts[index++] = getString(R.string.fps)+0;
				updateStatusTexts();

				mItemDataRecorderMode.setChecked(!dataRecorderOldState);
				RTABMapLib.setDataRecorderMode(mItemDataRecorderMode.isChecked());

				mOpenedDatabasePath = "";
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
				boolean databaseInMemory = sharedPref.getBoolean(getString(R.string.pref_key_db_in_memory), Boolean.parseBoolean(getString(R.string.pref_default_db_in_memory)));
				String tmpDatabase = mWorkingDirectory+RTABMAP_TMP_DB;
				(new File(tmpDatabase)).delete();
				RTABMapLib.openDatabase(tmpDatabase, databaseInMemory, false);

				mItemOpen.setEnabled(!mItemDataRecorderMode.isChecked() && mButtonPause.isChecked());
				mItemPostProcessing.setEnabled(!mItemDataRecorderMode.isChecked() && mButtonPause.isChecked());
				mItemExport.setEnabled(!mItemDataRecorderMode.isChecked() && mButtonPause.isChecked());

				mItemLocalizationMode.setEnabled(!mItemDataRecorderMode.isChecked());		   

				if(mItemDataRecorderMode.isChecked())
				    {
					mToast.makeText(getActivity(), String.format("Data recorder mode activated! Tip: You can increase data update rate in Parameters menu under Mapping options."), mToast.LENGTH_LONG).show();
				    }
				else
				    {
					mToast.makeText(getActivity(), String.format("Data recorder mode deactivated!"), mToast.LENGTH_LONG).show();
				    }
			    }
			})
		    .setNegativeButton("No", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			    }
			})
		    .show();
	    }
	else if(itemId == R.id.export_point_cloud ||
		itemId == R.id.export_point_cloud_highrez)
	    {
		final boolean regenerateCloud = itemId == R.id.export_point_cloud_highrez;

		export(false, false, regenerateCloud, false, 0);
	    }
	else if(itemId == R.id.export_optimized_mesh ||
		itemId == R.id.export_optimized_mesh_texture)
	    {
		final boolean isOBJ = itemId == R.id.export_optimized_mesh_texture;
			
	        RelativeLayout linearLayout = new RelativeLayout(this);
	        final NumberPicker aNumberPicker = new NumberPicker(this);
	        aNumberPicker.setMaxValue(9);
	        aNumberPicker.setMinValue(0);
	        aNumberPicker.setWrapSelectorWheel(false);
	        aNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	        aNumberPicker.setFormatter(new NumberPicker.Formatter() {
			@Override
			public String format(int i) {
			    if(i==0)
				{
				    return "No Limit";
				}
			    return String.format("%d00 000", i);
			}
		    });
	        aNumberPicker.setValue(2);

	        // Fix to correctly show value on first render
	        try {
		    Method method = aNumberPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
		    method.setAccessible(true);
		    method.invoke(aNumberPicker, true);
	        } catch (NoSuchMethodException e) {
		    e.printStackTrace();
	        } catch (IllegalArgumentException e) {
		    e.printStackTrace();
	        } catch (IllegalAccessException e) {
		    e.printStackTrace();
	        } catch (InvocationTargetException e) {
		    e.printStackTrace();
	        }


	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
	        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

	        linearLayout.setLayoutParams(params);
	        linearLayout.addView(aNumberPicker,numPicerParams);

	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	        alertDialogBuilder.setTitle("Maximum polygons");
	        alertDialogBuilder.setView(linearLayout);
	        alertDialogBuilder
		    .setCancelable(false)
		    .setPositiveButton("Ok",
				       new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog,
							       int id) {
					       export(isOBJ, true, false, true, aNumberPicker.getValue()*100000);
					   }
				       })
		    .setNegativeButton("Cancel",
				       new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog,
							       int id) {
					       dialog.cancel();
					   }
				       });
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();
	    }
	else if(itemId == R.id.open)
	    {
		final String[] files = Util.loadFileList(mWorkingDirectory, true);
		if(files.length > 0)
		    {
			String[] filesWithSize = new String[files.length];
			for(int i = 0; i<filesWithSize.length; ++i)
			    {
				File filePath = new File(mWorkingDirectory+files[i]);
				long mb = filePath.length()/(1024*1024);
				filesWithSize[i] = files[i] + " ("+mb+" MB)";
			    }
				
			ArrayList<HashMap<String, String> > arrayList = new ArrayList<HashMap<String, String> >();
		        for (int i = 0; i < filesWithSize.length; i++) {
		            HashMap<String, String> hashMap = new HashMap<String, String>();//create a hashmap to store the data in key value pair
		            hashMap.put("name", filesWithSize[i]);
		            hashMap.put("path", mWorkingDirectory + files[i]);
		            arrayList.add(hashMap);//add the hashmap into arrayList
		        }
		        String[] from = {"name", "path"};//string array
		        int[] to = {R.id.textView, R.id.imageView};//int array of views id's
		        DatabaseListArrayAdapter simpleAdapter = new DatabaseListArrayAdapter(this, arrayList, R.layout.database_list, from, to);//Create object and set the parameters for simpleAdapter

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose Your File (*.db)");
			builder.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
				//builder.setItems(filesWithSize, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, final int which) {
												
				    // Adjust color now?
				    new AlertDialog.Builder(getActivity())
					.setTitle("Opening database...")
					.setMessage("Do you want to adjust colors now?\nThis can be done later under Optimize menu.")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichIn) {
						    openDatabase(files[which], true);
						}
					    })
					.setNeutralButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichIn) {
						    openDatabase(files[which], false);
						}
					    })
					.show();
				    return;
				}
			    });
			builder.show();
		    }   	
	    }
	else if(itemId == R.id.settings)
	    {
		Intent intent = new Intent(getActivity(), SettingsActivity.class);
		startActivity(intent);
		mBlockBack = true;
	    }
	else if(itemId == R.id.about)
	    {
		AboutDialog about = new AboutDialog(this);
		about.setTitle("About RTAB-Map");
		about.show();
	    }

	return true;
    }
	
    private void export(final boolean isOBJ, final boolean meshing, final boolean regenerateCloud, final boolean optimized, final int optimizedMaxPolygons)
    {
	final String extension = isOBJ? ".obj" : ".ply";
		
	// get Export settings
	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	final String cloudVoxelSizeStr = sharedPref.getString(getString(R.string.pref_key_cloud_voxel), getString(R.string.pref_default_cloud_voxel));
	final float cloudVoxelSize = Float.parseFloat(cloudVoxelSizeStr);
	final int textureSize = isOBJ?Integer.parseInt(sharedPref.getString(getString(R.string.pref_key_texture_size), getString(R.string.pref_default_texture_size))):0;
	final int textureCount = Integer.parseInt(sharedPref.getString(getString(R.string.pref_key_texture_count), getString(R.string.pref_default_texture_count)));
	final int normalK = Integer.parseInt(sharedPref.getString(getString(R.string.pref_key_normal_k), getString(R.string.pref_default_normal_k)));
	final float maxTextureDistance = Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_max_texture_distance), getString(R.string.pref_default_max_texture_distance)));
	final int minTextureClusterSize = Integer.parseInt(sharedPref.getString(getString(R.string.pref_key_min_texture_cluster_size), getString(R.string.pref_default_min_texture_cluster_size)));
	final float optimizedVoxelSize = cloudVoxelSize;
	final int optimizedDepth = Integer.parseInt(sharedPref.getString(getString(R.string.pref_key_opt_depth), getString(R.string.pref_default_opt_depth)));
	final float optimizedColorRadius = Float.parseFloat(sharedPref.getString(getString(R.string.pref_key_opt_color_radius), getString(R.string.pref_default_opt_color_radius)));
	final boolean optimizedCleanWhitePolygons = sharedPref.getBoolean(getString(R.string.pref_key_opt_clean_white), Boolean.parseBoolean(getString(R.string.pref_default_opt_clean_white)));
	final boolean optimizedColorWhitePolygons = false;//sharedPref.getBoolean("pref_key_opt_color_white", false); // not used
	final boolean blockRendering = sharedPref.getBoolean(getString(R.string.pref_key_block_render), Boolean.parseBoolean(getString(R.string.pref_default_block_render)));

		
	mExportProgressDialog.setTitle("Exporting");
	mExportProgressDialog.setMessage(String.format("Please wait while preparing data to export..."));
	mExportProgressDialog.setProgress(0);
		
	final State previousState = mState;
		
	mExportProgressDialog.show();
	updateState(State.STATE_PROCESSING);
				
	Thread exportThread = new Thread(new Runnable() {
		public void run() {

		    final long startTime = System.currentTimeMillis()/1000;

		    final boolean success = RTABMapLib.exportMesh(
								  cloudVoxelSize,
								  regenerateCloud,
								  meshing,
								  textureSize,
								  textureCount,
								  normalK,
								  optimized,
								  optimizedVoxelSize,
								  optimizedDepth,
								  optimizedMaxPolygons,
								  optimizedColorRadius,
								  optimizedCleanWhitePolygons,
								  optimizedColorWhitePolygons,
								  maxTextureDistance,
								  minTextureClusterSize,
								  blockRendering);
		    runOnUiThread(new Runnable() {
			    public void run() {
				if(mExportProgressDialog.isShowing())
				    {
					if(success)
					    {
						if(!meshing && cloudVoxelSize>0.0f)
						    {
							mToast.makeText(getActivity(), String.format("Cloud assembled and voxelized at %s m.", cloudVoxelSizeStr), mToast.LENGTH_LONG).show();
						    }
								
						final long endTime = System.currentTimeMillis()/1000;

						// Visualize the result?
						AlertDialog d = new AlertDialog.Builder(getActivity())
						    .setCancelable(false)
						    .setTitle("Export Successful! (" + (endTime-startTime) + " sec)")
						    .setMessage(Html.fromHtml("Do you want visualize the result before saving to file or sharing to <a href=\"https://sketchfab.com/about\">Sketchfab</a>?"))
						    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							    public void onClick(DialogInterface dialog, int which) {
								resetNoTouchTimer();
								mSavedRenderingType = mItemRenderingPointCloud.isChecked()?0:mItemRenderingMesh.isChecked()?1:2;
								if(!meshing)
								    {
									mItemRenderingPointCloud.setChecked(true);
								    }
								else if(!isOBJ)
								    {
									mItemRenderingMesh.setChecked(true);
								    }
								else // isOBJ
								    {
									mItemRenderingTextureMesh.setChecked(true);
								    }
								if(!optimizedCleanWhitePolygons)
								    {
									mButtonLighting.setChecked(true);
									RTABMapLib.setLighting(true);
								    }
								updateState(State.STATE_VISUALIZING);
								RTABMapLib.postExportation(true);
								if(mButtonCameraView.getSelectedItemPosition() == 0)
								    {
									setCamera(2);
								    }
							    }
							})
						    .setNegativeButton("No", new DialogInterface.OnClickListener() {
							    public void onClick(DialogInterface dialog, int which) {
								updateState(State.STATE_IDLE);
								RTABMapLib.postExportation(false);
										
								AlertDialog d2 = new AlertDialog.Builder(getActivity())
								    .setCancelable(false)
								    .setTitle("Save to...")
								    .setMessage(Html.fromHtml("Do you want to share to <a href=\"https://sketchfab.com/about\">Sketchfab</a> or save it on device?"))
								    .setPositiveButton("Share to Sketchfab", new DialogInterface.OnClickListener() {
									    public void onClick(DialogInterface dialog, int which) {
										shareToSketchfab();
									    }
									})
								    .setNegativeButton("Save on device", new DialogInterface.OnClickListener() {
									    public void onClick(DialogInterface dialog, int which) {
										saveOnDevice();
									    }
									})
								    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
									    public void onClick(DialogInterface dialog, int which) {
									    }
									})
								    .create();
								d2.show();
								// Make the textview clickable. Must be called after show()
								((TextView)d2.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
							    }
							})
						    .create();
						d.show();
						// Make the textview clickable. Must be called after show()
						((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
					    }
					else
					    {
						updateState(previousState);
						mToast.makeText(getActivity(), String.format("Exporting map failed!"), mToast.LENGTH_LONG).show();
					    }
					mExportProgressDialog.dismiss();
				    }
				else
				    {
					mToast.makeText(getActivity(), String.format("Export canceled"), mToast.LENGTH_LONG).show();
					updateState(previousState);
				    }
			    }
			});
		} 
	    });
	exportThread.start();
    }
	
    private void saveDatabase(String fileName)
    {
	final String newDatabasePath = mWorkingDirectory + fileName + ".db";
	final String newDatabasePathHuman = mWorkingDirectoryHuman + fileName + ".db";
	mProgressDialog.setTitle("Saving");
	if(mOpenedDatabasePath.equals(newDatabasePath))
	    {
		mProgressDialog.setMessage(String.format("Please wait while updating \"%s\"...", newDatabasePathHuman));
	    }
	else
	    {
		mProgressDialog.setMessage(String.format("Please wait while saving \"%s\"...", newDatabasePathHuman));
	    }
	mProgressDialog.show();
	final State previousState = mState;
	updateState(State.STATE_PROCESSING);
	Thread saveThread = new Thread(new Runnable() {
		public void run() {
		    RTABMapLib.save(newDatabasePath); // save
		    runOnUiThread(new Runnable() {
			    public void run() {
				if(mOpenedDatabasePath.equals(newDatabasePath))
				    {
					mToast.makeText(getActivity(), String.format("Database \"%s\" updated.", newDatabasePathHuman), mToast.LENGTH_LONG).show();
				    }
				else
				    {
					mToast.makeText(getActivity(), String.format("Database saved to \"%s\".", newDatabasePathHuman), mToast.LENGTH_LONG).show();

					Intent intent = new Intent(getActivity(), RTABMapActivity.class);
					// use System.currentTimeMillis() to have a unique ID for the pending intent
					PendingIntent pIntent = PendingIntent.getActivity(getActivity(), (int) System.currentTimeMillis(), intent, 0);

					// build notification
					// the addAction re-use the same intent to keep the example short
					Notification n  = new Notification.Builder(getActivity())
					    .setContentTitle(getString(R.string.app_name))
					    .setContentText(newDatabasePathHuman + " saved!")
					    .setSmallIcon(R.drawable.ic_launcher)
					    .setContentIntent(pIntent)
					    .setAutoCancel(true).build();


					NotificationManager notificationManager = 
					    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

					notificationManager.notify(0, n); 
				    }
				if(!mItemDataRecorderMode.isChecked())
				    {
					mOpenedDatabasePath = newDatabasePath;
				    }
				mProgressDialog.dismiss();
				updateState(previousState);
			    }
			});
		} 
	    });
	saveThread.start();
    }
	
    private void saveOnDevice()
    {
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Model Name (will start with SiloamSee-):");
	final EditText input = new EditText(this);
	input.setInputType(InputType.TYPE_CLASS_TEXT);        
	builder.setView(input);
	if(mOpenedDatabasePath.isEmpty())
	    {
		String timeStamp = new SimpleDateFormat("yyMMdd-HHmmss").format(mDateOnPause);
		input.setText(timeStamp);
	    }
	else
	    {
		File f = new File(mOpenedDatabasePath);
		String name = f.getName();
		input.setText(name.substring(0,name.lastIndexOf(".")));
	    }
	input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
	input.setSelectAllOnFocus(true);
	input.selectAll();
	builder.setCancelable(false);
	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
		    dialog.dismiss();
		}
	    });
	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
		    // SiloamSee- is added as a prefix to the final filename whatever happens
		    final String fileName = "SiloamSee-" + input.getText().toString();    
		    dialog.dismiss();
		    if(!fileName.isEmpty())
			{
			    File newFile = new File(mWorkingDirectory + RTABMAP_EXPORT_DIR + fileName + ".zip");
			    if(newFile.exists())
				{
				    new AlertDialog.Builder(getActivity())
					.setTitle("File Already Exists")
					.setMessage("Do you want to overwrite the existing file?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						    writeExportedFiles(fileName);
						}
					    })
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						    saveOnDevice();
						}
					    })
					.show();
				}
			    else
				{
				    writeExportedFiles(fileName);
				}
			}
		}
	    });
	AlertDialog alertToShow = builder.create();
	alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	alertToShow.show();
    }
    
    /*
     * writeExportedFiles(final String fileName)
     *
     * Presumably writes the OBJ/PLY to a file on the sd-card
     * By sd-card it of course means internal storage, not an external
     * micro sdxc card. This could be problematic for the future,
     * depends how many databases/OBJs a single Siloam mapping run takes
     * 
     * Also why are tab widths 8. Who does that. Mathieu et al. do that's
     * who
     *
     * "fileName" is carried over from saveOnDevice() above. 
     *
     */
	
    private void writeExportedFiles(final String fileName)
    {		
	Log.i(TAG, String.format("Write exported mesh to \"%s\"", fileName));
		
	mProgressDialog.setTitle("Saving to sd-card");
	mProgressDialog.setMessage(String.format("Compressing the files..."));
	mProgressDialog.show();
		
	Thread workingThread = new Thread(new Runnable() {
		public void run() {
		    boolean success = false;
				
		    File tmpDir = new File(mWorkingDirectory + RTABMAP_TMP_DIR);
		    tmpDir.mkdirs();
		    String[] fileNames = Util.loadFileList(mWorkingDirectory + RTABMAP_TMP_DIR, false);
		    if(!DISABLE_LOG) Log.i(TAG, String.format("Deleting %d files in \"%s\"", fileNames.length, mWorkingDirectory + RTABMAP_TMP_DIR));
		    for(int i=0; i<fileNames.length; ++i)
			{
			    File f = new File(mWorkingDirectory + RTABMAP_TMP_DIR + "/" + fileNames[i]);
			    if(f.delete())
				{
				    if(!DISABLE_LOG) Log.i(TAG, String.format("Deleted \"%s\"", f.getPath()));
				}
			    else
				{
				    if(!DISABLE_LOG) Log.i(TAG, String.format("Failed deleting \"%s\"", f.getPath()));
				}
			}
		    File exportDir = new File(mWorkingDirectory + RTABMAP_EXPORT_DIR);
		    exportDir.mkdirs();
				
		    final String pathHuman = mWorkingDirectoryHuman + RTABMAP_EXPORT_DIR + fileName + ".zip";
		    if(RTABMapLib.writeExportedMesh(mWorkingDirectory + RTABMAP_TMP_DIR, RTABMAP_TMP_FILENAME))
			{
			    final String zipOutput = mWorkingDirectory+RTABMAP_EXPORT_DIR+fileName+".zip";
							
			    fileNames = Util.loadFileList(mWorkingDirectory + RTABMAP_TMP_DIR, false);
			    if(fileNames.length > 0)
				{
				    String[] filesToZip = new String[fileNames.length];
				    for(int i=0; i<fileNames.length; ++i)
					{
					    filesToZip[i] = mWorkingDirectory + RTABMAP_TMP_DIR + "/" + fileNames[i];
					}
			
				    File toZIPFile = new File(zipOutput);
				    toZIPFile.delete();			
						
				    try
					{
					    Util.zip(filesToZip, zipOutput);
					    success = true;
					}
				    catch(IOException e)
					{
					    final String msg = e.getMessage();
					    runOnUiThread(new Runnable() {
						    public void run() {
							mToast.makeText(getActivity(), String.format("Exporting mesh \"%s\" failed! Error=%s", pathHuman, msg), mToast.LENGTH_LONG).show();
						    }
						});
					}
				}
			}
				
		    if(success)
			{
			    runOnUiThread(new Runnable() {
				    public void run() {
					mProgressDialog.dismiss();
					mToast.makeText(getActivity(), String.format("Mesh \"%s\" successfully exported!", pathHuman), mToast.LENGTH_LONG).show();
					Intent intent = new Intent(getActivity(), RTABMapActivity.class);
					// use System.currentTimeMillis() to have a unique ID for the pending intent
					PendingIntent pIntent = PendingIntent.getActivity(getActivity(), (int) System.currentTimeMillis(), intent, 0);
				
					// build notification
					// the addAction re-use the same intent to keep the example short
					Notification n  = new Notification.Builder(getActivity())
					    .setContentTitle(getString(R.string.app_name))
					    .setContentText(pathHuman + " exported!")
					    .setSmallIcon(R.drawable.ic_launcher)
					    .setContentIntent(pIntent)
					    .setAutoCancel(true).build();
				
				
					NotificationManager notificationManager = 
					    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				
					notificationManager.notify(0, n); 
				    }
				});
			}
		    else
			{
			    runOnUiThread(new Runnable() {
				    public void run() {
					mProgressDialog.dismiss();
					mToast.makeText(getActivity(), String.format("Exporting mesh \"%s\" failed! No files found in tmp directory!? Last export may have failed or have been canceled.", pathHuman), mToast.LENGTH_LONG).show();
				    }
				});
			}
		}
	    });
	workingThread.start();
    }
	
    private void openDatabase(final String fileName, final boolean optimize)
    {
	mOpenedDatabasePath = mWorkingDirectory + fileName;
		
	Log.i(TAG, "Open database " + mOpenedDatabasePath);
		
	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	final boolean databaseInMemory = sharedPref.getBoolean(getString(R.string.pref_key_db_in_memory), Boolean.parseBoolean(getString(R.string.pref_default_db_in_memory)));		
		
		
	mProgressDialog.setTitle("Loading");
	mProgressDialog.setMessage(String.format("Opening database \"%s\"...", fileName));
	mProgressDialog.show();
	updateState(State.STATE_PROCESSING);
		
	Thread openThread = new Thread(new Runnable() {
		public void run() {
			
		    final String tmpDatabase = mWorkingDirectory+RTABMAP_TMP_DB;				
		    final int status = RTABMapLib.openDatabase2(mOpenedDatabasePath, tmpDatabase, databaseInMemory, optimize);
				
		    runOnUiThread(new Runnable() {
			    public void run() {
				if(status == -1)
				    {
					updateState(State.STATE_IDLE);
					mProgressDialog.dismiss();
					new AlertDialog.Builder(getActivity())
					    .setCancelable(false)
					    .setTitle("Error")
					    .setMessage("The map is loaded but optimization of the map's graph has "
							+ "failed, so the map cannot be shown. Change the Graph Optimizer approach used"
							+ " or enable/disable if the graph is optimized from graph "
							+ "end in \"Settings -> Mapping...\" and try opening again.")
					    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(getActivity(), SettingsActivity.class);
							startActivity(intent);
							mBlockBack = true;
						    }
						})
					    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int which) {
						    }
						})
					    .show();
				    }
				else if(status == -2)
				    {
					updateState(State.STATE_IDLE);
					mProgressDialog.dismiss();
					new AlertDialog.Builder(getActivity())
					    .setCancelable(false)
					    .setTitle("Error")
					    .setMessage("Failed to open database: Out of memory! Try "
							+ "again after lowering Point Cloud Density in Settings.")
					    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(getActivity(), SettingsActivity.class);
							startActivity(intent);
							mBlockBack = true;
						    }
						})
					    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int which) {
						    }
						})
					    .show();
				    }
				else
				    {
					if(status >= 1 && status<=3)
					    {
						mProgressDialog.dismiss();
						resetNoTouchTimer();
						updateState(State.STATE_VISUALIZING);
						mToast.makeText(getActivity(), String.format("Database loaded!"), mToast.LENGTH_LONG).show();
					    }
					else if(!mItemTrajectoryMode.isChecked())
					    {
						if(mButtonCameraView.getSelectedItemPosition() == 0)
						    {
							setCamera(2);
						    }
						// creating meshes...
						updateState(State.STATE_IDLE);
						mProgressDialog.setTitle("Loading");
						mProgressDialog.setMessage(String.format("Database \"%s\" loaded. Please wait while rendering point clouds and meshes...", fileName));
					    }
							
				    }
			    }
			});
		} 
	    });
	openThread.start();	
    }
	
    public void copy(File src, File dst) throws IOException {
	InputStream in = new FileInputStream(src);
	OutputStream out = new FileOutputStream(dst);

	// Transfer bytes from in to out
	byte[] buf = new byte[1024];
	int len;
	while ((len = in.read(buf)) > 0) {
	    out.write(buf, 0, len);
	}
	in.close();
	out.close();
    }
		
    private void shareToSketchfab()
    {
	Intent intent = new Intent(getActivity(), SketchfabActivity.class);
		
	intent.putExtra(RTABMAP_AUTH_TOKEN_KEY, mAuthToken);
	intent.putExtra(RTABMAP_WORKING_DIR_KEY, mWorkingDirectory);
		
	if(mOpenedDatabasePath.isEmpty())
	    {
		intent.putExtra(RTABMAP_FILENAME_KEY, new SimpleDateFormat("yyMMdd-HHmmss").format(mDateOnPause));
	    }
	else
	    {
		File f = new File(mOpenedDatabasePath);
		String name = f.getName();
		intent.putExtra(RTABMAP_FILENAME_KEY, name.substring(0,name.lastIndexOf(".")));
	    }
		
	startActivityForResult(intent, SKETCHFAB_ACTIVITY_CODE);
	mBlockBack = true;
    }
}
