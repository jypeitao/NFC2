Near Field Communications overview
==================================
* Close-range radio communication - 4 cm effective range
* Effective for small bursts of data
* Comes in 'active' and 'passive' forms, typically as in an active mobile device and a passive (sticker) tag  
* Expected smart-phone penetration of 50% by 2013

Workshop targets
=========================
* Learn some NDEF format basics
* Write some messages to an NFC tag
* Read those messages into an Android device with NFC support
* Send NDEF messages directly between device to device 

Requirements - bring this
=========================
* A computer (PC, Mac or Linux)
* An NFC-enabled device (Android 4.0 or higher)
* An USB cable to connect computer and device
* Eclipe with Android SDK installed, see below.

Installation - do this before you arrive
========================================
1. Get JDK 1.6 or later
2. Install [Eclipse Classic](http://www.eclipse.org/downloads/packages/eclipse-classic-42/junor) 3.7.2 (Indigo) or later and launch it
3. From update site https://dl-ssl.google.com/android/eclipse/ install 'Developer Tools' and restart
4. An Android wizard should appear automatically, run through it. Otherwise go to next step.
5. Open Window->Android SDK Manager and install all of the items under 'Tools' and 'Android 4.1 (API 16)' and also 'Google USB Driver' under 'Extras'
6. Install [NFC plugin](http://nfc-eclipse-plugin.googlecode.com) from update site http://nfc-eclipse-plugin.googlecode.com/git/nfc-eclipse-plugin-feature/update-site/ 
7. Install Android application [NFC Developer](https://play.google.com/store/apps/details?id=com.antares.nfc) from Android Play.
8. Install GIT client from Eclipse update repo and check out this (https://github.com/skjolber/Fagmote.git) Git repository. Alternatively, [download zip](https://github.com/skjolber/Fagmote/downloads).

Task 1 - Create new tag
=========================
### a. Open project
Import project 'HelloWorldNFC Base'. 

### b. Create new NDEF file
Create a new file in the root of the project using New -> Other -> Near Field Communications -> NDEF File.

Hint: NDEF is a data format used in tags.
### c. Create an Android Application Record
1. Read online documentation about [Android Application Records](http://developer.android.com/guide/topics/nfc/nfc.html#aar).
2. Create an Android Application Record with package name 'no.java.schedule' using the NDEF editor.

Hint: Open NDEF file and right-click on the table.
### d. Write the Android Application Record to a tag.
Start the 'NFC Developer' application:

1. Scan the QR code generated by the NDEF editor. 
2. Write by holding a tag to the back of the device.

Hint: If nothing happens, make sure you are holding the tag in the center of the backside of the phone (NFC range is only 4 cm ;-))
Hint: If get tag write IOException, try scanning again a bit slower.
### e. Try out the newly created tag
Close 'NFC Developer' application and navigate to the home screen. Make sure your Android device is online. What happens when you scan the tag?

Hint: If you do not already have an application with identifier '[no.java.schedule](https://play.google.com/store/search?q=no.java.schedule&c=apps)' installed, Android will search for it at [Google Play](https://play.google.com/store).

Task 2 - Hello NFC tag
===========================
### a. Launch hello world application
Connect Android device using USB cable and enable developer mode in settings:
* USB debugging
* Remain awake when charging (optional)

Launch hello world application via right-clicking on project and 'Run As -> Android application'. Show view 'Android->LogCat' in Eclipse and verify that messages appear.

### b. Change Hello World text by scanning a tag
We want to receieve NFC messages when our application is showing on the screen. 

1. Uncomment NFC [permissions](http://developer.android.com/guide/topics/nfc/nfc.html#manifest) in AndroidMainfest.xml:

		<!-- Near field communications permissions -->
        <uses-permission android:name="android.permission.NFC" />
		<uses-feature android:name="android.hardware.nfc" android:required="true" />

2. Initialize NFC [foreground mode](http://developer.android.com/guide/topics/nfc/advanced-nfc.html#foreground-dispatch) in the Hello World activity:

		@Override
		public void onCreate(Bundle savedInstanceState) {
        		super.onCreate(savedInstanceState);
        		setContentView(R.layout.main);
		
				// initialize NFC
        		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        		nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	    	}

	
3. Call enable/disable foreground mode from onResume() and onPause() in the Hello World activity:

		public void enableForegroundMode() {
		    	Log.d(TAG, "enableForegroundMode");
		
				// foreground mode gives the current active application priority for reading scanned tags
        		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        		IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
		}
	
		public void disableForegroundMode() {
			Log.d(TAG, "disableForegroundMode");
		
			nfcAdapter.disableForegroundDispatch(this);
		}

4. Change title to 'Hello NFC!' when a tag is scanned:

    	@Override
    	public void onNewIntent(Intent intent) { // this method is called when an NFC tag is scanned
		Log.d(TAG, "onNewIntent");

			// check for NFC related actions
        	if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
        		TextView textView = (TextView) findViewById(R.id.title);
        		textView.setText("Hello NFC!");
        	} else {
        		// ignore
        	}
    	}

Verify functionality: First start the application. Then scan the tag and see what happens.
### c. Read tag payload
Check for [NDEF](http://developer.android.com/guide/topics/nfc/nfc.html) messages in method onNewIntent(..) using 

		Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (messages != null) {	
   			Log.d(TAG, "Found " + messages.length + " NDEF messages");

			vibrate(); // signal found messages :-)
		}

Verify that messages appear in the log.
### d. Parse tag payload using [NDEF Tools for Android](http://code.google.com/p/ndef-tools-for-android/)
Parse messages from previous task into Records using

		// parse to records
		for (int i = 0; i < messages.length; i++) {
			try {
				List<Record> records = new Message((NdefMessage)messages[i]);
						
				Log.d(TAG, "Found " + records.size() + " records in message " + i);
						
				for(int k = 0; k < records.size(); k++) {
					Log.d(TAG, " Record #" + k + " is of class " + records.get(k).getClass().getSimpleName());
				}
			} catch (Exception e) {
				Log.e(TAG, "Problem parsing message", e);
			}
		
		}

Verify that the expected messages appear in the log.
### e. Determine which NDEF record types are present on the tag.
Iterate of the parsed records and investigate their type and contents.

Did your tag contain the expected content?

Task 3 - device to device communication: Android Beam
=====================================================
Use [Android Beam](http://developer.android.com/guide/topics/nfc/nfc.html#p2p) to exchange information between two devices.

### a. Register push message callback interface 
Read about the [NfcAdapter.CreateNdefMessageCallback](http://developer.android.com/reference/android/nfc/NfcAdapter.CreateNdefMessageCallback.html) interface and register callback in the activity 'onCreate()' method using 

        // Register Android Beam callback
        nfcAdapter.setNdefPushMessageCallback(this, this);

### b. Write push message  
Implement method 'createNdefMessage(..) - compose the NdefMessage to be pushed:

		// create record to be pushed
		TextRecord record = new TextRecord("This is my text record");
				
		// encode one or more record to NdefMessage
		return new NdefMessage(record.getNdefRecord());

### c. Read push message
Hold two devices together and see what happens. When a is message pushed from one NFC device to another?

### d. Post push message notification
Use the [NfcAdapter.OnNdefPushCompleteCallback](http://developer.android.com/reference/android/nfc/NfcAdapter.OnNdefPushCompleteCallback.html) interface to show a notification when a message is pushed using Android Beam.
First register callback in 'onCreate(..)'

        // Register callback to listen for message-sent success 
        nfcAdapter.setOnNdefPushCompleteCallback(this, this);

Then implement the 'onNdefPushComplete(..)' method:
    
        // Handle push success 
	    @Override
    	public void onNdefPushComplete(NfcEvent arg0) {
        	// A handler is needed to send messages to the activity when this
        	// callback occurs, because it happens from a binder thread
        	mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	    }

	    private static final int MESSAGE_SENT = 1;
	     
    	/** This handler receives a message from onNdefPushComplete */
    	private final Handler mHandler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
            	switch (msg.what) {
            	case MESSAGE_SENT:
                	Toast.makeText(getApplicationContext(), "Message beamed!", Toast.LENGTH_LONG).show();
               	 break;
            	}
        	}
    	};

Verify that everything is working by holding two phones together. Reflect over the user-friendliness of transferring a lot of data between two devices this way - what would be a more practical approach in such a scenario?

Bonus task - more NDEF record types
===================================
### a. How would you make NFC support optional?
Not all devices have NFC chips (yet)
### b. What are the tag classes and their storage capactiy?
What are the disadvantage of larger storage capacities?
### c. Create more record types
1. Create well-known URI record.
2. Create Mime Record.
3. Create External type Record.
4. Create Android Application Record with an Mime Record.
Are you able to start the application and also read the Mime Record payload?


